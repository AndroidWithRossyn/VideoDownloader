package com.prox1.video1.download1.Vpn_auto_connect.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.prox1.video1.download1.Ads.BannerAdsSetup;
import com.prox1.video1.download1.Constant;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;


import butterknife.ButterKnife;

public class MenuActivity extends AppCompatActivity {

    ImageView ivfaq, ivShare;
    private InterstitialAd mInterstitialAd;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        Constant.FullScreencall(this);

        sharedPref = new SharedPref(getApplicationContext());


        /*--------------------------IronSourceAds-------------------*/
        IronSource.init(this, MyApplication.iron_id);

        if (!IronSource.isInterstitialReady()) {
            IronSource.loadInterstitial();
        }


        /*--------------------------BannerAds-------------------*/
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            new BannerAdsSetup(this).admobbannerads();
        }

        ivfaq = findViewById(R.id.imgfaq);
        ivShare = findViewById(R.id.imgshare);

        ivfaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MenuActivity.this, Faq.class));
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ishare = new Intent(Intent.ACTION_SEND);
                ishare.setType("text/plain");
                String sAux = "\n" + getResources().getString(R.string.app_name) + "\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
                ishare.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(ishare, "choose one"));
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarold);
        toolbar.setTitle("Free VPN Proxy");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadAdmobInterstitialAds();
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
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!IronSource.isInterstitialReady()) {
            IronSource.loadInterstitial();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        if (Constant.isConnected(getApplicationContext())) {
            if (!sharedPref.GET_PURCHASED().booleanValue()) {
                if (mInterstitialAd != null) {
                    final ProgressDialog pd = new ProgressDialog(MenuActivity.this);
                    pd.setMessage("Showing Ads..");
                    pd.setCancelable(false);
                    pd.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            mInterstitialAd.show(MenuActivity.this);
                        }
                    }, 1000);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            mInterstitialAd = null;
                            Log.d("TAG", "The ad was dismissed.");
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
                        final ProgressDialog pd = new ProgressDialog(MenuActivity.this);
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
}

