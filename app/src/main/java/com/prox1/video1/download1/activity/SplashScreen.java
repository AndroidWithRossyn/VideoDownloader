package com.prox1.video1.download1.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.pesonal.adsdk.ADS_SplashActivity;
import com.pesonal.adsdk.getDataListner;
import com.prox1.video1.download1.Ads.Ads;
import com.prox1.video1.download1.Ads.AppOpenManager;
import com.prox1.video1.download1.Ads.RetrofitClient;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;
import com.prox1.video1.download1.Vpn_auto_connect.activity.MainActivity_VPN;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends ADS_SplashActivity {

    SplashScreen activity;
    Context context;
    Handler handlerforlongtime;

    private InterstitialAd splashadsmInterstitialAd;

    public static SharedPref sharedPref;

    private AppOpenAd appOpenAd = null;

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private int storagecheck = 114;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = activity = this;

        sharedPref = new SharedPref(getApplicationContext());
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            AppOpenManager.isShowingAd = true;
        }

        if (!isOnline(getApplicationContext())) {
            showAlertDialog();
        } else {
            next();
        }
        return;
    }

    private void getAds() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("app_package", getPackageName())
                .build();

        Call<List<Ads>> call = RetrofitClient.getInstance().getMyApi().getAds(requestBody);
        call.enqueue(new Callback<List<Ads>>() {
            @Override
            public void onResponse(Call<List<Ads>> call, Response<List<Ads>> response) {
                List<Ads> heroList = response.body();

                if (response.isSuccessful()) {
                    for (int i = 0; i < heroList.size(); i++) {
                        String s = response.body().get(i).getApp_package();
                        String s1 = response.body().get(i).getAppName();
                        MyApplication.App_version = response.body().get(i).getApp_version();
                        MyApplication.app_open = response.body().get(i).getApp_open();
                        MyApplication.banner = response.body().get(i).getBanner();
                        MyApplication.interstitial_1 = response.body().get(i).getInterstitial_1();
                        MyApplication.interstitial_2 = response.body().get(i).getInterstitial_2();
                        MyApplication.nativeeads = response.body().get(i).getNative_ads();
                        MyApplication.is_one_bool = response.body().get(i).getIs_one_bool();
                        MyApplication.is_two_bool = response.body().get(i).getIs_two_bool();
                        MyApplication.is_third_bool = response.body().get(i).getIs_third_bool();
                        MyApplication.is_fourth_bool = response.body().get(i).getIs_fourth_bool();
                        String s6 = response.body().get(i).getRewarded();
                        Log.e("autolog", s);
                    }
//                    loadFullscreen();
                    appopen();
                } else {
                    handlerforlongtime.removeCallbacksAndMessages(null);
                    HomeScreen();
                }
            }

            @Override
            public void onFailure(Call<List<Ads>> call, Throwable t) {
                handlerforlongtime.removeCallbacksAndMessages(null);
                HomeScreen();
                t.printStackTrace();
            }

        });
    }

    public void appopen() {
        AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            @Override
            public void onAdLoaded(AppOpenAd ad) {
                appOpenAd = ad;

                FullScreenContentCallback fullScreenContentCallback =
                        new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Set the reference to null so isAdAvailable() returns false.
                                appOpenAd = null;
                                handlerforlongtime.removeCallbacksAndMessages(null);
                                HomeScreen();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                appOpenAd = null;
                                handlerforlongtime.removeCallbacksAndMessages(null);
                                HomeScreen();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                handlerforlongtime.removeCallbacksAndMessages(null);
                            }
                        };
                appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                if (appOpenAd != null) {
                    appOpenAd.show(SplashScreen.this);
                }
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                // Handle the error.
                appOpenAd = null;
                handlerforlongtime.removeCallbacksAndMessages(null);
                HomeScreen();
            }

        };
        AppOpenAd.load(
                this, MyApplication.app_open, new AdRequest.Builder().build(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);

    }

    public void loadFullscreen() {
        InterstitialAd.load(SplashScreen.this, MyApplication.interstitial_2, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                splashadsmInterstitialAd = interstitialAd;
                handlerforlongtime.removeCallbacksAndMessages(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!sharedPref.GET_PURCHASED().booleanValue()) {

                            final ProgressDialog pd = new ProgressDialog(SplashScreen.this);
                            pd.setMessage("Showing Ads..");
                            pd.setCancelable(false);
                            pd.show();
                            // Show the ad when it's done loading.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    splashadsmInterstitialAd.show(SplashScreen.this);
                                }
                            }, 2000);

                            splashadsmInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    splashadsmInterstitialAd = null;
                                    handlerforlongtime.removeCallbacksAndMessages(null);
                                    HomeScreen();
                                    Log.d("TAG", "The ad was dismissed.");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    splashadsmInterstitialAd = null;
                                    handlerforlongtime.removeCallbacksAndMessages(null);
                                    HomeScreen();
                                    Log.d("TAG", "The ad failed to show.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent();
                                    // Called when fullscreen content is shown.
                                    Log.d("TAG", "The ad was shown.");
                                }

                            });
                        } else {
                            HomeScreen();
                        }
                    }
                }, 2000);
            }


            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                splashadsmInterstitialAd = null;
                handlerforlongtime.removeCallbacksAndMessages(null);
                HomeScreen();
                String error = String.format("domain: %s, code: %d, message: %s", loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
//                Toast.makeText(SplashScreen.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }

    public void HomeScreen() {
        ADSinit(SplashScreen.this, getCurrentVersionCode(), new getDataListner() {
            @Override
            public void onSuccess() {
                if (!isOnline(getApplicationContext())) {
                    showAlertDialog();
                } else {
                    appOpenAd = null;
                    AppOpenManager.isShowingAd = false;
                    handlerforlongtime.removeCallbacksAndMessages(null);
                    splashadsmInterstitialAd = null;
            /*startActivity(new Intent(getApplicationContext(), MainActivity_VPN.class));
            finish();*/
                    if (MyApplication.is_one_bool.equals("true")) {
                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity_VPN.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        if (MyApplication.is_fourth_bool.equals("true")) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                checkPermissions(storagecheck);
                            } else {
                                startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
                                finish();
                            }
                        } else {
                            Intent mainIntent = new Intent(getApplicationContext(), StartActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                    }
                }

            }

            @Override
            public void onUpdate(String url) {
                Log.e("my_log", "onUpdate: " + url);
                showUpdateDialog(url);
            }

            @Override
            public void onRedirect(String url) {
                Log.e("my_log", "onRedirect: " + url);
                showRedirectDialog(url);
            }

            @Override
            public void onReload() {
                startActivity(new Intent(SplashScreen.this, SplashScreen.class));
                finish();
            }

            @Override
            public void onGetExtradata(JSONObject extraData) {
                Log.e("my_log", "ongetExtradata: " + extraData.toString());
            }
        });

    }

    public void showRedirectDialog(final String url) {

        final Dialog dialog = new Dialog(SplashScreen.this);
        dialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.installnewappdialog, null);
        dialog.setContentView(view);
        TextView update = view.findViewById(R.id.update);
        TextView txt_title = view.findViewById(R.id.txt_title);
        TextView txt_decription = view.findViewById(R.id.txt_decription);

        update.setText("Install Now");
        txt_title.setText("Install our new app now and enjoy");
        txt_decription.setText("We have transferred our server, so install our new app by clicking the button below to enjoy the new features of app.");


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri marketUri = Uri.parse(url);
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                } catch (ActivityNotFoundException ignored1) {
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.create();
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }


    public void showUpdateDialog(final String url) {

        final Dialog dialog = new Dialog(SplashScreen.this);
        dialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.installnewappdialog, null);
        dialog.setContentView(view);
        TextView update = view.findViewById(R.id.update);
        TextView txt_title = view.findViewById(R.id.txt_title);
        TextView txt_decription = view.findViewById(R.id.txt_decription);

        update.setText("Update Now");
        txt_title.setText("Update our new app now and enjoy");
        txt_decription.setText("");
        txt_decription.setVisibility(View.GONE);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri marketUri = Uri.parse(url);
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                } catch (ActivityNotFoundException ignored1) {
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.create();
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public int getCurrentVersionCode() {
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    getPackageName(), 0);
            return info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;

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
            startActivity(new Intent(getApplicationContext(), GrandStorageActivity.class));
            finish();
            return false;
        } else {
            if (type == storagecheck) {
                startActivity(new Intent(getApplicationContext(), SelectGenderActivity.class));
                finish();
            }
        }
        return true;
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
        builder.setTitle("Alert!!!");
        builder.setMessage("Please check your internet connection !!!").setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //   error = "";
                        if (!isOnline(getApplicationContext())) {
                            showAlertDialog();
                        } else {
                            getAds();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static boolean isOnline(Context context) {
        try {
            @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setLocale(String lang) {
        if (lang.equals("")) {
            lang = "en";
        }
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

    private void next() {
        handlerforlongtime = new Handler();
        handlerforlongtime.postDelayed(new Runnable() {
            @Override
            public void run() {
                HomeScreen();
            }
        }, 15000);
        getAds();
    }
}
