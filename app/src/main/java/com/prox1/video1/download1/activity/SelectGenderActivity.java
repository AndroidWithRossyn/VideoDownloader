package com.prox1.video1.download1.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


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
import static com.prox1.video1.download1.util.Utils.RootDirectoryFacebook;
import static com.prox1.video1.download1.util.Utils.createFileFolder;
import static com.prox1.video1.download1.util.Utils.startDownload;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class
SelectGenderActivity extends AppCompatActivity {

    TextView txt_name;
    RadioGroup radioGroupGender;
    ImageView txtSubmit;
    ImageView img_female, img_male, img_male_select, img_female_select;

    public static String PREF_NAME = "PREF_VIDEO";
    public static String NAME_PREF_KEY = "name";
    public static String GENDER_PREF_KEY = "gender";
    LinearLayout linearAdsNative;

    private InterstitialAd mInterstitialAd;
    private NativeAdsSetup nativeAdsBaseApp;
    private boolean checkornot = false;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vc_select_gender);

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

        txt_name = findViewById(R.id.txt_name);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        txtSubmit = findViewById(R.id.txtSubmit);
        img_male = findViewById(R.id.img_male);
        img_female = findViewById(R.id.img_female);
        img_male_select = findViewById(R.id.img_male_select);
        img_female_select = findViewById(R.id.img_female_select);

        boolean isEdit = getIntent().getBooleanExtra("isEdit", false);

        if (isEdit) {
            String displayname = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(NAME_PREF_KEY, "");
            int gender = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(GENDER_PREF_KEY, 1);

            if (gender == 2) {
                radioGroupGender.check(R.id.radioFemale);
            } else if (gender == 3) {
                radioGroupGender.check(R.id.radioOther);
            } else {
                radioGroupGender.check(R.id.radiomale);
            }
        }

        String displayname_1 = "";
        txt_name.setText(displayname_1);

        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.radioFemale) {
                    checkornot = true;
                    img_female.setVisibility(View.GONE);
                    img_female_select.setVisibility(View.VISIBLE);
                    img_male_select.setVisibility(View.GONE);
                    img_male.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.radiomale) {
                    checkornot = true;
                    img_male.setVisibility(View.GONE);
                    img_male_select.setVisibility(View.VISIBLE);
                    img_female_select.setVisibility(View.GONE);
                    img_female.setVisibility(View.VISIBLE);
                }
            }
        });

        img_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkornot = true;
                radioGroupGender.check(R.id.radioFemale);
                img_female.setVisibility(View.GONE);
                img_female_select.setVisibility(View.VISIBLE);
                img_male_select.setVisibility(View.GONE);
                img_male.setVisibility(View.VISIBLE);
            }
        });
        img_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkornot = true;
                radioGroupGender.check(R.id.radiomale);
                img_male.setVisibility(View.GONE);
                img_male_select.setVisibility(View.VISIBLE);
                img_female_select.setVisibility(View.GONE);
                img_female.setVisibility(View.VISIBLE);
            }
        });


        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkornot) {
                    if (Constant.isConnected(getApplicationContext())) {
                        if (!sharedPref.GET_PURCHASED().booleanValue()) {
                            if (mInterstitialAd != null) {
                                final ProgressDialog pd = new ProgressDialog(SelectGenderActivity.this);
                                pd.setMessage("Showing Ads..");
                                pd.setCancelable(false);
                                pd.show();
                                // Show the ad when it's done loading.
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        mInterstitialAd.show(SelectGenderActivity.this);
                                    }
                                }, 2000);
                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        startActivity(new Intent(getApplicationContext(), StartActivity.class));
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
                                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                                finish();
                            }
                        } else {
                            startActivity(new Intent(getApplicationContext(), StartActivity.class));
                            finish();
                        }
                    } else {
                        startActivity(new Intent(getApplicationContext(), StartActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please select your gender", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        if (Constant.isConnected(getApplicationContext())) {
            if (mInterstitialAd != null) {
                if (!sharedPref.GET_PURCHASED().booleanValue()) {
                    final ProgressDialog pd = new ProgressDialog(SelectGenderActivity.this);
                    pd.setMessage("Showing Ads..");
                    pd.setCancelable(false);
                    pd.show();
                    // Show the ad when it's done loading.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            mInterstitialAd.show(SelectGenderActivity.this);
                        }
                    }, 2000);

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            if (MyApplication.is_one_bool.equals("true")) {
                                startActivity(new Intent(getApplicationContext(), Privacy_Policy.class));
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
                    if (MyApplication.is_one_bool.equals("true")) {
                        startActivity(new Intent(getApplicationContext(), Privacy_Policy.class));
                        finish();
                    } else {
                        showExitDialog();
                    }
                }
            } else {
                if (MyApplication.is_one_bool.equals("true")) {
                    startActivity(new Intent(getApplicationContext(), Privacy_Policy.class));
                    finish();
                } else {
                    showExitDialog();
                }
            }
        } else {
            if (MyApplication.is_one_bool.equals("true")) {
                startActivity(new Intent(getApplicationContext(), Privacy_Policy.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        nativeAdsBaseApp.CounterLoadbigNativeAdsBoth();
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
}