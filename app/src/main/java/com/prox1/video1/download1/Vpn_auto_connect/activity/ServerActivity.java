package com.prox1.video1.download1.Vpn_auto_connect.activity;

import static com.prox1.video1.download1.MyApplication.BUNDLE;
import static com.prox1.video1.download1.MyApplication.COUNTRY_DATA;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.google.gson.Gson;
import com.prox1.video1.download1.Ads.BannerAdsSetup;
import com.prox1.video1.download1.Constant;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.SharedPref;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.CountryData;
import com.prox1.video1.download1.Vpn_auto_connect.adapter.LocationListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServerActivity extends AppCompatActivity {

    @BindView(R.id.regions_recycler_view)
    RecyclerView regionsRecyclerView;

    @BindView(R.id.regions_progress)
    ProgressBar regionsProgressBar;

    private LocationListAdapter regionAdapter;
    private RegionChooserInterface regionChooserInterface;
    ImageView backToActivity;
    TextView activity_name;

    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_server);

        Constant.FullScreencall(this);

        ButterKnife.bind(this);

        sharedPref = new SharedPref(getApplicationContext());
        if (!sharedPref.GET_PURCHASED().booleanValue()) {
            new BannerAdsSetup(this).admobbannerads();
        }


        activity_name = (TextView) findViewById(R.id.activity_name);
        backToActivity = (ImageView) findViewById(R.id.finish_activity);
        activity_name.setText("Servers");
        backToActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        regionChooserInterface = new RegionChooserInterface() {
            @Override
            public void onRegionSelected(CountryData item) {
                if (!item.isPro()) {
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    Gson gson = new Gson();
                    String json = gson.toJson(item);

                    args.putString(COUNTRY_DATA, json);
                    intent.putExtra(BUNDLE, args);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                   /* if (!sharedPref.GET_PURCHASED().booleanValue()) {
                        Intent intent = new Intent(ServerActivity.this, VPNPremiumActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        Bundle args = new Bundle();
                        Gson gson = new Gson();
                        String json = gson.toJson(item);

                        args.putString(COUNTRY_DATA, json);
                        intent.putExtra(BUNDLE, args);
                        setResult(RESULT_OK, intent);
                        finish();
                    }*/
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    Gson gson = new Gson();
                    String json = gson.toJson(item);

                    args.putString(COUNTRY_DATA, json);
                    intent.putExtra(BUNDLE, args);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        };

        regionsRecyclerView.setHasFixedSize(true);
        regionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        regionAdapter = new LocationListAdapter(new LocationListAdapter.RegionListAdapterInterface() {
            @Override
            public void onCountrySelected(CountryData item) {
                regionChooserInterface.onRegionSelected(item);
            }
        }, ServerActivity.this);
        regionsRecyclerView.setAdapter(regionAdapter);
        loadServers();
    }

    private void loadServers() {
        showProgress();
        UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull final AvailableCountries countries) {
                hideProress();
                regionAdapter.setRegions(countries.getCountries());
                Log.e("Countries", countries.getCountries().toString());
            }

            @Override
            public void failure(@NonNull VpnException e) {
                hideProress();
            }
        });
    }

    private void showProgress() {
        regionsProgressBar.setVisibility(View.VISIBLE);
        regionsRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideProress() {
        regionsProgressBar.setVisibility(View.GONE);
        regionsRecyclerView.setVisibility(View.VISIBLE);
    }

   /* @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }*/

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), VPNActivity.class));
        finish();
    }

    public interface RegionChooserInterface {
        void onRegionSelected(CountryData item);
    }
}
