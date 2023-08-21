package com.prox1.video1.download1;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.sdk.HydraTransportConfig;
import com.anchorfree.sdk.NotificationConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.UnifiedSDKConfig;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.vpnservice.ProcessUtils;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.northghost.caketube.OpenVpnTransportConfig;
import com.onesignal.OneSignal;
import com.pixplicity.easyprefs.library.Prefs;
import com.prox1.video1.download1.Ads.Ads;
import com.prox1.video1.download1.Ads.AppOpenManager;
import com.prox1.video1.download1.Ads.RetrofitClient;
import com.prox1.video1.download1.util.AppLangSessionManager;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class MyApplication extends Application implements androidx.work.Configuration.Provider {

    AppLangSessionManager appLangSessionManager;

    public static AppOpenManager appOpenManager;
    private static MyApplication instance;

    public static final String COUNTRY_DATA = "Country_data";
    public static final String BUNDLE = "Bundle";
    public static final String SELECTED_COUNTRY = "selected_country";
    public static final String PRIMIUM_STATE = "primium_state";


    public static String App_version = "";
    public static String App_link = "";
    public static String banner = "test";
    public static String interstitial_1 = "test";
    public static String interstitial_2 = "test";
    public static String nativeeads = "test";
    public static String iron_id = "test";
    public static String app_open = "test";
    public static String is_one_bool = "false";
    public static String is_two_bool = "false";
    public static String is_third_bool = "false";
    public static String is_fourth_bool = "false";
    public static String Carrier_Id = "touchvpn";
    public static String Country_Code = "us";
    public static String force_update = "false";
    public static String force_redirect = "false";

    public static SharedPref sharedPref;

    //OTHER CODE
    @Override
    public androidx.work.Configuration getWorkManagerConfiguration() {
        return new androidx.work.Configuration.Builder()
                .setMinimumLoggingLevel(Log.INFO)
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        sharedPref = new SharedPref(getApplicationContext());

        AudienceNetworkAds.initialize(this);
//        FirebaseMessaging.getInstance().subscribeToTopic("all");
        appLangSessionManager = new AppLangSessionManager(getApplicationContext());
        setLocale(appLangSessionManager.getLanguage());

        // OneSignal Initialization
        try {
            OneSignal.initWithContext(this);
            OneSignal.setAppId("38164f4a-88f1-4bb8-9043-b848f234377d");
        } catch (Exception e) {
            e.printStackTrace();
        }

        appOpenManager = new AppOpenManager(this);
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            appOpenManager.fetchAd();
        }
        if (ProcessUtils.isMainProcess(this)) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        }
        try {
            //vpn
            new Prefs.Builder()
                    .setContext(this)
                    .setMode(ContextWrapper.MODE_PRIVATE)
                    .setPrefsName(getPackageName())
                    .setUseDefaultSharedPreference(true)
                    .build();

/*
            if (getAds(instance)) {
                hydraInit(instance);
            }
*/

            getAds(instance);
            // Vpn End

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setLocale(String lang) {
        if (lang.equals("")) {
            lang = "en";
        }
        Log.d("Support", lang + "");
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public static MyApplication getInstance() {
        return instance;
    }


    //vpn

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("vpn", "Sample VPN", 3);
            notificationChannel.setDescription("VPN notification");
            ((NotificationManager) context.getSystemService(NotificationManager.class)).createNotificationChannel(notificationChannel);
        }
    }

    public void hydraInit(Context context) {

        createNotificationChannel(context);
        SharedPreferences prefs = getPrefs();

        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl("https://d2isj403unfbyl.cloudfront.net")
//                .baseUrl("https://z2v0lwnhcnrlci51cwo.get-carter.us")
                .carrierId(MyApplication.Carrier_Id)
                .build();

        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(context.getResources().getString(R.string.app_name))
                .build();

        ArrayList arrayList = new ArrayList();
        arrayList.add(HydraTransportConfig.create());
        arrayList.add(OpenVpnTransportConfig.tcp());
        arrayList.add(OpenVpnTransportConfig.udp());
        UnifiedSDK.update(arrayList, CompletableCallback.EMPTY);
        UnifiedSDK.getInstance(clientInfo, UnifiedSDKConfig.newBuilder().idfaEnabled(false).build());
//        UnifiedSDK.update(notificationConfig);
        //# Notification
        UnifiedSDK.update(NotificationConfig.newBuilder().title("Click here to Disconnect...").channelId("vpn").build());
        UnifiedSDK.setLoggingLevel(2);

        //# Login
        UnifiedSDK.getInstance().getBackend().login(AuthMethod.anonymous(), new Callback<User>() {
            public void failure(VpnException vpnException) {
            }

            public void success(User user) {
            }
        });

    }

    public boolean getAds(Context context) {

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("app_package", context.getPackageName())
                .build();

        Call<List<Ads>> call = RetrofitClient.getInstance().getMyApi().getAds(requestBody);
        call.enqueue(new retrofit2.Callback<List<Ads>>() {
            @Override
            public void onResponse(Call<List<Ads>> call, Response<List<Ads>> response) {
                List<Ads> AdsList = response.body();
                if (response.isSuccessful()) {
                    for (int i = 0; i < AdsList.size(); i++) {
                        String s = response.body().get(i).getApp_package();
                        String s1 = response.body().get(i).getAppName();
                        MyApplication.App_version = response.body().get(i).getApp_version();
                        MyApplication.app_open = response.body().get(i).getApp_open();
                        MyApplication.banner = response.body().get(i).getBanner();
                        MyApplication.interstitial_1 = response.body().get(i).getInterstitial_1();
                        MyApplication.interstitial_2 = response.body().get(i).getInterstitial_2();
                        MyApplication.nativeeads = response.body().get(i).getNative_ads();
                        MyApplication.iron_id = response.body().get(i).getIron();
                        MyApplication.is_one_bool = response.body().get(i).getIs_one_bool();
                        MyApplication.is_two_bool = response.body().get(i).getIs_two_bool();
                        MyApplication.is_third_bool = response.body().get(i).getIs_third_bool();
                        MyApplication.is_fourth_bool = response.body().get(i).getIs_fourth_bool();
                        MyApplication.Carrier_Id = response.body().get(i).getCarrier_id();
                        MyApplication.Country_Code = response.body().get(i).getCountry_code();
                        MyApplication.App_link = response.body().get(i).getApp_link();
                        MyApplication.force_update = response.body().get(i).getForce_update();
                        MyApplication.force_redirect = response.body().get(i).getForce_redirect();
                        String s6 = response.body().get(i).getRewarded();
                        Log.e("autolog", s);
                    }
                    hydraInit(instance);
                }
            }

            @Override
            public void onFailure(Call<List<Ads>> call, Throwable t) {
                t.printStackTrace();
            }

        });

        return true;

    }

    public SharedPreferences getPrefs() {
        return getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public void setNewHostAndCarrier(String hostUrl, String carrierId) {
        SharedPreferences prefs = getPrefs();
        if (TextUtils.isEmpty(hostUrl)) {
            prefs.edit().remove(BuildConfig.STORED_HOST_URL_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_HOST_URL_KEY, hostUrl).apply();
        }
        if (TextUtils.isEmpty(carrierId)) {
            prefs.edit().remove(BuildConfig.STORED_CARRIER_ID_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_CARRIER_ID_KEY, carrierId).apply();
        }
        hydraInit(instance);
    }

}