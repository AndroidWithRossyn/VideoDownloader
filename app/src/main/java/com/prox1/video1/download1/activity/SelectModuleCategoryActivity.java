package com.prox1.video1.download1.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.prox1.video1.download1.Ads.BannerAdsSetup;
import com.prox1.video1.download1.Ads.NativeAdsSetup;
import com.prox1.video1.download1.Constant;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;
import com.prox1.video1.download1.Vpn_auto_connect.activity.VPNActivity;
import com.prox1.video1.download1.api.CommonClassForAPI;
import com.prox1.video1.download1.chip.SelectChipActivity;
import com.prox1.video1.download1.databinding.ActivityFacebookBinding;
import com.prox1.video1.download1.util.AppLangSessionManager;
import com.prox1.video1.download1.util.SharePrefs;
import com.prox1.video1.download1.util.Utils;
import static com.prox1.video1.download1.util.Utils.RootDirectoryFacebook;
import static com.prox1.video1.download1.util.Utils.createFileFolder;
import static com.prox1.video1.download1.util.Utils.startDownload;

public class SelectModuleCategoryActivity extends AppCompatActivity {

    LinearLayout linearAdsNative;
    private NativeAdsSetup nativeAdsBaseApp;
    private InterstitialAd mInterstitialAd;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_module_category);

        sharedPref = new SharedPref(getApplicationContext());

        Constant.FullScreencall(this);


        /*--------------------------BannerAds-------------------*/
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            new BannerAdsSetup(this).admobbannerads();
        }

        /*--------------------------NativeAds-------------------*/
        linearAdsNative = findViewById(R.id.linearAdsNative);
        nativeAdsBaseApp = new NativeAdsSetup(this, linearAdsNative, null);
        nativeAdsBaseApp.loadexitandcallingbothnativeads();

        /*--------------------------InterstitialAds-------------------*/
        loadAdmobInterstitialAds();


        /*--------------------------IronSourceAds-------------------*/
        IronSource.init(this, MyApplication.iron_id);

        if (!IronSource.isInterstitialReady()) {
            IronSource.loadInterstitial();
        }

        findViewById(R.id.btn_vpn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (mInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(SelectModuleCategoryActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            // Show the ad when it's done loading.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    mInterstitialAd.show(SelectModuleCategoryActivity.this);
                                }
                            }, 2000);

                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    startActivity(new Intent(getApplicationContext(), VPNActivity.class));
                                    finish();
                                    loadAdmobInterstitialAds();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    loadAdmobInterstitialAds();
                                }
                            });
                        } else {
                            if (IronSource.isInterstitialReady()) {
                                final ProgressDialog pd = new ProgressDialog(SelectModuleCategoryActivity.this);
                                pd.setMessage("Showing Ads..");
                                pd.setCancelable(false);
                                pd.show();
                                // Show the ad when it's done loading.
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        IronSource.showInterstitial();
                                    }
                                }, 1500);
                                IronSource.setInterstitialListener(new InterstitialListener() {
                                    @Override
                                    public void onInterstitialAdReady() {

                                    }

                                    @Override
                                    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {

                                    }

                                    @Override
                                    public void onInterstitialAdOpened() {

                                    }

                                    @Override
                                    public void onInterstitialAdClosed() {
                                        startActivity(new Intent(getApplicationContext(), VPNActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onInterstitialAdShowSucceeded() {

                                    }

                                    @Override
                                    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {

                                    }

                                    @Override
                                    public void onInterstitialAdClicked() {

                                    }
                                });
                            } else {
                                startActivity(new Intent(getApplicationContext(), VPNActivity.class));
                                finish();
                            }
                        }
                    } else {
                        startActivity(new Intent(getApplicationContext(), VPNActivity.class));
                        finish();
                    }
                } else {

                    startActivity(new Intent(getApplicationContext(), VPNActivity.class));
                    finish();

                }
            }
        });

        findViewById(R.id.btn_video_downloader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (mInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(SelectModuleCategoryActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            // Show the ad when it's done loading.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    mInterstitialAd.show(SelectModuleCategoryActivity.this);
                                }
                            }, 2000);

                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                    loadAdmobInterstitialAds();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    loadAdmobInterstitialAds();
                                }
                            });
                        } else {
                            if (IronSource.isInterstitialReady()) {
                                final ProgressDialog pd = new ProgressDialog(SelectModuleCategoryActivity.this);
                                pd.setMessage("Showing Ads..");
                                pd.setCancelable(false);
                                pd.show();
                                // Show the ad when it's done loading.
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        IronSource.showInterstitial();
                                    }
                                }, 1500);
                                IronSource.setInterstitialListener(new InterstitialListener() {
                                    @Override
                                    public void onInterstitialAdReady() {

                                    }

                                    @Override
                                    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {

                                    }

                                    @Override
                                    public void onInterstitialAdOpened() {

                                    }

                                    @Override
                                    public void onInterstitialAdClosed() {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onInterstitialAdShowSucceeded() {

                                    }

                                    @Override
                                    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {

                                    }

                                    @Override
                                    public void onInterstitialAdClicked() {

                                    }
                                });
                            } else {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }
                    } else {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                } else {

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!IronSource.isInterstitialReady()) {
            IronSource.loadInterstitial();
        }
    }

    public void loadAdmobInterstitialAds() {
        InterstitialAd.load(this, MyApplication.interstitial_2, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                        loadAdmobInterstitialAds();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SelectChipActivity.class));
        finish();
    }
}