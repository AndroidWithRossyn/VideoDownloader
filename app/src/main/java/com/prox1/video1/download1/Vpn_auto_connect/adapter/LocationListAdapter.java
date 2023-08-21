package com.prox1.video1.download1.Vpn_auto_connect.adapter;


import static com.prox1.video1.download1.MyApplication.PRIMIUM_STATE;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.prox1.video1.download1.BuildConfig;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.CountryData;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.Preference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {

    public Context context;
    private Preference preference;
    private List<CountryData> regions;
    private RegionListAdapterInterface listAdapterInterface;

    public LocationListAdapter(RegionListAdapterInterface listAdapterInterface, Activity cntec) {
        this.listAdapterInterface = listAdapterInterface;
        this.context = cntec;
        preference = new Preference(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_list_free, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final CountryData datanew = this.regions.get(holder.getAdapterPosition());
        final Country data = datanew.getCountryvalue();
        Locale locale = new Locale("", data.getCountry());

        if (position == 0) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/earthspeed", null, context.getPackageName()));
            holder.app_name.setText(R.string.best_performance_server);
            holder.limit.setVisibility(View.GONE);
        } else {
            ImageView imageView = holder.flag;

            //#From Drawable Folder
            Resources resources = context.getResources();
//            String sb = "drawable/" + data.getCountry().toLowerCase().trim();
//            imageView.setImageResource(resources.getIdentifier(sb, null, context.getPackageName()));

            //#From ASSET Folder
            String abc = data.getCountry().toLowerCase().trim() + ".png";
            try {
                InputStream ims_a = context.getAssets().open(abc);
                Bitmap bitmap = BitmapFactory.decodeStream(ims_a);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            Glide.with(context).load(context.getResources().getIdentifier(sb, null, context.getPackageName())).into(imageView);
            holder.app_name.setText(locale.getDisplayCountry());
            holder.limit.setVisibility(View.VISIBLE);

            Log.e("country_flag", String.valueOf(locale));

        }
        /*if (datanew.isPro()) {
            holder.pro.setVisibility(View.VISIBLE);
        } else {
            holder.pro.setVisibility(View.GONE);
        }*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapterInterface.onCountrySelected(regions.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return regions != null ? regions.size() : 0;
    }

    public void setRegions(List<Country> list) {
        regions = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CountryData newData = new CountryData();
            newData.setCountryvalue(list.get(i));
            if (i % 2 == 0) {
                if (list.get(i).getServers() > 1) {
                    if (BuildConfig.USE_IN_APP_PURCHASE) {
                        if (preference.isBooleenPreference(PRIMIUM_STATE)) {
                            newData.setPro(false);
                        } else {
                            newData.setPro(true);
                        }
                    } else {
                        newData.setPro(false);
                    }
                    regions.add(newData);
                } else {
                    newData.setPro(false);
                    regions.add(newData);
                }

            } else {
                newData.setPro(false);
                regions.add(newData);
            }
        }
        notifyDataSetChanged();
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(CountryData item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView app_name;
        ImageView flag, pro;
        ImageView limit;

        ViewHolder(View v) {
            super(v);
            this.app_name = itemView.findViewById(R.id.region_title);
            this.limit = itemView.findViewById(R.id.region_limit);
            this.flag = itemView.findViewById(R.id.country_flag);
            this.pro = itemView.findViewById(R.id.pro);
        }
    }
}
