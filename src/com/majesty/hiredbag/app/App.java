package com.majesty.hiredbag.app;

import com.majesty.hiredbag.utils.LogUtils;

import android.app.Application;


/**
 * Created by pengwei on 16/2/4.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.configAllowLog = true;
        LogUtils.configTagPrefix = "HiRobot";
    }
}
