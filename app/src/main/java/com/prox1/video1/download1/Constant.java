package com.prox1.video1.download1;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.ads.nativead.NativeAd;

public class Constant {
    //Native Ads
    public static NativeAd adAdmobNative;

    public static boolean isConnected(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    public static void FullScreencall(Activity activity) {
        /*if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }*/
    }
    public static void privacyPolicy(Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        String titltStre = "Privacy Policy";
        alert.setTitle(titltStre);
        alert.setCancelable(false);
        final WebView wv = new WebView(context);


        String urlStr = context.getString(R.string.privacy_policy);

        wv.loadUrl(urlStr);
        wv.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                webView.loadUrl(url);
                return false;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                Uri uri = request.getUrl();
                webView.loadUrl(uri.toString());
                return false;
            }
        });
        alert.setView(wv);
        alert.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public static void TermsOfService(Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        String titltStre = "Terms Of Service";
        alert.setTitle(titltStre);
        alert.setCancelable(false);
        final WebView wv = new WebView(context);


        String urlStr = "file:///android_asset/ppandtu.html";

        wv.loadUrl(urlStr);
        wv.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                webView.loadUrl(url);
                return false;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                Uri uri = request.getUrl();
                webView.loadUrl(uri.toString());
                return false;
            }
        });
        alert.setView(wv);
        alert.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

}
