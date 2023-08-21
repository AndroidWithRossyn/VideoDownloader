package com.prox1.video1.download1.Ads;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.SharedPref;


import static androidx.lifecycle.Lifecycle.Event.ON_START;


public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    private static final String LOG_TAG = "AppOpenManager";
    private AppOpenAd appOpenAd = null;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;

    private Activity currentActivity;


    public static boolean isShowingAd = false;


    private final MyApplication myApplication;


    /**
     * Constructor
     */
    public AppOpenManager(MyApplication myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }


    /**
     * LifecycleObserver methods
     */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        showAdIfAvailable();
        Log.d(LOG_TAG, "onStart");
    }

    /**
     * Request an ad
     */
    public void fetchAd() {
        if (!SharedPref.GET_PURCHASED().booleanValue()) {
//            return;


            if (isAdAvailable()) {
                return;
            }

            loadCallback =
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                            super.onAdLoaded(appOpenAd);
                            AppOpenManager.this.appOpenAd = appOpenAd;

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            fetchAd();
                        }
                    };
            AdRequest request = getAdRequest();
            AppOpenAd.load(
                    myApplication, MyApplication.app_open, request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
//        Log.d(LOG_TAG, firebaseremoteappopen);
        }
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null;
    }

    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            Log.d(LOG_TAG, "Will show ad.");

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;
                            fetchAd();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            fetchAd();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };

            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);

        } else {
            Log.d(LOG_TAG, "Can not show ad.");
            fetchAd();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }
}
