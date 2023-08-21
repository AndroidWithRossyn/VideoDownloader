package com.prox1.video1.download1.activity;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.prox1.video1.download1.Ads.BannerAdsSetup;
import com.prox1.video1.download1.Ads.NativeAdsSetup;
import com.prox1.video1.download1.Constant;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;
import com.prox1.video1.download1.api.CommonClassForAPI;
import com.prox1.video1.download1.databinding.ActivityFacebookBinding;
import com.prox1.video1.download1.databinding.ActivityLowStorageWhatsappBinding;
import com.prox1.video1.download1.fragment.WhatsappImageFragment;
import com.prox1.video1.download1.fragment.WhatsappVideoFragment;
import com.prox1.video1.download1.util.AppLangSessionManager;
import com.prox1.video1.download1.util.SharePrefs;
import com.prox1.video1.download1.util.Utils;
import static com.prox1.video1.download1.util.Utils.RootDirectoryFacebook;
import static com.prox1.video1.download1.util.Utils.createFileFolder;
import static com.prox1.video1.download1.util.Utils.startDownload;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WhatsappLowStorageActivity extends AppCompatActivity {

    private ActivityLowStorageWhatsappBinding binding;
    private WhatsappLowStorageActivity activity;
    AppLangSessionManager appLangSessionManager;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = (ActivityLowStorageWhatsappBinding) DataBindingUtil.setContentView(this, R.layout.activity_low_storage_whatsapp);
        activity = this;

        sharedPref = new SharedPref(getApplicationContext());

        Constant.FullScreencall(this);

        createFileFolder();
        initViews();
        AppLangSessionManager appLangSessionManager2 = new AppLangSessionManager(activity);
        appLangSessionManager = appLangSessionManager2;
//        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager2.getLanguage());

//        runtimePermission();

        /*----------------------BannerAds-------------------*/
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            new BannerAdsSetup(this).admobbannerads();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }


    private void initViews() {

        setupViewPager(binding.viewpager);
        binding.tabs.setupWithViewPager(binding.viewpager);
        binding.imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        for (int i = 0; i < binding.tabs.getTabCount(); i++) {
            TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
            binding.tabs.getTabAt(i).setCustomView(tv);
        }

        binding.LLOpenWhatsapp.setOnClickListener(v -> {
            Utils.OpenApp(activity, "com.whatsapp");
        });
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new WhatsappImageFragment(), getResources().getString(R.string.images));
        adapter.addFragment(new WhatsappVideoFragment(), getResources().getString(R.string.videos));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }
}
