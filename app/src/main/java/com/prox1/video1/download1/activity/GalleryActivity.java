package com.prox1.video1.download1.activity;

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
import com.prox1.video1.download1.databinding.ActivityGalleryBinding;
import com.prox1.video1.download1.fragment.FBDownloadedFragment;
import com.prox1.video1.download1.fragment.InstaDownloadedFragment;
import com.prox1.video1.download1.fragment.TwitterDownloadedFragment;
import com.prox1.video1.download1.fragment.WhatsAppDowndlededFragment;
import com.prox1.video1.download1.util.AppLangSessionManager;
import com.prox1.video1.download1.util.SharePrefs;
import com.prox1.video1.download1.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.prox1.video1.download1.util.Utils.createFileFolder;


public class GalleryActivity extends AppCompatActivity {
    GalleryActivity activity;
    ActivityGalleryBinding binding;

    AppLangSessionManager appLangSessionManager;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);
        activity = this;

        Constant.FullScreencall(this);

        sharedPref = new SharedPref(getApplicationContext());

        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager.getLanguage());


        /*--------------------------BannerAds-------------------*/
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            new BannerAdsSetup(this).admobbannerads();
        }

        initViews();
    }

    public void initViews() {
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

        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        createFileFolder();
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new InstaDownloadedFragment(), "Instagram");
        adapter.addFragment(new WhatsAppDowndlededFragment(), "Whatsapp");
//        adapter.addFragment(new TikTokDownloadedFragment(), "TikTok");
        adapter.addFragment(new FBDownloadedFragment(), "Facebook");
        adapter.addFragment(new TwitterDownloadedFragment(), "Twitter");
//        adapter.addFragment(new LikeeDownloadedFragment(), "Likee");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);

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
