package com.majesty.hiredbag.utils;

import android.util.Log;

public class LogUtils {
	public static boolean configAllowLog = true;
	public static String configTagPrefix = "MAJESTY_HI_RED";

	public static void e(Exception e) {
		e(e.getMessage());
	}

	public static void e(String msg) {
		Log.d(configTagPrefix, msg);
	}

}
