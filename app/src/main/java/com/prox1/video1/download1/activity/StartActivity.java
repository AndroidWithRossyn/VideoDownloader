package com.prox1.video1.download1.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.prox1.video1.download1.Ads.BannerAdsSetup;
import com.prox1.video1.download1.Ads.NativeAdsSetup;
import com.prox1.video1.download1.BuildConfig;
import com.prox1.video1.download1.Constant;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;
import com.prox1.video1.download1.chip.SelectChipActivity;
import com.prox1.video1.download1.util.Utils;

public class StartActivity extends AppCompatActivity {
    ImageView img_get_started;

    LinearLayout linearAdsNative;
    private NativeAdsSetup nativeAdsBaseApp;
    private InterstitialAd mInterstitialAd;
    ImageButton imgbtn_rate, imgbtn_share, imgbtn_privacy;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_new);

        Constant.FullScreencall(this);

        sharedPref = new SharedPref(getApplicationContext());

        imgbtn_rate = (ImageButton) findViewById(R.id.button_rate);
        imgbtn_share = (ImageButton) findViewById(R.id.button_share);
        imgbtn_privacy = (ImageButton) findViewById(R.id.button_privacy);


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

        img_get_started = findViewById(R.id.img_get_started);
        img_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (mInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(StartActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            // Show the ad when it's done loading.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    mInterstitialAd.show(StartActivity.this);
                                }
                            }, 2000);

                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    startActivity(new Intent(getApplicationContext(), SelectChipActivity.class));
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
                                final ProgressDialog pd = new ProgressDialog(StartActivity.this);
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
                                        startActivity(new Intent(getApplicationContext(), SelectChipActivity.class));
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
                                startActivity(new Intent(getApplicationContext(), SelectChipActivity.class));
                                finish();
                            }
                        }
                    } else {
                        startActivity(new Intent(getApplicationContext(), SelectChipActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), SelectChipActivity.class));
                    finish();
                }
            }
        });

        imgbtn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Download HD videos from anywhere now use this app : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        imgbtn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });
        imgbtn_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }*/
                Utils.showprivacy(StartActivity.this);
            }
        });


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
    protected void onResume() {
        super.onResume();
        nativeAdsBaseApp.CounterLoadbigNativeAdsBoth();
        if (!IronSource.isInterstitialReady()) {
            IronSource.loadInterstitial();
        }
    }

/*    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
        finish();

    }*/


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (Constant.isConnected(getApplicationContext())) {
            if (!sharedPref.GET_PURCHASED().booleanValue()) {
                if (mInterstitialAd != null) {
                    final ProgressDialog pd = new ProgressDialog(StartActivity.this);
                    pd.setMessage("Showing Ads..");
                    pd.setCancelable(false);
                    pd.show();
                    // Show the ad when it's done loading.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            mInterstitialAd.show(StartActivity.this);
                        }
                    }, 2000);

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            if (MyApplication.is_fourth_bool.equals("true")) {
                                startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
                                finish();
                            } else {
                                showExitDialog();
                            }
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
                        final ProgressDialog pd = new ProgressDialog(StartActivity.this);
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
                                if (MyApplication.is_fourth_bool.equals("true")) {
                                    startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
                                    finish();
                                } else {
                                    showExitDialog();
                                }
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
                        if (MyApplication.is_fourth_bool.equals("true")) {
                            startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
                            finish();
                        } else {
                            showExitDialog();
                        }
                    }
                }
            } else {
                if (MyApplication.is_fourth_bool.equals("true")) {
                    startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
                    finish();
                } else {
                    showExitDialog();
                }
            }
        } else {
            if (MyApplication.is_fourth_bool.equals("true")) {
                startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
                finish();
            } else {
                showExitDialog();
            }
        }
    }


    public void showExitDialog() {

        final Dialog dialogCustomExit = new Dialog(this, R.style.AdsDialog);
        dialogCustomExit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCustomExit.setContentView(R.layout.alert_dialog);
        dialogCustomExit.setCancelable(false);
        dialogCustomExit.show();

        TextView txtTileDialog = (TextView) dialogCustomExit.findViewById(R.id.txtTileDialog);
        TextView txtMessageDialog = (TextView) dialogCustomExit.findViewById(R.id.txtMessageDialog);

        LinearLayout linearAdsNative_dialog = (LinearLayout) dialogCustomExit.findViewById(R.id.linearAdsNative_dialog);
        NativeAdsSetup nativeAdsBaseApp = new NativeAdsSetup(this, linearAdsNative_dialog, null);
        nativeAdsBaseApp.loadexitandcallingbothnativeads();

        Button btnNegative = (Button) dialogCustomExit.findViewById(R.id.btnNegative);
        Button btnPositive = (Button) dialogCustomExit.findViewById(R.id.btnPositive);
        txtTileDialog.setText("Exit");
        txtMessageDialog.setText("Are you sure want to exit from app?");
        btnPositive.setText("Yes");
        btnNegative.setText("No");

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogCustomExit.dismiss();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(intent);
                finish();

            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogCustomExit.dismiss();
            }
        });
    }

}