package com.majesty.hiredbag;

import java.util.List;

import com.majesty.hiredbag.utils.LogUtils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {
    private AccessibilityManager accessibilityManager;
    private Button clickBtn;
    private TextView tv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        clickBtn = (Button) findViewById(R.id.click_func);
        tv_info=(TextView)findViewById(R.id.tv_info);
        clickBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.click_func:
            // 开启无障碍服务设置页面
            Intent killIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(killIntent);
            break;
        default:
            break;
        }
    }

    /**
     * 抢红包服务是否启用
     *
     * @return
     */
    private boolean isServiceEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager
                .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            LogUtils.e("Find a service:" + info.getId());
            if (info.getId().equals(getPackageName() + "/.RobotService")) {
                return true;
            }
        }
        return false;
    }

    //在resume阶段判断服务是否开启
    @Override
    protected void onResume() {
        super.onResume();
        // 根据服务可用与否，调整按钮文字
        if (isServiceEnabled()) {
            clickBtn.setText("关闭辅助");
            tv_info.setText("当前运行状态: 运行中~");
        } else {
            clickBtn.setText("打开辅助功能");
            tv_info.setText("当前运行状态: 未运行");
        }
    };

}
