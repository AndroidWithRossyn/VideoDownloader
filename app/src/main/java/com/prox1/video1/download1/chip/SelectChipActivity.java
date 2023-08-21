package com.prox1.video1.download1.chip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.prox1.video1.download1.Ads.BannerAdsSetup;
import com.prox1.video1.download1.Ads.NativeAdsSetup;
import com.prox1.video1.download1.Constant;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;
import com.prox1.video1.download1.activity.SelectModuleCategoryActivity;
import com.prox1.video1.download1.activity.StartActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectChipActivity extends Activity {

    @BindView(R.id.rv_interest_single)
    ChipRecyclerView rvInterestSingle;
    @BindView(R.id.rv_interest_multi)
    ChipRecyclerView rvInterestMulti;
    Button btn_category_next;

    private ArrayList<String> interestList = new ArrayList<>();
    private String interestString = "";
    LinearLayout linearAdsNative;
    private NativeAdsSetup nativeAdsBaseApp;
    private InterstitialAd mInterstitialAd;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chip);
        ButterKnife.bind(this);


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

        setInterestAdapterSingle();
        setInterestAdapterMulti();
        btn_category_next = (Button) findViewById(R.id.btn_category_next);
        btn_category_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (interestList.size() > 0) {
                    if (Constant.isConnected(getApplicationContext())) {
                        if (!sharedPref.GET_PURCHASED().booleanValue()) {
                            if (mInterstitialAd != null) {
                                final ProgressDialog pd = new ProgressDialog(SelectChipActivity.this);
                                pd.setMessage("Showing Ads..");
                                pd.setCancelable(false);
                                pd.show();
                                // Show the ad when it's done loading.
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        mInterstitialAd.show(SelectChipActivity.this);
                                    }
                                }, 2000);

                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        startActivity(new Intent(getApplicationContext(), SelectModuleCategoryActivity.class));
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

                                startActivity(new Intent(getApplicationContext(), SelectModuleCategoryActivity.class));
                                finish();

                            }
                        } else {
                            startActivity(new Intent(getApplicationContext(), SelectModuleCategoryActivity.class));
                            finish();
                        }
                    } else {

                        startActivity(new Intent(getApplicationContext(), SelectModuleCategoryActivity.class));
                        finish();

                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Please select atleast one category", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
        finish();
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

    public void setInterestAdapterSingle() {
        List<UserListData> userListData = new ArrayList<>();
        String[] interestArray = getResources().getStringArray(R.array.interest_array);
        for (int i = 0; i < interestArray.length; i++) {
            UserListData guestUserListData = new UserListData();
            guestUserListData.setName(interestArray[i]);
            guestUserListData.setSelected(false);
            userListData.add(guestUserListData);
        }
        InterestAdapter interestAdapterSingle = new InterestAdapter(this,
                userListData, rvInterestSingle.isMultiChoiceMode());
        rvInterestSingle.setAdapter(interestAdapterSingle);
    }


    public void setInterestAdapterMulti() {
        List<UserListData> userListData = new ArrayList<>();
        String[] interestArray = getResources().getStringArray(R.array.interest_array);
        for (int i = 0; i < interestArray.length; i++) {
            UserListData guestUserListData = new UserListData();
            guestUserListData.setName(interestArray[i]);
            guestUserListData.setSelected(false);
            userListData.add(guestUserListData);
        }
        InterestAdapter interestAdapterMulti = new InterestAdapter(this,
                userListData, rvInterestMulti.isMultiChoiceMode());
        rvInterestMulti.setAdapter(interestAdapterMulti);
    }

    public void selectGuestUserListData(List<UserListData> modifiedListUserData) {
        interestList = new ArrayList<>();
        for (int i = 0; i < modifiedListUserData.size(); i++) {
            if (modifiedListUserData.get(i).isSelected()) {
                interestList.add(modifiedListUserData.get(i).getName());
            }

        }
        interestString = interestList.toString().replaceAll("[\\[.\\].\\s+]", "");
    }

}
