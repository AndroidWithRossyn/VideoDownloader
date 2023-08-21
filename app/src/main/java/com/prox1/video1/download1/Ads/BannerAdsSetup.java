package com.prox1.video1.download1.Ads;

import static com.prox1.video1.download1.Constant.isConnected;

import android.app.Activity;
import android.widget.RelativeLayout;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;


public class BannerAdsSetup {

    private Activity activity;

    private com.google.android.gms.ads.AdView mAdViewAdmob;

    public BannerAdsSetup(Activity activity) {
        this.activity = activity;
    }

    public void admobbannerads() {

        RelativeLayout relativeAdsBanner = (RelativeLayout) activity.findViewById(R.id.relativeAdsBanner);

        relativeAdsBanner.removeAllViews();

        if (isConnected(activity)) {
            mAdViewAdmob = new com.google.android.gms.ads.AdView(activity);
            mAdViewAdmob.setAdSize(AdSize.LARGE_BANNER);
            mAdViewAdmob.setAdUnitId(MyApplication.banner);
            relativeAdsBanner.addView(mAdViewAdmob);
            mAdViewAdmob.loadAd(new AdRequest.Builder().build());
            mAdViewAdmob.setAdListener(new com.google.android.gms.ads.AdListener() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    admobbannerads();
                }
            });
        }
    }

   /* public void admobbannermediaumsizeads() {

        RelativeLayout relativeAdsBanner = (RelativeLayout) activity.findViewById(R.id.banner_md);
        relativeAdsBanner.removeAllViews();

        if (isConnected(activity)) {
            mAdViewAdmob = new com.google.android.gms.ads.AdView(activity);
            mAdViewAdmob.setAdSize(com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE);
            mAdViewAdmob.setAdUnitId(getfirebaseAdmobbannerid);
            relativeAdsBanner.addView(mAdViewAdmob);
            mAdViewAdmob.loadAd(new AdRequest.Builder()
                    .build());
            mAdViewAdmob.setAdListener(new com.google.android.gms.ads.AdListener() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    admobbannermediaumsizeads();
                }
            });
        }
    }*/

}

