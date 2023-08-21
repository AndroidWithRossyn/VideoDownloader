package com.number_locator_one.project_one.one.admobmanager;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.number_locator_one.project_one.one.activity.SplashscreeAtivity;
import com.number_locator_one.project_one.one.comman.SharePreferencess;

import java.util.Date;

public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private static final String LOG_TAG = "AppOpenManager";
    private AppOpenAd appOpenAd = null;
    private Activity currentActivity;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private static boolean isShowingAd = false;
    private final MyApplication myApplication;
    private long loadTime = 0;
    SharePreferencess sp;
    public AppOpenManager(MyApplication myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks (this);
        ProcessLifecycleOwner.get ().getLifecycle ().addObserver (this);
    }

    @OnLifecycleEvent(ON_START)
    public void onStart () {

        if (currentActivity != null) {
            if (currentActivity instanceof SplashscreeAtivity) {
            } else {
                Log.d (LOG_TAG, "onStart" + currentActivity);

                showAdIfAvailable ();

            }

        }


    }


    public void fetchAd () {

        if (sp.getAppOpbId() != null && !sp.getAppOpbId().isEmpty () && sp.getAd_show_Status().equals("1")) {
            if (isAdAvailable ()) {
                Log.e (LOG_TAG, "fetchAd: " + "Available");
                return;
            }

            loadCallback =new AppOpenAd.AppOpenAdLoadCallback () {


                        @Override
                        public void onAdLoaded (@NonNull AppOpenAd appOpenAd) {
                            super.onAdLoaded (appOpenAd);
                            AppOpenManager.this.appOpenAd = appOpenAd;
                            AppOpenManager.this.loadTime = (new Date()).getTime ();
                            Log.e (LOG_TAG, "onAppOpenAdLoaded: " + "Load for all");
                        }

                        @Override
                        public void onAdFailedToLoad (@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad (loadAdError);
                            Log.e (LOG_TAG, "onAppOpenAdFailedToLoad: " + loadAdError);
                        }


                    };
            AdRequest request = getAdRequest ();

            Log.e (LOG_TAG, "fetchAd: " + sp.getAppOpbId() + " ca-app-pub-3940256099942544/3419835294");
            AppOpenAd.load (
                    myApplication, sp.getAppOpbId(), request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
            // We will implement this below.
        }

    }


    private AdRequest getAdRequest () {
        return new AdRequest.Builder ().setHttpTimeoutMillis (10000).build ();
    }



    private boolean wasLoadTimeLessThanNHoursAgo (long numHours) {
        long dateDifference = (new Date ()).getTime () - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }


    public boolean isAdAvailable () {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo (4);
    }

    @Override
    public void onActivityCreated (@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted (@NonNull Activity activity) {
        currentActivity = activity;
        sp=new SharePreferencess(currentActivity);
    }

    @Override
    public void onActivityResumed (@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused (@NonNull Activity activity) {

    }
    @Override
    public void onActivityStopped (@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState (@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed (@NonNull Activity activity) {
        currentActivity = null;
    }

    public void showAdIfAvailable () {

        if (!isShowingAd && isAdAvailable ()) {
            Log.d (LOG_TAG, "Will show ad.");

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback () {
                        @Override
                        public void onAdDismissedFullScreenContent () {
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;

                            fetchAd ();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent (AdError adError) {
                            Log.e (LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError);
                        }

                        @Override
                        public void onAdShowedFullScreenContent () {
                            isShowingAd = true;
                        }
                    };

            appOpenAd.setFullScreenContentCallback (fullScreenContentCallback);
            appOpenAd.show (currentActivity);

        } else {
            Log.d (LOG_TAG, "Can not show ad." + "Load Ads");
            fetchAd ();
        }
    }

    public void fetchAndShowInSplash (OnAppOpenDisplayed onAppOpenDisplayed) {

        if (sp.getAppOpbId() != null && !sp.getAppOpbId().isEmpty () && sp.getAd_show_Status().equals("1")) {
            if (sp.getApp_open_Start ().equals("0")) {
                showInSplash (onAppOpenDisplayed);
                return;
            }
            loadCallback =new AppOpenAd.AppOpenAdLoadCallback () {
                        @Override
                        public void onAdLoaded (@NonNull AppOpenAd appOpenAd) {
                            super.onAdLoaded (appOpenAd);
                            AppOpenManager.this.appOpenAd = appOpenAd;
                            AppOpenManager.this.loadTime = (new Date ()).getTime ();
                            showInSplash (onAppOpenDisplayed);
                            Log.e (LOG_TAG, "onAppOpenAdLoaded: " + "Load For Splash");
                        }
                        @Override
                        public void onAdFailedToLoad (@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad (loadAdError);
                            Log.e (LOG_TAG, "onAppOpenAdFailedToLoad: " + loadAdError);
                            //      Toast.makeText (currentActivity, loadAdError.toString (), Toast.LENGTH_LONG).show ();
                            onAppOpenDisplayed.OnSucess ();
                        }

                    };
            AdRequest request = getAdRequest ();

            Log.e (LOG_TAG, "fetchAd: " +  sp.getAppOpbId() + " ca-app-pub-3940256099942544/3419835294");
            AppOpenAd.load (
                    myApplication, sp.getAppOpbId(), request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
        } else {
            onAppOpenDisplayed.OnSucess ();
        }
    }
    private void showInSplash (OnAppOpenDisplayed onAppOpenDisplayed) {
        if (!isShowingAd && isAdAvailable ()) {
            Log.d (LOG_TAG, "Will show ad.");
            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback () {
                        @Override
                        public void onAdDismissedFullScreenContent () {
                            // Set the reference to null so isAdAvailable() returns false.
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;
                            fetchAd ();
                            onAppOpenDisplayed.OnSucess ();
                        }
                        @Override
                        public void onAdFailedToShowFullScreenContent (AdError adError) {
                            Log.e (LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError);

                            onAppOpenDisplayed.OnSucess ();
                        }
                        @Override
                        public void onAdShowedFullScreenContent () {
                            isShowingAd = true;
                        }
                    };

            appOpenAd.setFullScreenContentCallback (fullScreenContentCallback);
            appOpenAd.show (currentActivity);

        } else {
            Log.d (LOG_TAG, "Can not show ad.");
            onAppOpenDisplayed.OnSucess ();
        }
    }

}