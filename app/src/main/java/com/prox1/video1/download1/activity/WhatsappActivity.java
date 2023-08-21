package com.prox1.video1.download1.activity;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.documentfile.provider.DocumentFile;
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
import com.prox1.video1.download1.databinding.ActivityWhatsappBinding;
import com.prox1.video1.download1.fragment.WhatsappImageFragment;
import com.prox1.video1.download1.fragment.WhatsappQImageFragment;
import com.prox1.video1.download1.fragment.WhatsappQVideoFragment;
import com.prox1.video1.download1.fragment.WhatsappVideoFragment;
import com.prox1.video1.download1.util.AppLangSessionManager;
import com.prox1.video1.download1.util.SharePrefs;
import com.prox1.video1.download1.util.Utils;
import static com.prox1.video1.download1.util.Utils.RootDirectoryFacebook;
import static com.prox1.video1.download1.util.Utils.createFileFolder;
import static com.prox1.video1.download1.util.Utils.startDownload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WhatsappActivity extends AppCompatActivity {
    private ActivityWhatsappBinding binding;
    private WhatsappActivity activity;

    private File[] allfiles;
    AppLangSessionManager appLangSessionManager;
    public ArrayList<Uri> fileArrayList;
    ProgressDialog progressDialog;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_whatsapp);
        activity = this;

        Constant.FullScreencall(this);

        sharedPref = new SharedPref(getApplicationContext());

        createFileFolder();
        initViews();

        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager.getLanguage());


        /*--------------------------BannerAds-------------------*/
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

        fileArrayList = new ArrayList<>();
        initProgress();
        /* if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {*/
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            setupViewPager(binding.viewpager);
            binding.tabs.setupWithViewPager(binding.viewpager);
            for (int i = 0; i < binding.tabs.getTabCount(); i++) {
                binding.tabs.getTabAt(i).setCustomView((View) (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, (ViewGroup) null));
            }
        } else if (getContentResolver().getPersistedUriPermissions().size() > 0) {
            progressDialog.show();
            new LoadAllFiles().execute(new String[0]);
            binding.sAccessBtn.setVisibility(8);
        } else {
            binding.sAccessBtn.setVisibility(0);
        }
        /*} else {
            runtimePermission();
        }*/

        binding.sAccessBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dialog dialog = new Dialog(activity, R.style.SheetDialog);
                dialog.requestWindowFeature(1);

                new AlertDialog.Builder(WhatsappActivity.this)
                        .setTitle("Permission!")
                        .setMessage(getString(R.string.allow_app_to_access_whatsapp_status_folder))

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                try {
                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                                        Intent createOpenDocumentTreeIntent = ((StorageManager) activity.getSystemService("storage")).getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                                        String replace = ((Uri) createOpenDocumentTreeIntent.getParcelableExtra("android.provider.extra.INITIAL_URI")).toString().replace("/root/", "/document/");
                                        createOpenDocumentTreeIntent.putExtra("android.provider.extra.INITIAL_URI", Uri.parse(replace + "%3A" + "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"));
                                        startActivityForResult(createOpenDocumentTreeIntent, 2001);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Denied", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        binding.tabs.setupWithViewPager(binding.viewpager);
        binding.imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onBackPressed();
                finish();
            }
        });

       /* for (int i = 0; i < binding.tabs.getTabCount(); i++) {
            TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
            binding.tabs.getTabAt(i).setCustomView(tv);
        }*/

        binding.LLOpenWhatsapp.setOnClickListener(v -> {
            Utils.OpenApp(activity, "com.whatsapp");
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void initProgress() {
        ProgressDialog progressDialog2 = new ProgressDialog(activity, R.style.AppCompatAlertDialogStyle);
        progressDialog = progressDialog2;
        progressDialog2.setProgressStyle(0);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading Status. Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new WhatsappImageFragment(), getResources().getString(R.string.images));
        adapter.addFragment(new WhatsappVideoFragment(), getResources().getString(R.string.videos));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
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

    public void onActivityResult(int i, int i2, Intent intent) {
        try {
            super.onActivityResult(i, i2, intent);
            if (i == 2001 && i2 == -1) {
                Uri data = intent.getData();
                if (data.toString().contains(".Statuses")) {
                    getContentResolver().takePersistableUriPermission(data, 3);
                    progressDialog.show();
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        new LoadAllFiles().execute(new String[0]);
                        return;
                    }
                    return;
                }
                WhatsappActivity whatsappActivity = activity;
                Utils.infoDialog(whatsappActivity, whatsappActivity.getResources().getString(R.string.wrong_folder), activity.getResources().getString(R.string.selected_wrong_folder));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class LoadAllFiles extends AsyncTask<String, String, String> {
        public void onProgressUpdate(String... strArr) {
        }

        LoadAllFiles() {
        }

        public String doInBackground(String... strArr) {
            for (DocumentFile documentFile : DocumentFile.fromTreeUri(activity, activity.getContentResolver().getPersistedUriPermissions().get(0).getUri()).listFiles()) {
                if (!documentFile.isDirectory() && !documentFile.getName().equals(".nomedia")) {
                    fileArrayList.add(documentFile.getUri());
                }
            }
            return null;
        }

        public void onPostExecute(String str) {
            progressDialog.dismiss();
            WhatsappActivity whatsappActivity = WhatsappActivity.this;
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(whatsappActivity.activity.getSupportFragmentManager(), 1);
            viewPagerAdapter.addFragment(new WhatsappQImageFragment(fileArrayList), getResources().getString(R.string.images));
            viewPagerAdapter.addFragment(new WhatsappQVideoFragment(fileArrayList), getResources().getString(R.string.videos));
            binding.viewpager.setAdapter(viewPagerAdapter);
            binding.viewpager.setOffscreenPageLimit(1);
            binding.tabs.setupWithViewPager(binding.viewpager);
            binding.sAccessBtn.setVisibility(8);
            for (int i = 0; i < binding.tabs.getTabCount(); i++) {
                binding.tabs.getTabAt(i).setCustomView((View) (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, (ViewGroup) null));
            }
        }

        public void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
        }
    }

}
