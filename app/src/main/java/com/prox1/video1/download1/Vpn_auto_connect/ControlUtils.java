package com.prox1.video1.download1.Vpn_auto_connect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;

public class ControlUtils {

    public static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 256;

    @SuppressLint("WrongConstant")
    public static boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT < 21 || ((AppOpsManager) context.getSystemService("appops")).checkOpNoThrow("android:get_usage_stats", Process.myUid(), context.getPackageName()) == 0) {
            return true;
        }
        return false;
    }

    public static void startPermSettings(Activity activity) {
        try {
            activity.startActivityForResult(new Intent("android.settings.USAGE_ACCESS_SETTINGS"), 256);
        } catch (Throwable unused) {
            activity.startActivityForResult(new Intent("android.settings.SETTINGS"), 256);
        }
    }
}
