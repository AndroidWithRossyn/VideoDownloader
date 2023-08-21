package com.prox1.video1.download1.Vpn_auto_connect.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.callback.Callback;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.SessionInfo;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.VpnPermissions;
import com.anchorfree.sdk.exceptions.PartnerApiException;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.callbacks.TrafficListener;
import com.anchorfree.vpnsdk.callbacks.VpnStateListener;
import com.anchorfree.vpnsdk.compat.CredentialsCompat;
import com.anchorfree.vpnsdk.exceptions.NetworkRelatedException;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionDeniedException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionRevokedException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.anchorfree.vpnsdk.transporthydra.HydraVpnTransportException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.northghost.caketube.CaketubeTransport;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.LoginDialog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MainActivity_VPN extends Central_Activity implements TrafficListener, VpnStateListener, LoginDialog.LoginConfirmationInterface {

    //    VPN Start
    public static String selectedCountry = "";
    private String ServerIPaddress = "00.000.000.00";
    private Locale locale;

    protected static final String TAG = MainActivity_VPN.class.getSimpleName();

    @Override
    protected void onStart() {
        super.onStart();

        // Force Update Start
        if (MyApplication.force_update.equals("true")) {
            try {
                /*versionChecker VersionChecker = new versionChecker();
                String versionUpdated = VersionChecker.execute().get().toString();
                Log.i("version code is", versionUpdated);*/

                PackageInfo packageInfo = null;
                try {
                    packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                
                int version_code = packageInfo.versionCode;
                String version_name = packageInfo.versionName;
                Log.i("updated version code", String.valueOf(version_code) + "  " + version_name);

                /*if (version_name != versionUpdated) {*/
                if (version_code < Integer.parseInt(MyApplication.App_version)) {
                    String packageName = getApplicationContext().getPackageName();//
                    UpdateMeeDialog updateMeeDialog = new UpdateMeeDialog();
                    updateMeeDialog.showDialogAddRoute(MainActivity_VPN.this, packageName);
                    Toast.makeText(getApplicationContext(), "please updated", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        if (MyApplication.force_redirect.equals("true")) {
            showDialog_Update();
        }
        // Force Update End

        //        VPN Start
        loginToVpn();
        UnifiedSDK.addTrafficListener(this);
        UnifiedSDK.addVpnStateListener(this);
        //        VPN End

    }

    @Override
    protected void onStop() {
        super.onStop();
        UnifiedSDK.removeVpnStateListener(this);
        UnifiedSDK.removeTrafficListener(this);
    }

    @Override
    public void onTrafficUpdate(long bytesTx, long bytesRx) {
        updateUI();
        updateTrafficStats(bytesTx, bytesRx);
        //        UnifiedSDK.update(NotificationConfig.newBuilder().title("Upload :" + l + " Download :" + l1).channelId("vpn").build());
    }

    @Override
    public void vpnStateChanged(@NonNull VPNState vpnState) {
        updateUI();
    }

    @Override
    public void vpnError(@NonNull VpnException e) {
        updateUI();
        handleError(e);
    }


    /*public void handleError(VpnException vpnException) {
        Log.w(TAG, vpnException);
        if (vpnException instanceof NetworkRelatedException) {
            showMessage("Check internet connection");
        } else if (vpnException == null) {
        } else {
            if (vpnException instanceof VpnPermissionRevokedException) {
                showMessage("User revoked vpn permissions");
            } else if (vpnException instanceof VpnPermissionDeniedException) {
                showMessage("User canceled to grant vpn permissions");
            } else if (vpnException instanceof HydraVpnTransportException) {
                VpnTransportException vpnTransportException = (VpnTransportException) vpnException;
                if (vpnTransportException.getCode() == 181) {
                    showMessage("Connection with vpn server was lost");
                } else if (vpnTransportException.getCode() == 191) {
                    showMessage("Client traffic exceeded");
                } else {
                    showMessage("Error in VPN transport");
                }
            }
        }
    }*/

    // VPN Start

    @Override
    protected void loginToVpn() {
        Log.e(TAG, "loginToVpn: 1111");
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSDK.getInstance().getBackend().login(authMethod, new com.anchorfree.vpnsdk.callbacks.Callback<User>() {
            @Override
            public void success(@NonNull User user) {
                updateUI();
            }

            @Override
            public void failure(@NonNull VpnException e) {
                updateUI();
                handleError(e);
            }
        });
    }

    @Override
    protected void isConnected(Callback<Boolean> callback) {
        UnifiedSDK.getVpnState(new com.anchorfree.vpnsdk.callbacks.Callback<VPNState>() {
            public void success(VPNState vPNState) {
                callback.success(vPNState == VPNState.CONNECTED);
            }

            public void failure(VpnException vpnException) {
                callback.success(false);
            }
        });
    }

    @Override
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
                    showConnectProgress();
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
                            hideConnectProgress();
                            startUIUpdateTask();
                            startActivity(new Intent(getApplicationContext(), Privacy_Policy.class));
                            finish();
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                            hideConnectProgress();
                            updateUI();
                            handleError(e);
                        }
                    });
                }
            }

            public void failure(VpnException vpnException) {
                handleError(vpnException);
            }
        });
    }

    @Override
    protected void disconnectFromVnp() {
        showConnectProgress();
        UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            public void complete() {
                hideConnectProgress();
                stopUIUpdateTask();
            }

            public void error(VpnException vpnException) {
                hideConnectProgress();
                updateUI();
                handleError(vpnException);
            }
        });
    }

    @Override
    protected void chooseServer() {
        UnifiedSDK.getInstance().getBackend().isLoggedIn(new com.anchorfree.vpnsdk.callbacks.Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
//                    startActivityForResult(new Intent(MainActivity_VPN.this, ServerActivity.class), 3000);
                } else {
                    showMessage("Login please");
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                handleError(e);
            }
        });
    }

    @Override
    protected void getCurrentServer(final com.anchorfree.vpnsdk.callbacks.Callback<String> callback) {
        UnifiedSDK.getVpnState(new com.anchorfree.vpnsdk.callbacks.Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    UnifiedSDK.getStatus(new com.anchorfree.vpnsdk.callbacks.Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {
                            ServerIPaddress = sessionInfo.getCredentials().getServers().get(0).getAddress();
                            server_ip.setText(ServerIPaddress);
                            callback.success(CredentialsCompat.getServerCountry(sessionInfo.getCredentials()));
                        }

                        @Override
                        public void failure(@NonNull VpnException e) {
                            callback.success(selectedCountry);
                        }
                    });
                } else {
                    callback.success(selectedCountry);
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                callback.failure(e);
            }
        });
    }

    @Override
    protected void checkRemainingTraffic() {
        UnifiedSDK.getInstance().getBackend().remainingTraffic(new com.anchorfree.vpnsdk.callbacks.Callback<RemainingTraffic>() {
            @Override
            public void success(@NonNull RemainingTraffic remainingTraffic) {
                updateRemainingTraffic(remainingTraffic);
            }

            @Override
            public void failure(@NonNull VpnException e) {
                updateUI();
                handleError(e);
            }
        });
    }

    public static boolean startVPNService() {

        VpnPermissions.request(new CompletableCallback() {
            @Override
            public void complete() {
                //permission was granted
//                Toast.makeText(MainActivity_VPN.this, "Permission Granted!!!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void error(@NonNull VpnException e) {
//                Toast.makeText(getApplicationContext(), "Permission not Granted!!!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
        return true;
    }

    @Override
    public void setLoginParams(String hostUrl, String carrierId) {
        ((MyApplication) getApplication()).setNewHostAndCarrier(hostUrl, carrierId);
    }

    @Override
    public void loginUser() {
        loginToVpn();
    }

    public void handleError(Throwable e) {
        Log.w(TAG, e);
        if (e instanceof NetworkRelatedException) {
            showMessage("Check internet connection");
        } else if (e instanceof VpnException) {
            if (e instanceof VpnPermissionRevokedException) {
                showMessage("User revoked vpn permissions");
            } else if (e instanceof VpnPermissionDeniedException) {
                showMessage("User canceled to grant vpn permissions");
            } else if (e instanceof HydraVpnTransportException) {
                HydraVpnTransportException hydraVpnTransportException = (HydraVpnTransportException) e;
                if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_ERROR_BROKEN) {
                    showMessage("Connection with vpn server was lost");
                } else if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_DCN_BLOCKED_BW) {
                    showMessage("Client traffic exceeded");
                } else {
                    showMessage("Error in VPN transport");
                }
            } else {
//                showMessage("Error in VPN Service");
            }
        } else if (e instanceof PartnerApiException) {
            switch (((PartnerApiException) e).getContent()) {
                case PartnerApiException.CODE_NOT_AUTHORIZED:
                    showMessage("User unauthorized");
                    break;
                case PartnerApiException.CODE_TRAFFIC_EXCEED:
                    showMessage("Server unavailable");
                    break;
                default:
                    showMessage("Other error. Check PartnerApiException constants");
                    break;
            }
        }
    }


    // VPN End

    // Forcefully Update
    public void showDialog_Update() {
        Dialog dialog = new Dialog(MainActivity_VPN.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_update);

        AppCompatButton cancelDialogue = (AppCompatButton) dialog.findViewById(R.id.buttonUpdate);
        cancelDialogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyApplication.App_link.equals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(MyApplication.App_link));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                    startActivity(intent);
                }
            }
        });
        dialog.show();
    }


    // version check and update
    /*class versionChecker extends AsyncTask<String, String, String> {
        String newVersion;

        @Override
        protected String doInBackground(String... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName())
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;
        }
    }*/

    public class UpdateMeeDialog {

        ActivityManager am;
        TextView rootName;
        Context context;
        Dialog dialog;
        String key1, schoolId;

        public void showDialogAddRoute(Activity activity, final String packageName) {
            context = activity;
            dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_update);
            am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);

            Button cancelDialogue = (Button) dialog.findViewById(R.id.buttonUpdate);
            Log.i("package name", packageName);
            cancelDialogue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details? id = " + packageName + " & hl = en"));
                    context.startActivity(intent);*/
                    if (!MyApplication.App_link.equals("")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(MyApplication.App_link));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                        startActivity(intent);
                    }
                }
            });
            dialog.show();
        }
    }

}
