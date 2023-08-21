package com.prox1.video1.download1.Vpn_auto_connect.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.prox1.video1.download1.Ads.BannerAdsSetup;
import com.prox1.video1.download1.Constant;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;
import com.prox1.video1.download1.activity.GrandStorageActivity;
import com.prox1.video1.download1.activity.SelectGenderActivity;
import com.prox1.video1.download1.activity.StartActivity;

import java.util.ArrayList;
import java.util.List;

public class Privacy_Policy extends AppCompatActivity {

    private InterstitialAd InterstitialAd;
    private int storagecheck = 114;

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_privacy_policy);

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

        /*--------------------------InterstitialAds-------------------*/
        loadAdmobInterstitialAds();


        TextView htmltextview = findViewById(R.id.html_privacy_view);

        /*try
        {
            //Note here I gave file name with "file:///android_asset/" to get it from assets
            InputStream inputStream = getResources().getAssets().open("file:///android_asset/privacy_policy.html");

            String html = IOUtils.toString(inputStream);

            htmltextview.setText(Html.fromHtml(html));
        }
        catch (IOException exception)
        {
            htmltextview.setText("Failed loading html.");
        }*/

        String string = "<html>\n" +
                "<body>\n" +
                "<div>\n" +
                "    <h3 style=\"font-family: monospace\">Privacy Policy</h3>\n" +
                "</div>\n" +
                "<div class=\"col-md-1 col-sm-1\"></div>\n" +
                "<div class=\"col-md-10 col-sm-10\">\n" +
                "    <div class=\"text-inter\">\n" +
                "        <p style=\"font-family: monospace\">When you use Our applications on Android,\n" +
                "            This Privacy Policy describes the information collected by us and how we use that information.\n" +
                "            Your privacy is important to us. Sometimes we need information to provide services that you request.\n" +
                "        </p>\n" +
                "\n" +
                "        <h4 style=\"font-family: monospace\">Personal Information:</h4>\n" +
                "        <p style=\"font-family: monospace\"><b>Take Photos and Videos:</b> This permission allows us to use your device&#8217;s camera to take photos / videos and turn ON/OFF Camera Flash.<br><br>\n" +
                "            <b>Full network access:</b> This permission is used to access the device&#8217;s network for certain functions including receiving update notifications or accessing app classification labels.<br><br>\n" +
                "            <b>Connect and disconnect from Wi-Fi:</b> This permission is used in settings and notification toolbar in order to connect and disconnect from Wi-Fi.<br><br>\n" +
                "            <b>Read Google service configuration:</b> This information is used to acquire advertising ID. Provide users with better advertising service by using such anonymous ID.<br><br>\n" +
                "            <b>Expand/collapse status bar:</b> This permission is used for the gesture feature of User System to expand and collapse the status bar.<br><br>\n" +
                "            <b>Measure app storage space:</b> This permission is used to acquire the amount of storage space used by an application.<br><br>\n" +
                "            <b>Modify system settings:</b> This permission is used in settings, in order to switch or adjust ringtone, vibration and brightness level of the screen.<br><br>\n" +
                "            <b>Photos / Media Files:</b> Modify or delete the contents of your Storage.</p><br>\n" +
                "        <p style=\"font-family: monospace\">  <b>You give us To avail services on this app, you are required to provide the following information for better experience.</b><br></br>\n" +
                "\n" +
                "            <b>(A). Age and Gender:</b> </br>For the purpose of delivery personalized and targeted ads through third party ad network providers if consented by the user.</br></br>\n" +
                "\n" +
                "            <b>All required information you provide will be protect by us, We do not share your personal information(Age & Gender) by anyone. </b></p>\n" +
                "\n" +
                "\n" +
                "\n" +
                "        <h4 style=\"font-family: monospace\">Non- Personal Information :</h4>\n" +
                "        <p style=\"font-family: monospace\">We may collect and use non-personal information in the following circumstances.\n" +
                "            To have a better understanding in user&#8217;s behaviour, solve problems in products and services, improve our products, services and advertising, we may collect non-personal information such as installed Other Applications name and package name, the data of instal.<br>\n" +
                "            We also collect unique device GCM token for Notification purpose.\n" +
                "            If non-personal information is combined with personal information, We treat the combined information as personal information for the purposes of this Privacy Policy.<br>\n" +
                "\n" +
                "            <b>Information we get from your use of our services  :</b>\n" +
                "            We may collect information about the services that you use and how you use them, such as when you view and interact with our content. We may collect device-specific information.We will not share that information with third parties. <br>\n" +
                "\n" +
                "            <b>Location information :</b>\n" +
                "            When you use a location-enabled service, we may collect and process information about your actual location, like GPS signals sent by a mobile device. We may also use various technologies to determine location, such as sensor data from your device that may, for example, provide information on nearby Wi-Fi access points and cell towers. <br>\n" +
                "\n" +
                "            <b>Unique Application numbers :</b>\n" +
                "            Certain services include a unique application number. This number and information about your installation (for example, the operating system type and application version number) may be sent to us when you install or uninstall that service or when that service periodically contacts our servers, such as for automatic updates.</p>\n" +
                "\n" +
                "        <h4 style=\"font-family: monospace\">Advertisement in App:</h4>\n" +
                "        <p style=\"font-family: monospace\">We use Google Admob, Facebook Audience Network, AppNext, StartApp and Unity SDK for advertisements in our Applications. There could be errors in the programming and sometime programming errors may cause unwanted side effects. </br>\n" +
                "            </br>\n" +
                "            <b>Ad Network Privacy Policy :</b>\n" +
                "\n" +
                "            </br>\n" +
                "        <ul>\n" +
                "            <li><a style=\"font-family: monospace\" href=\"https://support.google.com/admob/answer/6128543?hl=en\" target=\"_blank\">ADMOB Privacy</a></br></li>\n" +
                "            <li><a style=\"font-family: monospace\" href=\"https://www.facebook.com/about/privacy/\" target=\"_blank\">FACEBOOK Audience Network</a></br></li>\n" +
                "            <li><a style=\"font-family: monospace\" href=\"https://www.appnext.com/privacy-policy-oem-operators/\" target=\"_blank\">AppNext</a></br></li>\n" +
                "            <li><a style=\"font-family: monospace\" href=\"https://unity3d.com/legal/privacy-policy\" target=\"_blank\">Unity</a></br></li>\n" +
                "            <li><a style=\"font-family: monospace\" href=\"https://startapp.com/policy/privacy-policy/\" target=\"_blank\">StartApp</a></br></li>\n" +
                "        </ul>\n" +
                "\n" +
                "        <p style=\"font-family: monospace\"> We are very concerned about safeguarding the confidentiality of your information Please be aware that no security measures that we take to protect your information is absolutely guaranteed to avoid unauthorized access or use of your Non-Personal Information which is impenetrable. We haven't any Intention to Copy or use Others Product use and Access in company&#8217;s Application.<br>\n" +
                "            We are occasionally update this privacy statement. When we do so, we will also revise the \"last modified\" date of the privacy statement.<br>\n" +
                "\n" +
                "            If any query about this privacy policy do not use our service.<br></br>\n" +
                "\n" +
                "            Thank You...</p>\n" +
                "\n" +
                "        <p style=\"font-family: monospace\"><b>Last Modified : 25-January-2022</b></p>\n" +
                "\n" +
                "\n" +
                "\n" +
                "    </div>\n" +
                "\n" +
                "</div>\n" +
                "<div class=\"col-md-1 col-sm-1\"/>\n" +
                "\n" +
                "</div>\n" +
                "</html>";
        htmltextview.setText(Html.fromHtml(string));
        htmltextview.setMovementMethod(new ScrollingMovementMethod());

    }

    public void next(View view) {
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            if (InterstitialAd != null) {
                final ProgressDialog pd = new ProgressDialog(Privacy_Policy.this);
                pd.setMessage("Showing Ads..");
                pd.setCancelable(false);
                pd.show();
                // Show the ad when it's done loading.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        InterstitialAd.show(Privacy_Policy.this);
                    }
                }, 2000);
                InterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        nextact();
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
                    final ProgressDialog pd = new ProgressDialog(Privacy_Policy.this);
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
                            nextact();
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
                    nextact();
                }
            }
        } else {
            nextact();
        }
    }

    private void nextact() {
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

    public void loadAdmobInterstitialAds() {
        InterstitialAd.load(this, MyApplication.interstitial_2, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        InterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        InterstitialAd = null;
                        loadAdmobInterstitialAds();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity_VPN.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!IronSource.isInterstitialReady()) {
            IronSource.loadInterstitial();
        }
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
}
