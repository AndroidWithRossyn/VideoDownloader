package com.prox1.video1.download1.activity;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;


import static com.prox1.video1.download1.util.Utils.RootDirectoryInsta;
import static com.prox1.video1.download1.util.Utils.createFileFolder;
import static com.prox1.video1.download1.util.Utils.startDownload;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.prox1.video1.download1.adapter.StoriesListAdapter;
import com.prox1.video1.download1.adapter.UserListAdapter;
import com.prox1.video1.download1.api.CommonClassForAPI;
import com.prox1.video1.download1.databinding.ActivityFacebookBinding;
import com.prox1.video1.download1.databinding.ActivityInstagramBinding;
import com.prox1.video1.download1.interfaces.UserListInterface;
import com.prox1.video1.download1.model.Edge;
import com.prox1.video1.download1.model.EdgeSidecarToChildren;
import com.prox1.video1.download1.model.ResponseModel;
import com.prox1.video1.download1.model.story.FullDetailModel;
import com.prox1.video1.download1.model.story.StoryModel;
import com.prox1.video1.download1.model.story.TrayModel;
import com.prox1.video1.download1.util.AppLangSessionManager;
import com.prox1.video1.download1.util.SharePrefs;
import com.prox1.video1.download1.util.Utils;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.northghost.caketube.CaketubeTransport;

import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.reactivex.observers.DisposableObserver;

public class InstagramActivity extends AppCompatActivity implements UserListInterface {
    private ActivityInstagramBinding binding;
    private InstagramActivity activity;
    Context context;
    private ClipboardManager clipBoard;
    CommonClassForAPI commonClassForAPI;
    private String PhotoUrl;
    private String VideoUrl;
    private InterstitialAd mInterstitialAd;

    LinearLayout linearAdsNative;
    private NativeAdsSetup nativeAdsBaseApp;
    AppLangSessionManager appLangSessionManager;
    UserListAdapter userListAdapter;
    StoriesListAdapter storiesListAdapter;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_instagram);

        Constant.FullScreencall(this);

        sharedPref = new SharedPref(getApplicationContext());

        context = activity = this;

        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager.getLanguage());

        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();

        /*--------------------------BannerAds-------------------*/
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            new BannerAdsSetup(this).admobbannerads();
        }

        /*--------------------------NativeAds-------------------*/
        linearAdsNative = findViewById(R.id.linearAdsNative);
        nativeAdsBaseApp = new NativeAdsSetup(this, linearAdsNative, null);
        nativeAdsBaseApp.loadexitandcallingbothnativeads();

        InterstitialAdsINIT();

        /*--------------------------IronSourceAds-------------------*/
        IronSource.init(this, MyApplication.iron_id);

        if (!IronSource.isInterstitialReady()) {
            IronSource.loadInterstitial();
        }

        initViews();

        if (MyApplication.is_third_bool.equals("true")) {
            stopToVpn();
        } else {
//            connectToVpn();
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


    private void stopToVpn() {
        try {
            UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                public void complete() {
                }

                public void error(VpnException vpnException) {
                }
            });

        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        context = activity = this;
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
                .load(R.drawable.insta1)
                .into(binding.layoutHowTo.imHowto1);

        Glide.with(activity)
                .load(R.drawable.insta2)
                .into(binding.layoutHowTo.imHowto2);

        Glide.with(activity)
                .load(R.drawable.insta3)
                .into(binding.layoutHowTo.imHowto3);

        Glide.with(activity)
                .load(R.drawable.insta4)
                .into(binding.layoutHowTo.imHowto4);


        binding.layoutHowTo.tvHowTo1.setText(getResources().getString(R.string.opn_insta));
        binding.layoutHowTo.tvHowTo3.setText(getResources().getString(R.string.opn_insta));
        if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISSHOWHOWTOINSTA)) {
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.ISSHOWHOWTOINSTA, true);
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
                GetInstagramData();
                showInterstitial();
            }


        });

        binding.tvPaste.setOnClickListener(v -> {
            PasteText();
        });
        binding.LLOpenInstagram.setOnClickListener(v -> {
            Utils.OpenApp(activity, "com.instagram.android");
        });

        GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        binding.RVUserList.setLayoutManager(mLayoutManager);
        binding.RVUserList.setNestedScrollingEnabled(false);
        mLayoutManager.setOrientation(RecyclerView.HORIZONTAL);


        if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
            layoutCondition();
            callStoriesApi();
            binding.SwitchLogin.setChecked(true);
        } else {
            binding.SwitchLogin.setChecked(false);
        }

        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,
                        LoginActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        binding.RLLoginInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
                    Intent intent = new Intent(activity,
                            LoginActivity.class);
                    startActivityForResult(intent, 100);
                } else {
                    AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                    ab.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharePrefs.getInstance(activity).putBoolean(SharePrefs.ISINSTALOGIN, false);
                            SharePrefs.getInstance(activity).putString(SharePrefs.COOKIES, "");
                            SharePrefs.getInstance(activity).putString(SharePrefs.CSRF, "");
                            SharePrefs.getInstance(activity).putString(SharePrefs.SESSIONID, "");
                            SharePrefs.getInstance(activity).putString(SharePrefs.USERID, "");

                            if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
                                binding.SwitchLogin.setChecked(true);
                            } else {
                                binding.SwitchLogin.setChecked(false);
                                binding.RVUserList.setVisibility(View.GONE);
                                binding.RVStories.setVisibility(View.GONE);
                                binding.tvViewStories.setText(activity.getResources().getText(R.string.view_stories));
                                binding.tvLogin.setVisibility(View.VISIBLE);
                            }
                            dialog.cancel();

                        }
                    });
                    ab.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = ab.create();
                    alert.setTitle(getResources().getString(R.string.do_u_want_to_download_media_from_pvt));
                    alert.show();
                }

            }
        });

        GridLayoutManager mLayoutManager1 = new GridLayoutManager(getApplicationContext(), 3);
        binding.RVStories.setLayoutManager(mLayoutManager1);
        binding.RVStories.setNestedScrollingEnabled(false);
        mLayoutManager1.setOrientation(RecyclerView.VERTICAL);

    }

    public void layoutCondition() {
        binding.tvViewStories.setText(activity.getResources().getString(R.string.stories));
        binding.tvLogin.setVisibility(View.GONE);

    }

    private void GetInstagramData() {
        try {
            createFileFolder();
            URL url = new URL(binding.etText.getText().toString());
            String host = url.getHost();
            Log.e("initViews: ", host);
            if (host.equals("www.instagram.com")) {
                callDownload(binding.etText.getText().toString());
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
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("instagram.com")) {
                        binding.etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("instagram.com")) {
                        binding.etText.setText(item.getText().toString());
                    }

                }
            } else {
                if (CopyIntent.contains("instagram.com")) {
                    binding.etText.setText(CopyIntent);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getUrlWithoutParameters(String url) {
        try {
            URI uri = new URI(url);
            return new URI(uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null, // Ignore the query part of the input url
                    uri.getFragment()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            return "";
        }
    }


    private void callDownload(String Url) {
        String UrlWithoutQP = getUrlWithoutParameters(Url);
        UrlWithoutQP = UrlWithoutQP + "?__a=1";
        try {
            Utils utils = new Utils(activity);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    Utils.showProgressDialog(activity);
                    commonClassForAPI.callResult(instaObserver, UrlWithoutQP,
                            "ds_user_id=" + SharePrefs.getInstance(activity).getString(SharePrefs.USERID)
                                    + "; sessionid=" + SharePrefs.getInstance(activity).getString(SharePrefs.SESSIONID));
                }
            } else {
                Utils.setToast(activity, getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private DisposableObserver<JsonObject> instaObserver = new DisposableObserver<JsonObject>() {

        @Override
        public void onNext(JsonObject versionList) {
            Utils.hideProgressDialog(activity);
            try {
                Log.e("onNext: ", versionList.toString());
                Type listType = new TypeToken<ResponseModel>() {
                }.getType();
                ResponseModel responseModel = new Gson().fromJson(versionList.toString(), listType);
                EdgeSidecarToChildren edgeSidecarToChildren = responseModel.getGraphql().getShortcode_media().getEdge_sidecar_to_children();
                if (edgeSidecarToChildren != null) {
                    List<Edge> edgeArrayList = edgeSidecarToChildren.getEdges();
                    for (int i = 0; i < edgeArrayList.size(); i++) {
                        if (edgeArrayList.get(i).getNode().isIs_video()) {
                            VideoUrl = edgeArrayList.get(i).getNode().getVideo_url();
                            startDownload(VideoUrl, RootDirectoryInsta, activity, getVideoFilenameFromURL(VideoUrl));
                            binding.etText.setText("");
                            VideoUrl = "";

                        } else {
                            PhotoUrl = edgeArrayList.get(i).getNode().getDisplay_resources().get(edgeArrayList.get(i).getNode().getDisplay_resources().size() - 1).getSrc();
                            startDownload(PhotoUrl, RootDirectoryInsta, activity, getImageFilenameFromURL(PhotoUrl));
                            PhotoUrl = "";
                            binding.etText.setText("");
                        }
                    }
                } else {
                    boolean isVideo = responseModel.getGraphql().getShortcode_media().isIs_video();
                    if (isVideo) {
                        VideoUrl = responseModel.getGraphql().getShortcode_media().getVideo_url();
                        //new DownloadFileFromURL().execute(VideoUrl,getFilenameFromURL(VideoUrl));
                        startDownload(VideoUrl, RootDirectoryInsta, activity, getVideoFilenameFromURL(VideoUrl));
                        VideoUrl = "";
                        binding.etText.setText("");
                    } else {
                        PhotoUrl = responseModel.getGraphql().getShortcode_media().getDisplay_resources()
                                .get(responseModel.getGraphql().getShortcode_media().getDisplay_resources().size() - 1).getSrc();

                        startDownload(PhotoUrl, RootDirectoryInsta, activity, getImageFilenameFromURL(PhotoUrl));
                        PhotoUrl = "";
                        binding.etText.setText("");
                        // new DownloadFileFromURL().execute(PhotoUrl,getFilenameFromURL(PhotoUrl));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            Utils.hideProgressDialog(activity);

            // Login Failed
            Toast makeText;
            try {
                if (!binding.etText.getText().toString().trim().equals("")) {
                    makeText = Toast.makeText(getApplicationContext(), "No Media on Instagram or Invalid Link", 0);
                    makeText.show();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog(activity);
        }
    };

    public String getImageFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath().toString()).getName();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".png";
        }
    }

    public String getVideoFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath().toString()).getName();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".mp4";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instaObserver.dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 100 && resultCode == RESULT_OK) {
                String requiredValue = data.getStringExtra("key");
                if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
                    binding.SwitchLogin.setChecked(true);
                    layoutCondition();
                    callStoriesApi();
                } else {
                    binding.SwitchLogin.setChecked(false);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
                final ProgressDialog pd = new ProgressDialog(InstagramActivity.this);
                pd.setMessage("Showing Ads..");
                pd.setCancelable(false);
                pd.show();
                // Show the ad when it's done loading.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        mInterstitialAd.show(InstagramActivity.this);
                    }
                }, 2000);
            } else {
                if (IronSource.isInterstitialReady()) {
                    final ProgressDialog pd = new ProgressDialog(InstagramActivity.this);
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

    private void callStoriesApi() {
        try {
            Utils utils = new Utils(activity);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    binding.prLoadingBar.setVisibility(View.VISIBLE);
                    commonClassForAPI.getStories(storyObserver, "ds_user_id=" + SharePrefs.getInstance(activity).getString(SharePrefs.USERID)
                            + "; sessionid=" + SharePrefs.getInstance(activity).getString(SharePrefs.SESSIONID));
                }
            } else {
                Utils.setToast(activity, activity
                        .getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    private DisposableObserver<StoryModel> storyObserver = new DisposableObserver<StoryModel>() {
        @Override
        public void onNext(StoryModel response) {
            binding.RVUserList.setVisibility(View.VISIBLE);
            binding.prLoadingBar.setVisibility(View.GONE);
            try {
                userListAdapter = new UserListAdapter(activity, response.getTray(), activity);
                binding.RVUserList.setAdapter(userListAdapter);
                userListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            binding.prLoadingBar.setVisibility(View.GONE);
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            binding.prLoadingBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void userListClick(int position, TrayModel trayModel) {
        callStoriesDetailApi(String.valueOf(trayModel.getUser().getPk()));
    }

    private void callStoriesDetailApi(String UserId) {
        try {
            Utils utils = new Utils(activity);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    binding.prLoadingBar.setVisibility(View.VISIBLE);
                    commonClassForAPI.getFullDetailFeed(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(activity).getString(SharePrefs.USERID)
                            + "; sessionid=" + SharePrefs.getInstance(activity).getString(SharePrefs.SESSIONID));
                }
            } else {
                Utils.setToast(activity, activity
                        .getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private DisposableObserver<FullDetailModel> storyDetailObserver = new DisposableObserver<FullDetailModel>() {
        @Override
        public void onNext(FullDetailModel response) {
            binding.RVUserList.setVisibility(View.VISIBLE);
            binding.prLoadingBar.setVisibility(View.GONE);
            try {
                storiesListAdapter = new StoriesListAdapter(activity, response.getReel_feed().getItems());
                binding.RVStories.setAdapter(storiesListAdapter);
                storiesListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            binding.prLoadingBar.setVisibility(View.GONE);

            // Login Failed
            Toast makeText;
            try {
                if (!binding.etText.getText().toString().trim().equals("")) {
                    makeText = Toast.makeText(getApplicationContext(), "No Media on Instagram or Invalid Link", 0);
                    makeText.show();
                }
                UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                    public void complete() {
                    }

                    public void error(VpnException vpnException) {
                    }
                });

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            binding.prLoadingBar.setVisibility(View.GONE);
        }
    };

}
