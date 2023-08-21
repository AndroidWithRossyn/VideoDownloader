package com.prox1.video1.download1.activity;


import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;


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
import com.prox1.video1.download1.databinding.ActivityMainBinding;
import com.prox1.video1.download1.util.AppLangSessionManager;
import com.prox1.video1.download1.util.ClipboardListener;
import com.prox1.video1.download1.util.SharePrefs;
import com.prox1.video1.download1.util.Utils;
import static com.prox1.video1.download1.util.Utils.RootDirectoryFacebook;
import static com.prox1.video1.download1.util.Utils.createFileFolder;
import static com.prox1.video1.download1.util.Utils.startDownload;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.northghost.caketube.CaketubeTransport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MainActivity activity;
    ActivityMainBinding binding;
    boolean doubleBackToExitPressedOnce = false;
    private ClipboardManager clipBoard;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    String CopyKey = "";
    String CopyValue = "";

    AppLangSessionManager appLangSessionManager;

    private InterstitialAd mInterstitialAd;

    LinearLayout linearAdsNative;
    private NativeAdsSetup nativeAdsBaseApp;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activity = this;
        appLangSessionManager = new AppLangSessionManager(activity);

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

        findViewById(R.id.animation_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), PremiumActivity.class));
//                finish();
            }
        });

        initViews();
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
        activity = this;
        assert activity != null;
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        if (MyApplication.is_third_bool.equals("true")) {
            try {
                connectToVpn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    protected void connectToVpn() {
        if (MyApplication.Country_Code == null) {
            MyApplication.Country_Code = "";
        }
        UnifiedSDK.getInstance().getBackend().isLoggedIn(new com.anchorfree.vpnsdk.callbacks.Callback<Boolean>() {
            public void success(Boolean bool) {
                if (bool) {
                    List<String> fallbackOrder = new ArrayList<>();
                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
                    List<String> bypassDomains = new LinkedList<>();
                    bypassDomains.add("*facebook.com");
                    bypassDomains.add("*wtfismyip.com");
                    bypassDomains.add("*instagram.com");
                    UnifiedSDK.getInstance().getVPN().start(new SessionConfig.Builder()
                            .withReason(TrackingConstants.GprReasons.M_UI)
                            .withTransportFallback(fallbackOrder)
                            .withVirtualLocation(MyApplication.Country_Code)
                            .withTransport(HydraTransport.TRANSPORT_ID)
                            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                            .build(), new CompletableCallback() {
                        @Override
                        public void complete() {
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                        }
                    });
                }
            }

            public void failure(VpnException vpnException) {
            }
        });
    }


    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    public void initViews() {
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        if (activity.getIntent().getExtras() != null) {
            for (String key : activity.getIntent().getExtras().keySet()) {
                CopyKey = key;
                String value = activity.getIntent().getExtras().getString(CopyKey);
                if (CopyKey.equals("android.intent.extra.TEXT")) {
                    CopyValue = activity.getIntent().getExtras().getString(CopyKey);
                    callText(value);
                } else {
                    CopyValue = "";
                    callText(value);
                }
            }
        }
        if (clipBoard != null) {
            clipBoard.addPrimaryClipChangedListener(new ClipboardListener() {
                @Override
                public void onPrimaryClipChanged() {
                    try {
                        showNotification(Objects.requireNonNull(clipBoard.getPrimaryClip().getItemAt(0).getText()).toString());
                    } catch (
                            Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions(0);
        }

        binding.rvLikee.setOnClickListener(this);
        binding.rvInsta.setOnClickListener(this);
        binding.rvWhatsApp.setOnClickListener(this);
        binding.rvTikTok.setOnClickListener(this);
        binding.rvFB.setOnClickListener(this);
        binding.rvTwitter.setOnClickListener(this);
        binding.rvGallery.setOnClickListener(this);
//        binding.rvAbout.setOnClickListener(this);
        binding.rvShareApp.setOnClickListener(this);
        binding.rvRateApp.setOnClickListener(this);
        binding.rvMoreApp.setOnClickListener(this);
        binding.btnPolicy.setOnClickListener(this);

        //TODO :  Change Language Dialog Open
        binding.rvChangeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final BottomSheetDialog dialogSortBy = new BottomSheetDialog(MainActivity.this, R.style.SheetDialog);
                dialogSortBy.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogSortBy.setContentView(R.layout.dialog_language);
                final TextView tv_english = dialogSortBy.findViewById(R.id.tv_english);
                final TextView tv_hindi = dialogSortBy.findViewById(R.id.tv_hindi);
                final TextView tv_cancel = dialogSortBy.findViewById(R.id.tv_cancel);
//                final TextView tvArabic = dialogSortBy.findViewById(R.id.tvArabic);

                dialogSortBy.show();


                tv_english.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setLocale("en");
                        appLangSessionManager.setLanguage("en");
                    }
                });
                tv_hindi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setLocale("hi");
                        appLangSessionManager.setLanguage("hi");
                    }
                });
               /* tvArabic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setLocale("ar");
                        appLangSessionManager.setLanguage("ar");
                    }
                });*/
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogSortBy.dismiss();
                    }
                });

            }
        });


        createFileFolder();

    }

    private void callText(String CopiedText) {
        try {
            if (CopiedText.contains("likee")) {

                try {
                    List<String> extractedUrls = extractUrls(CopiedText);
                    CopyValue = extractedUrls.get(0);

                    Log.d("LIKEEEEE MAIN", CopyValue);
                } catch (Exception ex) {

                }


                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(100);
                } else {
                    callLikeeActivity();
                }
            } else if (CopiedText.contains("instagram.com")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(101);
                } else {
                    callInstaActivity();
                }
            } else if (CopiedText.contains("facebook.com")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(104);
                } else {
                    callFacebookActivity();
                }
            } else if (CopiedText.contains("tiktok.com")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(103);
                } else {
                    callTikTokActivity();
                }
            } else if (CopiedText.contains("twitter.com")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(106);
                } else {
                    callTwitterActivity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = null;

        switch (v.getId()) {
            case R.id.rvLikee:
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (mInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            // Show the ad when it's done loading.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    mInterstitialAd.show(MainActivity.this);
                                }
                            }, 2000);
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        checkPermissions(100);
                                    } else {
                                        callLikeeActivity();
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
                            if (Build.VERSION.SDK_INT >= 23) {
                                checkPermissions(100);
                            } else {
                                callLikeeActivity();
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            checkPermissions(100);
                        } else {
                            callLikeeActivity();
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        checkPermissions(100);
                    } else {
                        callLikeeActivity();
                    }
                }


                break;
            case R.id.rvInsta:
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (mInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            // Show the ad when it's done loading.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    mInterstitialAd.show(MainActivity.this);
                                }
                            }, 2000);
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        checkPermissions(101);
                                    } else {
                                        callInstaActivity();
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
                            if (Build.VERSION.SDK_INT >= 23) {
                                checkPermissions(101);
                            } else {
                                callInstaActivity();
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            checkPermissions(101);
                        } else {
                            callInstaActivity();
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        checkPermissions(101);
                    } else {
                        callInstaActivity();
                    }
                }


                break;

            case R.id.rvWhatsApp:
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (mInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            // Show the ad when it's done loading.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    mInterstitialAd.show(MainActivity.this);
                                }
                            }, 2000);
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        checkPermissions(102);
                                    } else {
                                        callWhatsappActivity();
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
                            if (Build.VERSION.SDK_INT >= 23) {
                                checkPermissions(102);
                            } else {
                                callWhatsappActivity();
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            checkPermissions(102);
                        } else {
                            callWhatsappActivity();
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        checkPermissions(102);
                    } else {
                        callWhatsappActivity();
                    }
                }


                break;
            case R.id.rvTikTok:
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (mInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            // Show the ad when it's done loading.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    mInterstitialAd.show(MainActivity.this);
                                }
                            }, 2000);
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        checkPermissions(103);
                                    } else {
                                        callTikTokActivity();
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
                            if (Build.VERSION.SDK_INT >= 23) {
                                checkPermissions(103);
                            } else {
                                callTikTokActivity();
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            checkPermissions(103);
                        } else {
                            callTikTokActivity();
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        checkPermissions(103);
                    } else {
                        callTikTokActivity();
                    }
                }


                break;
            case R.id.rvFB:
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (mInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            // Show the ad when it's done loading.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    mInterstitialAd.show(MainActivity.this);
                                }
                            }, 2000);
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        checkPermissions(104);
                                    } else {
                                        callFacebookActivity();
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
                            if (Build.VERSION.SDK_INT >= 23) {
                                checkPermissions(104);
                            } else {
                                callFacebookActivity();
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            checkPermissions(104);
                        } else {
                            callFacebookActivity();
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        checkPermissions(104);
                    } else {
                        callFacebookActivity();
                    }
                }


                break;
            case R.id.rvGallery:
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (mInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            // Show the ad when it's done loading.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    mInterstitialAd.show(MainActivity.this);
                                }
                            }, 2000);
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        checkPermissions(105);
                                    } else {
                                        callGalleryActivity();
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
                            if (Build.VERSION.SDK_INT >= 23) {
                                checkPermissions(105);
                            } else {
                                callGalleryActivity();
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            checkPermissions(105);
                        } else {
                            callGalleryActivity();
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        checkPermissions(105);
                    } else {
                        callGalleryActivity();
                    }
                }

                break;
            case R.id.rvTwitter:
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (mInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            // Show the ad when it's done loading.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    mInterstitialAd.show(MainActivity.this);
                                }
                            }, 2000);
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        checkPermissions(106);
                                    } else {
                                        callTwitterActivity();
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
                            if (Build.VERSION.SDK_INT >= 23) {
                                checkPermissions(106);
                            } else {
                                callTwitterActivity();
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            checkPermissions(106);
                        } else {
                            callTwitterActivity();
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        checkPermissions(106);
                    } else {
                        callTwitterActivity();
                    }
                }

                break;
           /* case R.id.rvAbout:
                i = new Intent(activity, AboutUsActivity.class);
                startActivity(i);
                break;*/
            case R.id.rvShareApp:
                Utils.ShareApp(activity);
                break;

            case R.id.rvRateApp:
                Utils.RateApp(activity);
                break;
            case R.id.rvMoreApp:
                Utils.MoreApp(activity);
                break;
            case R.id.btn_policy:
                Utils.showprivacy(activity);
                break;


        }
    }

    public void callLikeeActivity() {
        Intent i = new Intent(activity, LikeeActivity.class);
        i.putExtra("CopyIntent", CopyValue);
        startActivity(i);
    }

    public void callInstaActivity() {
        Intent i = new Intent(activity, InstagramActivity.class);
        i.putExtra("CopyIntent", CopyValue);
        startActivity(i);
    }

    public void callWhatsappActivity() {
        Intent i = new Intent(activity, WhatsappActivity.class);
        startActivity(i);
    }

    public void callTikTokActivity() {
        Intent i = new Intent(activity, TikTokActivity.class);
        i.putExtra("CopyIntent", CopyValue);
        startActivity(i);
    }

    public void callFacebookActivity() {
        Intent i = new Intent(activity, FacebookActivity.class);
        i.putExtra("CopyIntent", CopyValue);
        startActivity(i);

    }

    public void callTwitterActivity() {
        Intent i = new Intent(activity, TwitterActivity.class);
        i.putExtra("CopyIntent", CopyValue);
        startActivity(i);
    }

    public void callGalleryActivity() {
        Intent i = new Intent(activity, GalleryActivity.class);
        startActivity(i);
    }

    public void showNotification(String Text) {
        if (Text.contains("instagram.com") || Text.contains("facebook.com") || Text.contains("tiktok.com")
                || Text.contains("twitter.com") || Text.contains("likee")) {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Notification", Text);
            PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(getResources().getString(R.string.app_name),
                        getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableLights(true);
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(mChannel);
            }
            NotificationCompat.Builder notificationBuilder;
            notificationBuilder = new NotificationCompat.Builder(activity, getResources().getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(getResources().getColor(R.color.black))
                    .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(),
                            R.mipmap.ic_launcher))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle("Copied text")
                    .setContentText(Text)
                    .setChannelId(getResources().getString(R.string.app_name))
                    .setFullScreenIntent(pendingIntent, true);
            notificationManager.notify(1, notificationBuilder.build());
        }
    }

    private boolean checkPermissions(int type) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(activity, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) (activity),
                    listPermissionsNeeded.toArray(new
                            String[listPermissionsNeeded.size()]), type);
            return false;
        } else {
            if (type == 100) {
                callLikeeActivity();
            } else if (type == 101) {
                callInstaActivity();
            } else if (type == 102) {
                callWhatsappActivity();
            } else if (type == 103) {
                callTikTokActivity();
            } else if (type == 104) {
                callFacebookActivity();
            } else if (type == 105) {
                callGalleryActivity();
            } else if (type == 106) {
                callTwitterActivity();
            }

        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callLikeeActivity();
            } else {
            }
            return;
        } else if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callInstaActivity();
            } else {
            }
            return;
        } else if (requestCode == 102) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callWhatsappActivity();
            } else {
            }
            return;
        } else if (requestCode == 103) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callTikTokActivity();
            } else {
            }
            return;
        } else if (requestCode == 104) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callFacebookActivity();
            } else {
            }
            return;
        } else if (requestCode == 105) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callGalleryActivity();
            } else {
            }
            return;
        } else if (requestCode == 106) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callTwitterActivity();
            } else {
            }
            return;
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SelectModuleCategoryActivity.class));
        finish();
    }


    //TODO :  Using for Set Locale
    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);


        Intent refresh = new Intent(MainActivity.this, MainActivity.class);
        startActivity(refresh);
        finish();
    }


}
