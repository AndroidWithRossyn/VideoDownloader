package com.prox1.video1.download1.Ads;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.prox1.video1.download1.Constant;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;


public class NativeAdsSetup {

    public static int nativebigRefreshCount = 0;
    public static int nativesmallRefreshCount = 0;
    private final Activity activity;
    private final LinearLayout linearLayout;
    private final LinearLayout smalllinearLayout;

    private NativeAd adSmallAdmobNative;
    private NativeAd adExitAdmobNative;

    public NativeAdsSetup(Activity otheractivity, LinearLayout otherslinearLayout, LinearLayout smalllinearLayout) {
        activity = otheractivity;
        linearLayout = otherslinearLayout;
        this.smalllinearLayout = smalllinearLayout;
    }

    public void CounterLoadbigNativeAdsBoth() {
        if (!SharedPref.GET_PURCHASED().booleanValue()) {
            if (Constant.isConnected(activity)) {
                nativebigRefreshCount++;
                if (nativebigRefreshCount >= 3) {
                    nativebigRefreshCount = 0;
                    loadbigbothnativeads();
                }
            }
        }
    }


    public void loadbigbothnativeads() {
        if (!SharedPref.GET_PURCHASED().booleanValue()) {
            if (Constant.isConnected(activity)) {
                loadAdmobbigNativeAdsList();
            }
        }
    }

    public void loadsmallbothnativeads() {
        if (!SharedPref.GET_PURCHASED().booleanValue()) {
            if (Constant.isConnected(activity)) {
                loadsmallAdmobNativeAdsList();
            }
        }
    }

    public void loadexitandcallingbothnativeads() {
        if (!SharedPref.GET_PURCHASED().booleanValue()) {
            if (Constant.isConnected(activity)) {
                loadExitAdmobNativeAdsList();
            }
        }
    }

    private void loadExitAdmobNativeAdsList() {
        if (Constant.isConnected(activity)) {
            try {
                AdLoader.Builder builder = new AdLoader.Builder(activity, MyApplication.nativeeads);
                AdLoader adLoader = builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        if (adExitAdmobNative != null)
                            adExitAdmobNative.destroy();
                        adExitAdmobNative = nativeAd;
                        NativeAdsSetup.this.showExitadmobnative();
                    }
                }).withAdListener(
                        new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError errorCode) {
                                // A native ad failed to load, check if the ad loader has finished loading
                                // and if so, insert the ads into the list.
                                Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                        + " load another.");

                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                loadexitandcallingbothnativeads();
                            }
                        }).build();

                // Load the Native ads.
                adLoader.loadAds(new AdRequest.Builder().build(), 2);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void loadAdmobbigNativeAdsList() {
        if (Constant.isConnected(activity)) {
            try {
                AdLoader.Builder builder = new AdLoader.Builder(activity, MyApplication.nativeeads);
                AdLoader adLoader = builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        if (Constant.adAdmobNative != null)
                            Constant.adAdmobNative.destroy();
                        Constant.adAdmobNative = nativeAd;
                        NativeAdsSetup.this.showbigadmobnative();
                    }
                }).withAdListener(
                        new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError errorCode) {
                                // A native ad failed to load, check if the ad loader has finished loading
                                // and if so, insert the ads into the list.
                                Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                        + " load another.");

                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                loadbigbothnativeads();
                            }
                        }).build();

                // Load the Native ads.
                adLoader.loadAd(new AdRequest.Builder().build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadsmallAdmobNativeAdsList() {
        if (Constant.isConnected(activity)) {
            try {
                AdLoader.Builder builder = new AdLoader.Builder(activity, MyApplication.nativeeads);
                AdLoader adLoader = builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        if (adSmallAdmobNative != null)
                            adSmallAdmobNative.destroy();
                        adSmallAdmobNative = nativeAd;
                        NativeAdsSetup.this.showadmobsmallnative();
                    }
                }).withAdListener(
                        new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError errorCode) {
                                // A native ad failed to load, check if the ad loader has finished loading
                                // and if so, insert the ads into the list.
                                Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                        + " load another.");

                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                loadsmallbothnativeads();
                            }
                        }).build();

                // Load the Native ads.
                adLoader.loadAd(new AdRequest.Builder().build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showbigadmobnative() {
        if (Constant.adAdmobNative != null) {
            try {
                NativeAdView adView = (NativeAdView) LayoutInflater.from(activity).inflate(R.layout.ad_unified, null);
                populateNativeAdView(Constant.adAdmobNative, adView);
                linearLayout.removeAllViews();
                linearLayout.addView(adView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showExitadmobnative() {
        if (adExitAdmobNative != null) {
            try {
                NativeAdView adView = (NativeAdView) LayoutInflater.from(activity).inflate(R.layout.ad_unified, null);
                populateNativeAdView(adExitAdmobNative, adView);
                linearLayout.removeAllViews();
                linearLayout.addView(adView);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void showadmobsmallnative() {
        if (adSmallAdmobNative != null) {
            try {
                NativeAdView adView = (NativeAdView) LayoutInflater.from(activity).inflate(R.layout.admob_small_ad_unified, null);
                populateNativeAdView(adSmallAdmobNative, adView);
                smalllinearLayout.removeAllViews();
                smalllinearLayout.addView(adView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //load Native Ads
    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

}
