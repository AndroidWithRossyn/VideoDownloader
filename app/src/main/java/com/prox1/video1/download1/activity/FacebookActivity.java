package com.prox1.video1.download1.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.ironsource.mediationsdk.IronSource;

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
import com.prox1.video1.download1.util.AppLangSessionManager;
import com.prox1.video1.download1.util.SharePrefs;
import com.prox1.video1.download1.util.Utils;
import static com.prox1.video1.download1.util.Utils.RootDirectoryFacebook;
import static com.prox1.video1.download1.util.Utils.createFileFolder;
import static com.prox1.video1.download1.util.Utils.startDownload;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.ContentValues.TAG;



public class FacebookActivity extends AppCompatActivity {
    ActivityFacebookBinding binding;
    FacebookActivity activity;
    CommonClassForAPI commonClassForAPI;
    private String VideoUrl;
    private ClipboardManager clipBoard;

    AppLangSessionManager appLangSessionManager;
    private InterstitialAd mInterstitialAd;
    LinearLayout linearAdsNative;
    private NativeAdsSetup nativeAdsBaseApp;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_facebook);
        activity = this;

        Constant.FullScreencall(this);

        sharedPref = new SharedPref(getApplicationContext());

        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager.getLanguage());

        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();
        initViews();

        InterstitialAdsINIT();
        /*--------------------------BannerAds-------------------*/
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            new BannerAdsSetup(this).admobbannerads();
        }

        /*--------------------------NativeAds-------------------*/
        linearAdsNative = findViewById(R.id.linearAdsNative);
        nativeAdsBaseApp = new NativeAdsSetup(this, linearAdsNative, null);
        nativeAdsBaseApp.loadexitandcallingbothnativeads();


        /*--------------------------IronSourceAds-------------------*/
        IronSource.init(this, MyApplication.iron_id);

        if (!IronSource.isInterstitialReady()) {
            IronSource.loadInterstitial();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        assert activity != null;
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        PasteText();
        InterstitialAdsINIT();
        if (!IronSource.isInterstitialReady()) {
            IronSource.loadInterstitial();
        }
    }

    private void initViews() {
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        binding.imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.imInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE);
            }
        });


        Glide.with(activity)
                .load(R.drawable.fb1)
                .into(binding.layoutHowTo.imHowto1);

        Glide.with(activity)
                .load(R.drawable.fb2)
                .into(binding.layoutHowTo.imHowto2);

        Glide.with(activity)
                .load(R.drawable.fb3)
                .into(binding.layoutHowTo.imHowto3);

        Glide.with(activity)
                .load(R.drawable.fb4)
                .into(binding.layoutHowTo.imHowto4);


        binding.layoutHowTo.tvHowTo1.setText(getResources().getString(R.string.opn_fb));
        binding.layoutHowTo.tvHowTo3.setText(getResources().getString(R.string.copy_video_link_frm_fb));

        if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISSHOWHOWTOFB)) {
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.ISSHOWHOWTOFB, true);
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE);
        } else {
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.GONE);
        }

        binding.loginBtn1.setOnClickListener(v -> {
            String LL = binding.etText.getText().toString();
            if (LL.equals("")) {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            } else if (!Patterns.WEB_URL.matcher(LL).matches()) {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            } else {
                GetFacebookData();
                showInterstitial();
            }
        });

        binding.tvPaste.setOnClickListener(v -> {
            PasteText();
        });
        binding.LLOpenFacebbook.setOnClickListener(v -> {
            Utils.OpenApp(activity, "com.facebook.katana");
        });


    }

    private void GetFacebookData() {
        try {
            createFileFolder();
            URL url = new URL(binding.etText.getText().toString());
            String host = url.getHost();
            Log.e("initViews: ", host);

            if (host.contains("facebook.com") || host.contains("fb.com") || host.contains("fb.watch") || host.contains("fb.gg")) {
                Utils.showProgressDialog(activity);
                new callGetFacebookData().execute(binding.etText.getText().toString());
            } else {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PasteText() {
        try {
            binding.etText.setText("");
            String CopyIntent = getIntent().getStringExtra("CopyIntent");
            if (CopyIntent.equals("")) {
                if (!(clipBoard.hasPrimaryClip())) {

                } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("facebook.com") ||
                            clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("fb.com") ||
                            clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("fb.watch") ||
                            clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("fb.gg")) {
                        binding.etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("facebook.com") ||
                            item.getText().toString().contains("fb.com") ||
                            item.getText().toString().contains("fb.watch") ||
                            item.getText().toString().contains("fb.gg")) {
                        binding.etText.setText(item.getText().toString());
                    }

                }
            } else {
                if (CopyIntent.contains("facebook.com") ||
                        CopyIntent.contains("fb.com") ||
                        CopyIntent.contains("fb.watch") ||
                        CopyIntent.contains("fb.gg")
                ) {
                    binding.etText.setText(CopyIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class callGetFacebookData extends AsyncTask<String, Void, Document> {
        Document facebookDoc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... urls) {
            try {
                facebookDoc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Error");
            }
            return facebookDoc;
        }

        protected void onPostExecute(Document result) {
            Utils.hideProgressDialog(activity);
            try {

                VideoUrl = result.select("meta[property=\"og:video\"]").last().attr("content");
                Log.e("onPostExecute: ", VideoUrl);
                if (!VideoUrl.equals("")) {
                    try {
                        startDownload(VideoUrl, RootDirectoryFacebook, activity, getFilenameFromURL(VideoUrl));
                        VideoUrl = "";
                        binding.etText.setText("");
                    } catch (Exception e) {
                        // Login Failed
                        Toast makeText;
                        try {
                            if (!binding.etText.getText().toString().trim().equals("")) {
                                makeText = Toast.makeText(getApplicationContext(), R.string.no_media_on_facebook, 0);
                                makeText.show();
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                } else {
                    // Login Failed
                    Toast makeText;
                    try {
                        if (!binding.etText.getText().toString().trim().equals("")) {
                            makeText = Toast.makeText(getApplicationContext(), R.string.no_media_on_facebook, 0);
                            makeText.show();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

    }

    public String getFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath()).getName() + ".mp4";
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".mp4";
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

    public void InterstitialAdsINIT() {

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
                        InterstitialAdsINIT();
                    }
                });

    }

    //InterstitialAd : Start

    private void showInterstitial() {
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            if (mInterstitialAd != null) {
                final ProgressDialog pd = new ProgressDialog(FacebookActivity.this);
                pd.setMessage("Showing Ads..");
                pd.setCancelable(false);
                pd.show();
                // Show the ad when it's done loading.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        mInterstitialAd.show(FacebookActivity.this);
                    }
                }, 2000);
            } else {
                if (IronSource.isInterstitialReady()) {
                    final ProgressDialog pd = new ProgressDialog(FacebookActivity.this);
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
                    }, 2000);
                }
            }
        }
    }

}