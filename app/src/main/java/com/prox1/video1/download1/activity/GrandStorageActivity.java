package com.prox1.video1.download1.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.prox1.video1.download1.Ads.BannerAdsSetup;
import com.prox1.video1.download1.Ads.NativeAdsSetup;
import com.prox1.video1.download1.Constant;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;
import com.prox1.video1.download1.Vpn_auto_connect.activity.Privacy_Policy;
import com.prox1.video1.download1.api.CommonClassForAPI;
import com.prox1.video1.download1.databinding.ActivityFacebookBinding;
import com.prox1.video1.download1.util.AppLangSessionManager;
import com.prox1.video1.download1.util.SharePrefs;
import com.prox1.video1.download1.util.Utils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.List;

public class GrandStorageActivity extends AppCompatActivity {
    ImageView img_gotoapp;

    LinearLayout linearAdsNative;
    private NativeAdsSetup nativeAdsBaseApp;

    private InterstitialAd mInterstitialAd;
    private int storagecheck = 114;

    public static SharedPref sharedPref;

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_storage_permision);

        Constant.FullScreencall(this);

        sharedPref = new SharedPref(getApplicationContext());

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


        img_gotoapp = findViewById(R.id.img_gotoapp);
        img_gotoapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isConnected(getApplicationContext())) {
                    if (checkPermissions(storagecheck)) {
                        if (!sharedPref.GET_PURCHASED().booleanValue()) {
                            if (mInterstitialAd != null) {
                                final ProgressDialog pd = new ProgressDialog(GrandStorageActivity.this);
                                pd.setMessage("Showing Ads..");
                                pd.setCancelable(false);
                                pd.show();
                                // Show the ad when it's done loading.
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        mInterstitialAd.show(GrandStorageActivity.this);
                                    }
                                }, 2000);
                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
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
                                startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
                                finish();
                            }
                        } else {
                            startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
                            finish();
                        }
                    } else {
                        checkPermissions(storagecheck);
//                        Toast.makeText(getApplicationContext(), "Please Allow Storage Permission", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadAdmobInterstitialAds() {
        InterstitialAd.load(this, MyApplication.interstitial_1, new AdRequest.Builder().build(),
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
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Privacy_Policy.class));
        finish();
    }

    private boolean checkPermissions(int type) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) (this),
                    listPermissionsNeeded.toArray(new
                            String[listPermissionsNeeded.size()]), type);
            return false;
        } /*else {
            if (type == storagecheck) {
                startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
                finish();
            }
        }*/
        return true;
    }


}