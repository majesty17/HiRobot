package com.majesty.hiredbag;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import com.majesty.hiredbag.model.Ids;
import com.majesty.hiredbag.model.MessageType;
import com.majesty.hiredbag.utils.LogUtils;
import com.majesty.hiredbag.utils.NodeUtils;

/**
 * Created by pengwei on 16/2/2.
 */
public class RobotService extends AccessibilityService {

    // 聊天界面
    private static final String CHAT_CLASS_NAME = "com.baidu.hi.activities.Chat";
    // 红包界面
    private static final String LUCKY_MONEY_CLASS_NAME = "com.baidu.hi.luckymoney.LuckyMoneyActivity";
    // 聊天列表
    private static final String CONTACT_CLASS_NAME = "com.baidu.hi.activities.Contact";

    // 上一个聊天记录
    private List<AccessibilityNodeInfo> prevFetchList = new ArrayList<AccessibilityNodeInfo>();
    // 上一个记录红包个数
    private int prevPackageCount = 0;
    // 当前activity的className
    private String currentClassName = getClass().toString();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    /***
     * 这里监听三种类型的事件 主要逻辑: 
     * 1,窗口内容变化 如果类名为聊天窗，则从聊天窗口获取红包 
     * 2,打开弹窗，菜单，对话框事件 如果是红包窗，则试图打开
     */

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        LogUtils.e("has an event: "+event.toString());
        switch (eventType) {
        case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
            if (currentClassName.equals(CHAT_CLASS_NAME)) {
                getPacket();
            }
            break;
        case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            currentClassName = event.getClassName().toString();
            if (currentClassName.equals(LUCKY_MONEY_CLASS_NAME)) {
                openPacketDetail();
            }
            break;
        default:
            break;
        }
    }



    /**
     * 打开红包
     *  1.能抢，点击拆红包并退出页面
     *  2.完成 
     *  2.1 没抢到，退出页面
     *  2.2 抢过的，退出页面
     */
    private void openPacketDetail() {
        final AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            // 拆红包
            AccessibilityNodeInfo openLucky = NodeUtils.findNodeById(nodeInfo, Ids.ENVELOPE_OPEN);
            if (openLucky != null) {
                performGlobalAction(GLOBAL_ACTION_BACK);
                openLucky.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                // 关闭页面
                AccessibilityNodeInfo closeNode = null;
                if ((closeNode = NodeUtils.findNodeById(nodeInfo, Ids.CLOSE_BTN)) != null
                        || (closeNode = NodeUtils.findNodeById(nodeInfo, Ids.BTN_CLOSE)) != null) {
                    closeNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }



    /**
     * 获取红包列表
     */
    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        AccessibilityNodeInfo listNode = NodeUtils.findNodeById(rootNode, Ids.CHAT_LIST_VIEW);
        int packageCount = rootNode.findAccessibilityNodeInfosByViewId(Ids.LUCKY_MONEY_TITLE).size();
        if (rootNode == null || listNode == null || listNode.getChildCount() == 0) {
            return;
        } else if (packageCount != prevPackageCount) {
            // 页面中红包个数不相等
            openPacket(rootNode);
            prevPackageCount = packageCount;
        } else {
            if (prevFetchList.size() == 0) {
                LogUtils.e("*1");
                openPacket(rootNode);
            } else {
                if (prevFetchList.size() != listNode.getChildCount()) {
                    if (prevFetchList.size() == listNode.getChildCount() - 1 && MessageType
                            .getMsgType(listNode.getChild(listNode.getChildCount() - 1)) != MessageType.MSG_PACKAGE) {
                        // 新增一条记录而且不是红包就不抢
                        LogUtils.e("*8");
                    } else {
                        openPacket(rootNode);
                        LogUtils.e("*2");
                    }
                } else {
                    if (listNode.getChildCount() == 1) {
                        if (!NodeUtils.isSame(listNode.getChild(0), prevFetchList.get(0))) {
                            openPacket(rootNode);
                            LogUtils.e("*3");
                        } else {
                            LogUtils.e("*7");
                        }
                    } else {
                        int last = listNode.getChildCount() - 1;
                        if (!NodeUtils.isSame(listNode.getChild(0), prevFetchList.get(0))
                                || !NodeUtils.isSame(listNode.getChild(last), prevFetchList.get(last))) {
                            openPacket(rootNode);
                            LogUtils.e("*4");
                        } else {
                            LogUtils.e("*5");
                        }
                    }
                }
            }
            prevFetchList.clear();
            for (int i = 0; i < listNode.getChildCount(); ++i) {
                prevFetchList.add(listNode.getChild(i));
            }
        }
        // 顶部消息通知
        AccessibilityNodeInfo notifyText = NodeUtils.findNodeById(rootNode, Ids.MSG_NOTIFY_INFO);
        if (notifyText != null && notifyText.getText().toString().contains("[百度红包]")) {
            notifyText.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    /**
     * 打开红包
     *
     * @param rootNode
     */
    private void openPacket(AccessibilityNodeInfo rootNode) {
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(Ids.LUCKY_MONEY_TITLE);
        // 倒顺，先抢最新的红包
        for (int i = nodes.size() - 1; i >= 0; i--) {
            recycle(nodes.get(i));
        }
    }

    public void recycle(AccessibilityNodeInfo info) {
        if (info != null) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            AccessibilityNodeInfo parent = info.getParent();
            while (parent != null) {
                if (parent.isClickable()) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
                parent = parent.getParent();
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
