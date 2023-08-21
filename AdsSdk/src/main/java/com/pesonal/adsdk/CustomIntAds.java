package com.pesonal.adsdk;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Collections;


public class CustomIntAds extends Dialog  {
    private LinearLayout LLTop;
    public Context mContext;
    public OnCloseListener listener_positive;
    private ImageView ad_media_view;
    private RelativeLayout int_bg;
    private TextView txt_title;
    private TextView txt_body;
    private TextView txt_rate;
    private TextView txt_download;
    Button btn_call_to_action;
    CustomAdModel customAdModel;

    public CustomIntAds(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomIntAds(@NonNull Context context,CustomAdModel customAdModel) {
        super(context);
        this.mContext = context;
        this.customAdModel = customAdModel;
    }

    public CustomIntAds(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomIntAds(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    public interface OnCloseListener {
        void onAdsCloseClick();

        void setOnKeyListener();
    }


    public CustomIntAds setOnCloseListener(OnCloseListener onCloseListener) {
        this.listener_positive = onCloseListener;
        return this;
    }
    @SuppressLint("WrongConstant")
    public Point screen_size_get(Context context) {
        Point point = new Point();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getSize(point);
        return point;
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cust_int);

        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = screen_size_get(getContext()).x;
        attributes.height = screen_size_get(getContext()).y;
        getWindow().setAttributes(attributes);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        if(customAdModel!=null)
        {
            try
            {
                ad_media_view = (ImageView) findViewById(R.id.native_ad_media);
                txt_title = (TextView) findViewById(R.id.native_ad_title);
                txt_body = (TextView) findViewById(R.id.native_ad_social_context);
                txt_rate = (TextView) findViewById(R.id.txt_rate);
                txt_download = (TextView) findViewById(R.id.txt_download);
                btn_call_to_action = findViewById(R.id.native_ad_call_to_action);
                int_bg = findViewById(R.id.int_bg);
                Glide
                        .with(mContext)
                        .load(customAdModel.getApp_logo())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into((ImageView) findViewById(R.id.native_ad_icon));


                Glide
                        .with(mContext)
                        .load(customAdModel.getApp_banner())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ad_media_view
                        );

                txt_title.setText(customAdModel.getApp_name());
                txt_body.setText(customAdModel.getApp_shortDecription());
                txt_rate.setText(customAdModel.getApp_rating());
                txt_download.setText(customAdModel.getApp_download());
                btn_call_to_action.setText(customAdModel.getApp_buttonName());
                btn_call_to_action.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        String action_str = customAdModel.getApp_packageName();
                        if (action_str.contains("http")) {
                            Uri marketUri = Uri.parse(action_str);
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                            mContext.startActivity(marketIntent);
                        } else {
                            mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + action_str)));
                        }

                    }
                });

                findViewById(R.id.ImgClose).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        OnCloseListener onCloseListener = listener_positive;
                        if (onCloseListener != null) {
                            onCloseListener.onAdsCloseClick();
                        }
                    }
                });





                AppManage.count_custIntAd++;
            }
            catch (Exception e)
            {
                OnCloseListener onCloseListener = listener_positive;
                if (onCloseListener != null) {
                    onCloseListener.onAdsCloseClick();
                }
            }
        }
        else
        {
            OnCloseListener onCloseListener = listener_positive;
            if (onCloseListener != null) {
                onCloseListener.onAdsCloseClick();
            }
        }

        setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                OnCloseListener onCloseListener = listener_positive;
                if (onCloseListener != null) {
                    onCloseListener.onAdsCloseClick();
                }
                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
