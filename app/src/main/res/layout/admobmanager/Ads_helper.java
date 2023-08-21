package com.number_locator_one.project_one.one.admobmanager;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.number_locator_one.project_one.one.comman.SharePreferencess;

import java.util.ArrayList;

public class Ads_helper {
    SharePreferencess sp;
    Context context;
    static Ads_helper adHelper;
    AdLoader adLoader;
    public ArrayList<NativeAd> list = new ArrayList<>();

    public Ads_helper(Context context) {
        this.context=context;
        sp=new SharePreferencess(context);
    }
    public static Ads_helper getInstance(Context context)
    {
      if(adHelper==null)
      {
       adHelper=new Ads_helper(context);
      }
      else
      {
      adHelper.setContext(context);
      }
      return adHelper;
    }
    private void setContext(Context context) {
        this.context = context;
    }
    public void preLoadAdmobNative() {
        admobNativeBuilder(context, sp.getAdmob_Native());
        if (adLoader != null) {

            adLoader.loadAds(new AdRequest.Builder().build(), 2);
        }

    }

    private void admobNativeBuilder(Context context, String native_placement_id) {
        if (native_placement_id.isEmpty() || sp.getAd_show_Admob().equals("0") || sp.getAd_show_Status().equals("0") ) {
            return;
        }
        AdLoader.Builder builder = new AdLoader.Builder(context, native_placement_id)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                      //  Log.d("Load_native", "onNativeAdLoaded: admob native is loaded");

                        list.add(nativeAd);

                    }
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                       // Log.d("Load_native", "onAdFailedToLoad: admob native failed to load, error: " + loadAdError.getMessage());
                    }

                });
        adLoader = builder.build();
    }
    public void showAdmobPreloadNative(ViewGroup viewGroup, boolean isSmall) {
        NativeAd nativeAd;
        if (list == null || list.size() == 0) {
            Log.e("preload", "showAdmobPreloadNative: admob native list is null");
            showNativeWhenNotPreLoad(context, viewGroup, isSmall);
            return;
        }

        if (list.size() < 2) {
            preLoadAdmobNative();
        }

        nativeAd = list.get(0);

        if (isSmall) {
            new Native_inflate(context).inflateNativeSmallNew(nativeAd, viewGroup);
        } else {

            new Native_inflate(context).inflateNATIV(nativeAd, viewGroup);
        }
        list.remove(0);
    }
    public void showNativeWhenNotPreLoad(Context context, ViewGroup viewGroup, boolean isSmall) {
        if (sp.getAdmob_Native().isEmpty() || sp.getAd_show_Status().equals("0") || sp.getAd_show_Admob().equals("0") ) {
            return;
        }
        AdLoader.Builder builder = new AdLoader.Builder(context, sp.getAdmob_Native())
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {

                        Log.d("preload", "onNativeAdLoaded: native admob native is loaded");
                        if (isSmall) {
                            new Native_inflate(context).inflateNativeSmallNew(nativeAd, viewGroup);
                        } else {
                            new Native_inflate(context).inflateNATIV(nativeAd, viewGroup);
                        }

                    }
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        preLoadAdmobNative();
                        Log.d("preload", "onAdFailedToLoad: admob native failed to load, error: " + loadAdError.getMessage());
                    }

                });

        adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

}
