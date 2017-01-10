package com.majesty.hiredbag.utils;

import android.view.accessibility.AccessibilityNodeInfo;



import java.util.List;

import com.majesty.hiredbag.model.Ids;
import com.majesty.hiredbag.model.MessageType;

/**
 * Created by pengwei on 16/2/4.
 */
public class NodeUtils {

	/**
	 * 根据id判断是否存在
	 *
	 * @param rootNode
	 * @param viewId
	 * @return
	 */
	public static boolean exist(AccessibilityNodeInfo rootNode, String viewId) {
		if (rootNode == null || rootNode.getChildCount() == 0 || viewId == null) {
			return false;
		}
		List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(viewId);
		return list != null && list.size() > 0;
	}

	/**
	 * 两个结点内容是否一致
	 *
	 * @param node1
	 * @param node2
	 * @return
	 */
	public static boolean isSame(AccessibilityNodeInfo node1, AccessibilityNodeInfo node2) {
		if (node1 == null || node2 == null) {
			return false;
		}
		if (MessageType.getMsgType(node1) == MessageType.getMsgType(node2)
				&& node1.getChildCount() == node2.getChildCount()) {
			switch (MessageType.getMsgType(node1)) {
			case MessageType.MSG_PACKAGE:
				AccessibilityNodeInfo title1 = NodeUtils.findNodeById(node1, Ids.LUCKY_MONEY_TITLE);
				AccessibilityNodeInfo title2 = NodeUtils.findNodeById(node2, Ids.LUCKY_MONEY_TITLE);
				if (title1 == null || title2 == null || !title1.getText().equals(title2.getText())) {
					return false;
				}
				break;
			case MessageType.MSG_TEXT:
				AccessibilityNodeInfo text1 = NodeUtils.findNodeById(node1, Ids.CHAT_LEFT_CONTENT);
				AccessibilityNodeInfo text2 = NodeUtils.findNodeById(node2, Ids.CHAT_LEFT_CONTENT);
				if (text1 != null && text2 != null && !text1.getText().equals(text2.getText())) {
					return false;
				}
				break;
			default:
				break;
			}
			return true;
		}
		return false;
	}

	/**
	 * 根据id查找结点，存在返回不存在返回null
	 *
	 * @param nodeInfo
	 * @param viewId
	 * @return
	 */
	public static AccessibilityNodeInfo findNodeById(AccessibilityNodeInfo nodeInfo, String viewId) {
		if (nodeInfo == null || viewId == null) {
			return null;
		}
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
}
