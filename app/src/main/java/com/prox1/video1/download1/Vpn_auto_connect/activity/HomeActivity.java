package com.prox1.video1.download1.Vpn_auto_connect.activity;


import static com.prox1.video1.download1.Vpn_auto_connect.activity.VPNActivity.selectedCountry;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.pixplicity.easyprefs.library.Prefs;
import com.prox1.video1.download1.Ads.BannerAdsSetup;
import com.prox1.video1.download1.Ads.NativeAdsSetup;
import com.prox1.video1.download1.BuildConfig;
import com.prox1.video1.download1.Constant;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.APIClient;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.ApiResponse;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.Convert;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.Preference;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.RestApis;
import com.prox1.video1.download1.activity.SelectModuleCategoryActivity;


import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public abstract class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static final String TAG = VPNActivity.class.getSimpleName();
    public static Menu menuItem;
    VPNState state;
    int progressBarValue = 0;
    Handler handler = new Handler();
    @Nullable
    @BindView(R.id.lottiConnectStatus)
    LottieAnimationView lottiConnectStatus;

    @Nullable
    @BindView(R.id.connect_btn)
    ImageView connectBtnTextView;

    @Nullable
    @BindView(R.id.connection_state)
    ImageView connectionStateTextView;

    @Nullable
    @BindView(R.id.connection_progress)
    ProgressBar connectionProgressBar;
    private Handler customHandler = new Handler();
    private UnifiedNativeAd nativeAd;
    @BindView(R.id.server_ip)
    TextView server_ip;

    @BindView(R.id.uploading_speed)
    TextView uploading_speed_textview;

    @BindView(R.id.downloading_speed)
    TextView downloading_speed_textview;

    @BindView(R.id.vpn_country_image)
    ImageView selectedServerImage;

    @BindView(R.id.vpn_country_name)
    TextView selectedServerName;

    Preference preference;

    public static String STATUS = "";
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    protected abstract void checkRemainingTraffic();

    protected abstract void loginToVpn();

//    private DrawerLayout drawer;

    private InterstitialAd mInterstitialAd;
    private InterstitialAd secondmInterstitialAd;

    LinearLayout linearAdsNative;
    private NativeAdsSetup nativeAdsBaseApp;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home_main);

        Constant.FullScreencall(this);

        ButterKnife.bind(this);

        sharedPref = new SharedPref(getApplicationContext());

//        OneSignal.startInit(this).init();
        loginToVpn();

        preference = new Preference(this);

        /*final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
        pd.setMessage("Ads Loading..");
        pd.setCancelable(true);
        pd.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
            }
        }, 2000);*/

        /*--------------------------BannerAds-------------------*/
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            new BannerAdsSetup(this).admobbannerads();
        }

        /*--------------------------NativeAds-------------------*/
        linearAdsNative = findViewById(R.id.linearAdsNative);
        nativeAdsBaseApp = new NativeAdsSetup(this, linearAdsNative, null);
        nativeAdsBaseApp.loadbigbothnativeads();

        /*--------------------------InterstitialAds-------------------*/
        loadAdmobInterstitialAds();
        secondloadAdmobInterstitialAds();


        /*--------------------------IronSourceAds-------------------*/
        IronSource.init(this, MyApplication.iron_id);

        if (!IronSource.isInterstitialReady()) {
            IronSource.loadInterstitial();
        }

        ImageView img_quick = findViewById(R.id.imgquick);
        img_quick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (secondmInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    secondmInterstitialAd.show(HomeActivity.this);
                                }
                            }, 1000);
                            secondmInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    secondmInterstitialAd = null;
                                    Log.d("TAG", "The ad was dismissed.");
                                    connectToVpn();
                                    secondloadAdmobInterstitialAds();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    secondloadAdmobInterstitialAds();
                                }
                            });
                        } else {
                            if (IronSource.isInterstitialReady()) {
                                final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
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
                                        connectToVpn();
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
                                connectToVpn();
                            }
                        }
                    } else {
                        connectToVpn();
                    }
                } else {
                    connectToVpn();
                }
            }
        });
        ImageView img_country = findViewById(R.id.imgcountry);
        img_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (mInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    mInterstitialAd.show(HomeActivity.this);
                                }
                            }, 1000);
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    mInterstitialAd = null;
                                    Log.d("TAG", "The ad was dismissed.");
                                    startActivity(new Intent(HomeActivity.this, ServerActivity.class));
                                    loadAdmobInterstitialAds();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    loadAdmobInterstitialAds();
                                }
                            });
                        } else {
                            startActivity(new Intent(HomeActivity.this, ServerActivity.class));
                        }
                    } else {
                        startActivity(new Intent(HomeActivity.this, ServerActivity.class));
                    }
                } else {
                    startActivity(new Intent(HomeActivity.this, ServerActivity.class));
                }
            }
        });
        ImageView img_menu = findViewById(R.id.imgmenu);
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isConnected(getApplicationContext())) {
                    if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        if (secondmInterstitialAd != null) {
                            final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    secondmInterstitialAd.show(HomeActivity.this);
                                }
                            }, 1000);
                            secondmInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    secondmInterstitialAd = null;
                                    Log.d("TAG", "The ad was dismissed.");
                                    startActivity(new Intent(HomeActivity.this, MenuActivity.class));
                                    secondloadAdmobInterstitialAds();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    secondloadAdmobInterstitialAds();
                                }
                            });
                        } else {
                            if (IronSource.isInterstitialReady()) {
                                final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
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
                                        startActivity(new Intent(HomeActivity.this, MenuActivity.class));
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
                                startActivity(new Intent(HomeActivity.this, MenuActivity.class));
                            }
                        }
                    } else {
                        startActivity(new Intent(HomeActivity.this, MenuActivity.class));
                    }
                } else {
                    startActivity(new Intent(HomeActivity.this, MenuActivity.class));
                }
            }
        });
        ImageView img_rate = findViewById(R.id.imgrate);
        img_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + HomeActivity.this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + HomeActivity.this.getPackageName())));
                }
            }
        });

        if (Prefs.contains("connectStart") && Prefs.getString("connectStart", "").equals("on")) {

            isConnected(new Callback<Boolean>() {
                @Override
                public void success(@NonNull Boolean aBoolean) {
                    if (aBoolean) {
                        STATUS = "Disconnect";
                        disconnectAlert();

                    } else {
                        STATUS = "Connect";
                        updateUI();
                        connectToVpn();
                    }
                }

                @Override
                public void failure(@NonNull VpnException e) {
                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), 1).show();
                }

            });
        }

        getip();
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

    public void secondloadAdmobInterstitialAds() {
        InterstitialAd.load(this, MyApplication.interstitial_2, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        secondmInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        secondmInterstitialAd = null;
                        secondloadAdmobInterstitialAds();
                    }
                });
    }

    public void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    @Override
    public void onBackPressed() {
        if (Constant.isConnected(getApplicationContext())) {
            if (!sharedPref.GET_PURCHASED().booleanValue()) {
                if (secondmInterstitialAd != null) {
                    final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
                    pd.setMessage("Showing Ads..");
                    pd.setCancelable(false);
                    pd.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            secondmInterstitialAd.show(HomeActivity.this);
                        }
                    }, 1000);
                    secondmInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            secondmInterstitialAd = null;
                            Log.d("TAG", "The ad was dismissed.");
                            startActivity(new Intent(getApplicationContext(), SelectModuleCategoryActivity.class));
                            finish();
                            secondloadAdmobInterstitialAds();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            secondloadAdmobInterstitialAds();
                        }
                    });
                } else {
                    if (IronSource.isInterstitialReady()) {
                        final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
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
                                startActivity(new Intent(getApplicationContext(), SelectModuleCategoryActivity.class));
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
                        startActivity(new Intent(getApplicationContext(), SelectModuleCategoryActivity.class));
                        finish();
                    }
                }
            } else {
                startActivity(new Intent(getApplicationContext(), SelectModuleCategoryActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(getApplicationContext(), SelectModuleCategoryActivity.class));
            finish();
        }
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        menuItem = menu;
        if (selectedCountry != null)
            if (!selectedCountry.equalsIgnoreCase(""))
                menuItem.findItem(R.id.action_glob).setIcon(this.getResources().getIdentifier(selectedCountry.toLowerCase(), "drawable", this.getPackageName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_glob) {

            startActivity(new Intent(this, ServerActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
//        if the application again available from background state...
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }

        });
        nativeAdsBaseApp.CounterLoadbigNativeAdsBoth();
        if (!IronSource.isInterstitialReady()) {
            IronSource.loadInterstitial();
        }

    }

    @Override
    protected void onPause() {
//        application in the background state...
        super.onPause();
        stopUIUpdateTask();
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }

    @OnClick(R.id.vpn_select_country)
    public void showRegionDialog() {
        if (Constant.isConnected(getApplicationContext())) {
            if (!sharedPref.GET_PURCHASED().booleanValue()) {
                if (mInterstitialAd != null) {
                    final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
                    pd.setMessage("Showing Ads..");
                    pd.setCancelable(false);
                    pd.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            mInterstitialAd.show(HomeActivity.this);
                        }
                    }, 1000);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            mInterstitialAd = null;
                            Log.d("TAG", "The ad was dismissed.");
//                        startActivity(new Intent(HomeActivity.this, ServersActivity.class));
                            chooseServer();
                            loadAdmobInterstitialAds();

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            loadAdmobInterstitialAds();
                        }
                    });
                } else {
//                startActivity(new Intent(HomeActivity.this, ServersActivity.class));
                    chooseServer();
                }
            } else {
                chooseServer();
            }
        } else {
//            startActivity(new Intent(HomeActivity.this, ServersActivity.class));
            chooseServer();
        }
    }

    @OnClick(R.id.connect_btn)
    public void onConnectBtnClick(View v) {
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    STATUS = "Disconnect";
                    if (Constant.isConnected(getApplicationContext())) {
                        if (!sharedPref.GET_PURCHASED().booleanValue()) {
                            if (mInterstitialAd != null) {
                                final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
                                pd.setMessage("Showing Ads..");
                                pd.setCancelable(false);
                                pd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        mInterstitialAd.show(HomeActivity.this);
                                    }
                                }, 1000);
                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        mInterstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                        disconnectAlert();
                                        loadAdmobInterstitialAds();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);
                                        loadAdmobInterstitialAds();
                                    }
                                });
                            } else {
                                disconnectAlert();
                            }
                        } else {
                            disconnectAlert();
                        }
                    } else {
                        disconnectAlert();
                    }
                } else {
                    STATUS = "Connect";
                    if (Constant.isConnected(getApplicationContext())) {
                        if (!sharedPref.GET_PURCHASED().booleanValue()) {
                            if (mInterstitialAd != null) {
                                final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
                                pd.setMessage("Showing Ads..");
                                pd.setCancelable(false);
                                pd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        mInterstitialAd.show(HomeActivity.this);
                                    }
                                }, 1000);
                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        mInterstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                        updateUI();
                                        connectToVpn();
                                        loadAdmobInterstitialAds();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);
                                        loadAdmobInterstitialAds();
                                    }
                                });
                            } else {
                                updateUI();
                                connectToVpn();
                            }
                        } else {
                            updateUI();
                            connectToVpn();
                        }
                    } else {
                        updateUI();
                        connectToVpn();
                    }
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    protected void startUIUpdateTask() {
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }

    protected void updateUI() {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                state = vpnState;
                switch (vpnState) {
                    case IDLE: {
                        connectionStateTextView.setImageResource(R.drawable.disc);
                        getip();
                        loadIcon();
                        selectedServerImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerName.setText(R.string.select_country);
                        uploading_speed_textview.setText("");
                        downloading_speed_textview.setText("");
                        server_ip.setText(R.string.default_server_ip_text);
                        connectBtnTextView.setEnabled(true);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {
                        loadIcon();
                        connectBtnTextView.setEnabled(true);
                        connectionStateTextView.setImageResource(R.drawable.conne);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        loadIcon();
                        connectionStateTextView.setImageResource(R.drawable.connecting);
                        selectedServerImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerName.setText(R.string.select_country);
                        connectBtnTextView.setEnabled(false);
                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
                        connectBtnTextView.setBackgroundResource(R.drawable.ic_hare_connect);
                        selectedServerImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerName.setText(R.string.select_country);
                        break;
                    }
                }
            }

            @Override
            public void failure(VpnException e) {

            }
        });

        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        selectedServerImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerName.setText(R.string.select_country);
                        if (!currentServer.equals("")) {
                            Locale locale = new Locale("", currentServer);
                            Resources resources = getResources();
                            String sb = "drawable/" + currentServer.toLowerCase();
                            selectedServerImage.setImageResource(resources.getIdentifier(sb, null, getPackageName()));
                            selectedServerName.setText(locale.getDisplayCountry());
                        } else {
                            selectedServerImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                            selectedServerName.setText(R.string.select_country);
                        }
                    }
                });
            }

            @Override
            public void failure(@NonNull VpnException e) {
                selectedServerImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                selectedServerName.setText(R.string.select_country);
            }
        });
    }

    protected void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Convert.humanReadableByteCountOld(outBytes, false);
        String inString = Convert.humanReadableByteCountOld(inBytes, false);

        uploading_speed_textview.setText(inString);
        downloading_speed_textview.setText(outString);

    }

    private void getip() {
        RestApis mRestApis = APIClient.getRetrofitInstance("https://api.ipify.org").create(RestApis.class);
        Call<ApiResponse> userAdd = mRestApis.requestip("json");
        userAdd.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.e(TAG, "onResponse: " + response.body().getIp());
                if (response != null) {
                    server_ip.setText(response.body().getIp());
                } else {
                    server_ip.setText(R.string.default_server_ip_text);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                server_ip.setText(R.string.default_server_ip_text);
            }
        });
    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
            //trafficLimitTextView.setText("UNLIMITED available");
        } else {
            String trafficUsed = Convert.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Convert.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";
        }
    }

    protected void showConnectProgress() {

        connectionProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {
                    progressBarValue++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            connectionProgressBar.setProgress(progressBarValue);
                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    protected void ShowIPaddera(String ipaddress) {
        server_ip.setText(ipaddress);
    }

    protected void hideConnectProgress() {
        connectionProgressBar.setVisibility(View.GONE);
//        connectionStateTextView.setVisibility(View.VISIBLE);
    }

    protected void showMessage(String msg) {
        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/vpn/details?id=" + this.getPackageName())));
        }
    }

    protected void loadIcon() {
        if (state == VPNState.IDLE) {
            if (lottiConnectStatus.getVisibility() == View.VISIBLE) {
                lottiConnectStatus.setVisibility(View.GONE);
                lottiConnectStatus.cancelAnimation();
            }
            Glide.with(this).load(R.drawable.ic_hare_connect).into(connectBtnTextView);
            Glide.with(this).load(R.drawable.ic_hare_connect).into(connectBtnTextView);

        } else if (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {
            if (lottiConnectStatus.getVisibility() == View.GONE) {
                lottiConnectStatus.setVisibility(View.VISIBLE);
                lottiConnectStatus.setAnimation(R.raw.connect_status);
                lottiConnectStatus.playAnimation();
            }
            connectBtnTextView.setVisibility(View.VISIBLE);
            Glide.with(this).load(R.drawable.rabbit).into(connectBtnTextView);

        } else if (state == VPNState.CONNECTED) {
            if (lottiConnectStatus.getVisibility() == View.VISIBLE) {
                lottiConnectStatus.setVisibility(View.GONE);
                lottiConnectStatus.cancelAnimation();
            }
            Glide.with(this).load(R.drawable.ic_hare_connected).into(connectBtnTextView);
            Glide.with(this).load(R.drawable.ic_hare_connected).into(connectBtnTextView);
            connectBtnTextView.setVisibility(View.VISIBLE);

        }
    }

    protected void disconnectAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Do you want to disconnet?");

        builder.setPositiveButton("Disconnect",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        disconnectFromVnp();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

}
