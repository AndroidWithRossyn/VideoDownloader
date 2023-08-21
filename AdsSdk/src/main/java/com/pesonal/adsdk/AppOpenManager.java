package com.pesonal.adsdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

import static com.pesonal.adsdk.AppManage.mysharedpreferences;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String LOG_TAG = "AppOpenManager";
    private static String AD_UNIT_ID;
    private AppOpenAd appOpenAd = null;
    private static boolean isShowingAd = false;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private long loadTime = 2;

    private final Activity myApplication;
    private Activity currentActivity;

    public AppOpenManager(Activity myApplication) {
        this.myApplication = myApplication;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.myApplication.registerActivityLifecycleCallbacks(this);
        }
        SharedPreferences mysharedpreferences = myApplication.getSharedPreferences(myApplication.getPackageName(), Context.MODE_PRIVATE);
        AD_UNIT_ID = mysharedpreferences.getString("AppOpenID", "");
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public interface splshADlistner {

        void onSuccess();

        void onError(String error);
    }

    public void showAdIfAvailable(final splshADlistner listner) {
        if (!isShowingAd && isAdAvailable()) {
            Log.d(LOG_TAG, "Will show ad.");

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;
                            listner.onSuccess();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            listner.onError(adError.getMessage());
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;

                        }
                    };

            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);


        } else {
            listner.onError("");
        }
    }
    /** Request an ad */
    public void fetchAd(final splshADlistner listner) {
        if (isAdAvailable()) {
            return;
        }

        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                super.onAdLoaded(appOpenAd);
                AppOpenManager.this.appOpenAd = appOpenAd;
                AppOpenManager.this.loadTime = (new Date()).getTime();
                Log.e("my_log", "onAppOpenAdToLoad: " );
                listner.onSuccess();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("my_log", "onAppOpenAdFailedToLoad: "+loadAdError.getMessage() );
                listner.onError(loadAdError.getMessage());
            }
        };

        AdRequest request = getAdRequest();
        AppOpenAd.load(
                myApplication, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    /** Creates and returns ad request. */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityPreStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityPostPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPreStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityPostSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityPreDestroyed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {

    }


}