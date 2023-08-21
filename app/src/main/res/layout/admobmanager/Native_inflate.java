package com.number_locator_one.project_one.one.admobmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.number_locator_one.project_one.one.R;

public class Native_inflate {
    Context context;
    public Native_inflate(Context context) {
        this.context = context;
    }

    public void inflateNATIV(NativeAd nativeAd, ViewGroup cardView) {
        cardView.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_ads_native_admob, null);
        cardView.removeAllViews();
        cardView.addView(view);

        NativeAdView adView = view.findViewById(R.id.aal_nv_BaseAd);
        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.aal_tv_Heading));
        adView.setBodyView(adView.findViewById(R.id.aal_tv_Body));
        adView.setCallToActionView(adView.findViewById(R.id.aal_btn_CTA));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
         adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

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
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }


        adView.setNativeAd(nativeAd);
        //   VideoController vc = nativeAd.getVideoController ();

    }

    public void inflateNativeSmallNew(NativeAd nativeAd, ViewGroup cardView) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_ads_native_admob_small_new, null);
        cardView.removeAllViews();
        cardView.addView(view);

        NativeAdView adView = view.findViewById(R.id.aal_nv_BaseAd);

        adView.setMediaView(adView.findViewById(R.id.ad_media));

        adView.setHeadlineView(adView.findViewById(R.id.aal_tv_Heading));
        adView.setBodyView(adView.findViewById(R.id.aal_tv_Body));
        adView.setCallToActionView(adView.findViewById(R.id.aal_btn_CTA));


//        adView.setPriceView(adView.findViewById(R.id.main_price));
         adView.setStarRatingView(adView.findViewById(R.id.aal_rb_Rate));
//        adView.setStoreView(adView.findViewById(R.id.main_store));
//        adView.setAdvertiserView(adView.findViewById(R.id.main_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

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
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }


        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.GONE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }
}
