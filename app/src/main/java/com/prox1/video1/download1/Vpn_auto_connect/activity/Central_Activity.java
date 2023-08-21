package com.prox1.video1.download1.Vpn_auto_connect.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.airbnb.lottie.LottieAnimationView;
import com.anchorfree.partner.api.callback.Callback;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.partner.exceptions.PartnerRequestException;
import com.anchorfree.sdk.UnifiedSDK;
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
import com.pixplicity.easyprefs.library.Prefs;
import com.prox1.video1.download1.Ads.NativeAdsSetup;
import com.prox1.video1.download1.Constant;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;
import com.prox1.video1.download1.Vpn_auto_connect.ControlUtils;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.APIClient;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.ApiResponse;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.Convert;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.RestApis;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public abstract class Central_Activity extends AppCompatActivity {

    protected static final String TAG = Central_Activity.class.getSimpleName();

    private InterstitialAd vpnInterstitialAd;

    public VPNState state;
    int progressBarValue = 0;
    Handler handler = new Handler();

    @BindView(R.id.connect_btn)
    ImageView connectBtnTextView;

    @BindView(R.id.country_flag)
    ImageView country_flag;

    @BindView(R.id.selected_server)
    TextView selectedServerTextView;

    @BindView(R.id.connection_load)
    LottieAnimationView connection_load;

    @BindView(R.id.connected)
    TextView connected;

    @BindView(R.id.disconnected)
    TextView disconnected;

    @BindView(R.id.uploading_speed)
    TextView uploading_speed_textview;

    @BindView(R.id.downloading_speed)
    TextView downloading_speed_textview;

    private Handler customHandler = new Handler();
    private UnifiedNativeAd nativeAd;
    @BindView(R.id.server_ip)
    TextView server_ip;

    @BindView(R.id.next_btn)
    AppCompatButton next_btn;

    ObjectAnimator objectAnimator = null;

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

    LinearLayout linearAdsNative;
    private NativeAdsSetup nativeAdsBaseApp;

    WifiManager wm;
    String ip;

    public static SharedPref sharedPref;

    public static String inString, outString;

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(com.anchorfree.vpnsdk.callbacks.Callback<String> callback);

    protected abstract void loginToVpn();

    protected abstract void checkRemainingTraffic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_app_bar_main);

        Constant.FullScreencall(this);

        ButterKnife.bind(this);

        MainActivity_VPN.startVPNService();

        sharedPref = new SharedPref(getApplicationContext());

        /*--------------------------NativeAds-------------------*/
        linearAdsNative = findViewById(R.id.linearAdsNative);
        nativeAdsBaseApp = new NativeAdsSetup(this, linearAdsNative, null);
        nativeAdsBaseApp.loadexitandcallingbothnativeads();

        /*--------------------------InterstitialAds-------------------*/
        loadAdmobInterstitialAds();

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
                        next_btn.setVisibility(View.VISIBLE);
                    }
                }

                @SuppressLint("WrongConstant")
                public void failure(PartnerRequestException partnerRequestException) {
                    Toast.makeText(getApplicationContext(), "" + partnerRequestException.getMessage(), 1).show();
                }
            });
        }


        getip();

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Privacy_Policy.class));
                finish();
            }
        });

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


    @Override
    protected void onStart() {
        super.onStart();
        if (STATUS.equals("Connect")) {
            updateUI();
            connectToVpn();
        } else if (STATUS.equals("Disconnect")) {
            disconnectAlert();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
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
            public void failure(PartnerRequestException partnerRequestException) {

            }
        });

    }

    @OnClick(R.id.connect_btn)
    public void onConnectBtnClick(View v) {

        MainActivity_VPN.startVPNService();
        loginToVpn();

        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    STATUS = "Disconnect";
                    if (Constant.isConnected(getApplicationContext())) {
                        if (!sharedPref.GET_PURCHASED().booleanValue()) {
                            if (vpnInterstitialAd != null) {
                                final ProgressDialog pd = new ProgressDialog(Central_Activity.this);
                                pd.setMessage("Showing Ads..");
                                pd.setCancelable(false);
                                pd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        vpnInterstitialAd.show(Central_Activity.this);
                                    }
                                }, 1000);
                                vpnInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        vpnInterstitialAd = null;
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
                            if (vpnInterstitialAd != null) {
                                final ProgressDialog pd = new ProgressDialog(Central_Activity.this);
                                pd.setMessage("Showing Ads..");
                                pd.setCancelable(false);
                                pd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        vpnInterstitialAd.show(Central_Activity.this);
                                    }
                                }, 1000);
                                vpnInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        vpnInterstitialAd = null;
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
            public void failure(PartnerRequestException e) {
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
        UnifiedSDK.getVpnState(new com.anchorfree.vpnsdk.callbacks.Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                state = vpnState;
                switch (vpnState) {
                    case IDLE: {
                        if (ControlUtils.hasPermission(Central_Activity.this) || Build.VERSION.SDK_INT >= 29) {
                            loadIcon();
//                            server_ip.setText(R.string.default_server_ip_text);
                            country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                            connectBtnTextView.setEnabled(true);
                            uploading_speed_textview.setText("");
                            downloading_speed_textview.setText("");
//                            connectBtnTextView.setText("Security Protection is turned Off");
                            hideConnectProgress();
                        }
                        break;
                    }
                    case CONNECTED: {
                        loadIcon();
                        connectBtnTextView.setEnabled(true);
//                        connectBtnTextView.setText("Security Protection is turned On");
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        if (ControlUtils.hasPermission(Central_Activity.this) || Build.VERSION.SDK_INT >= 29) {
                            loadIcon();
//                        connectionStateTextView.setImageResource(R.drawable.connecting);
                            country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                            disconnected.setVisibility(View.INVISIBLE);
                            connected.setVisibility(View.VISIBLE);
                            connected.setText("Security Protection is Connecting");
                            connectBtnTextView.setEnabled(false);
                            objectAnimator.start();
                            showConnectProgress();
                        }
                        break;
                    }
                    case PAUSED: {
//                        connectBtnTextView.setText("Security Protection is turned Off");
                        country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        disconnected.setVisibility(View.INVISIBLE);
                        connected.setVisibility(View.VISIBLE);
                        connected.setText("Security Protection is Pause");
                        break;
                    }
                }
            }

            @Override
            public void failure(VpnException e) {
                country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                selectedServerTextView.setText(R.string.select_country);
            }
        });

        getCurrentServer(new com.anchorfree.vpnsdk.callbacks.Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerTextView.setText(R.string.select_country);
                        if (!currentServer.equals("")) {
                            Locale locale = new Locale("", currentServer);
                            Resources resources = getResources();
                            String sb = "drawable/" + currentServer.toLowerCase();
                            country_flag.setImageResource(resources.getIdentifier(sb, null, getPackageName()));
                            selectedServerTextView.setText(locale.getDisplayCountry());
                        } else {
                            country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                            selectedServerTextView.setText(R.string.select_country);
                        }
                    }
                });
            }

            @Override
            public void failure(@NonNull VpnException e) {
                country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                selectedServerTextView.setText(R.string.select_country);
            }
        });

    }

    public static StringBuilder m362J(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        return sb;
    }

    protected void updateTrafficStats(long outBytes, long inBytes) {
        outString = Convert.humanReadableByteCountOld(outBytes, false);
        inString = Convert.humanReadableByteCountOld(inBytes, false);

        uploading_speed_textview.setText(inString);
        downloading_speed_textview.setText(outString);

//        UnifiedSDK.update(NotificationConfig.newBuilder().title("Upload :" + inString + " Download :" + outString).channelId("vpn").build());
//        UnifiedSDK.update(NotificationConfig.newBuilder().title("Click here to disconnect...").channelId("vpn").build());
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
//        connectionProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {
                    progressBarValue++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            connectionProgressBar.setProgress(progressBarValue);
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

    protected void hideConnectProgress() {
//        connectionProgressBar.setVisibility(View.GONE);
//        connectionStateTextView.setVisibility(View.VISIBLE);
    }

    protected void showMessage(String msg) {
        Toast.makeText(Central_Activity.this, msg, Toast.LENGTH_SHORT).show();
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
            Glide.with(this).load(R.drawable.connect_btn).into(connectBtnTextView);
        } else if (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {
            connection_load.setVisibility(View.VISIBLE);
            disconnected.setVisibility(View.INVISIBLE);
            connected.setVisibility(View.VISIBLE);
            connected.setText("Connecting,...");
        } else if (state == VPNState.CONNECTED) {
            Glide.with(this).load(R.drawable.connect_btn).into(connectBtnTextView);
            connected.setVisibility(View.VISIBLE);
            next_btn.setVisibility(View.VISIBLE);
            disconnected.setVisibility(View.GONE);
            connection_load.setVisibility(View.GONE);
            server_ip.setText(ip);
        }
    }

    protected void disconnectAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Do you want to Disconnect?");

        builder.setPositiveButton("Disconnect",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        connected.setVisibility(View.INVISIBLE);
                        next_btn.setVisibility(View.INVISIBLE);
                        disconnected.setVisibility(View.VISIBLE);
                        disconnectFromVnp();
                        server_ip.setText(ip);
                        next_btn.setVisibility(View.INVISIBLE);
//                        switch_btn.setChecked(false);
//                        connectBtnTextView.setBackgroundColor(Color.RED);
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        server_ip.setText(ip);
//                        connectBtnTextView.setBackgroundColor(Color.GREEN);
                    }
                });
        builder.show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loadAdmobInterstitialAds() {
        InterstitialAd.load(this, MyApplication.interstitial_1, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        vpnInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        vpnInterstitialAd = null;
                        loadAdmobInterstitialAds();
                    }
                });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (Constant.isConnected(getApplicationContext())) {
            if (!sharedPref.GET_PURCHASED().booleanValue()) {
                if (vpnInterstitialAd != null) {
                    final ProgressDialog pd = new ProgressDialog(Central_Activity.this);
                    pd.setMessage("Showing Ads..");
                    pd.setCancelable(false);
                    pd.show();
                    // Show the ad when it's done loading.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            vpnInterstitialAd.show(Central_Activity.this);
                        }
                    }, 2000);

                    vpnInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            showExitDialog();
                            loadAdmobInterstitialAds();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            loadAdmobInterstitialAds();
                        }
                    });
                } else {
                    showExitDialog();
                }
            } else {
                showExitDialog();
            }
        } else {
            showExitDialog();
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

}
