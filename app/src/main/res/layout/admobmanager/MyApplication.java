package com.number_locator_one.project_one.one.admobmanager;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.anchorfree.sdk.HydraTransportConfig;
import com.anchorfree.sdk.NotificationConfig;
import com.anchorfree.sdk.TransportConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.northghost.caketube.OpenVpnTransportConfig;
import com.number_locator_one.project_one.one.R;
import com.number_locator_one.project_one.one.comman.SharePreferencess;
import com.number_locator_one.project_one.one.comman.Utilss;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static final String CHANNEL_ID = "Mobile Number Locator";
    AppOpenManager appOpenManager;
    TemplateView templateView;
    SharePreferencess sp;
    public static String url;
    public static String Base_Url;
    public static String key_value;
    public AppOpenManager getAppOpenManager() {
        return appOpenManager;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sp = new SharePreferencess(this);
        url = Utilss.decrypeData();
        Base_Url=Utilss.Base_Url();
        key_value=Utilss.getKeyHash(this);
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });
        appOpenManager = new AppOpenManager(this);
                initVPNSdk();
    }


    private void initVPNSdk() {
        createNotificationChannel();

        List<TransportConfig> transportConfigList = new ArrayList<>();
        transportConfigList.add(HydraTransportConfig.create());
        transportConfigList.add(OpenVpnTransportConfig.tcp());
        transportConfigList.add(OpenVpnTransportConfig.udp());
        UnifiedSDK.update(transportConfigList, CompletableCallback.EMPTY);

        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .channelId(CHANNEL_ID)
                .build();
        UnifiedSDK.update(notificationConfig);
        UnifiedSDK.setLoggingLevel(Log.VERBOSE);

    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = "VPN notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
