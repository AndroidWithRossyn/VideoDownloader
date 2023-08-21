package com.prox1.video1.download1.Vpn_auto_connect.activity;

import static com.prox1.video1.download1.MyApplication.BUNDLE;
import static com.prox1.video1.download1.MyApplication.COUNTRY_DATA;
import static com.prox1.video1.download1.MyApplication.SELECTED_COUNTRY;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.SessionInfo;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.callbacks.TrafficListener;
import com.anchorfree.vpnsdk.callbacks.VpnStateListener;
import com.anchorfree.vpnsdk.compat.CredentialsCompat;
import com.anchorfree.vpnsdk.exceptions.NetworkRelatedException;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionDeniedException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionRevokedException;
import com.anchorfree.vpnsdk.exceptions.VpnTransportException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.anchorfree.vpnsdk.transporthydra.HydraVpnTransportException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.google.gson.Gson;
import com.northghost.caketube.CaketubeTransport;
import com.prox1.video1.download1.MyApplication;
import com.prox1.video1.download1.SharedPref;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.CountryData;
import com.prox1.video1.download1.Vpn_auto_connect.Utils.LoginDialog;
import com.prox1.video1.download1.activity.SelectModuleCategoryActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class VPNActivity extends HomeActivity implements TrafficListener, VpnStateListener, LoginDialog.LoginConfirmationInterface {

    public static String selectedCountry = "";
    private String ServerIPaddress = "00.000.000.00";
    private Locale locale;

    public static SharedPref sharedPref;

    protected void onStart() {
        super.onStart();
        UnifiedSDK.addTrafficListener(this);
        UnifiedSDK.addVpnStateListener(this);
        loginToVpn();
        sharedPref = new SharedPref(getApplicationContext());

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
    }

    @Override
    public void vpnStateChanged(VPNState vpnState) {
        updateUI();
    }

    public void vpnError(VpnException vpnException) {
        updateUI();
        handleError(vpnException);
    }

    @Override
    protected void loginToVpn() {
        Log.e(TAG, "loginToVpn: 1111");
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSDK.getInstance().getBackend().login(authMethod, new Callback<User>() {
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
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            public void success(VPNState vPNState) {
                callback.success(Boolean.valueOf(vPNState == VPNState.CONNECTED));
            }

            public void failure(VpnException vpnException) {
                callback.success(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), SelectModuleCategoryActivity.class));
        finish();
    }

    public void connectToVpn() {
        UnifiedSDK.getInstance().getBackend().isLoggedIn(new Callback<Boolean>() {
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
                            .withVirtualLocation(selectedCountry)
                            .withTransport(HydraTransport.TRANSPORT_ID)
                            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                            .build(), new CompletableCallback() {
                        @Override
                        public void complete() {
                            hideConnectProgress();
                            startUIUpdateTask();
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

    public void disconnectFromVnp() {
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
        UnifiedSDK.getInstance().getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startActivityForResult(new Intent(VPNActivity.this, ServerActivity.class), 3000);
                } else {
                    showMessage("Login please");
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3000) {
            if (resultCode == RESULT_OK) {
                Gson gson = new Gson();
                Bundle args = data.getBundleExtra(BUNDLE);
                CountryData item = gson.fromJson(args.getString(COUNTRY_DATA), CountryData.class);
//                Country item = gson.fromJson(args.getString(COUNTRY_DATA), Country.class);
                onRegionSelected(item);
            }
        }
    }

    public void getCurrentServer(final Callback<String> callback) {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            public void success(VPNState vPNState) {
                /*if (MainActivity.this.state == VPNState.CONNECTED) {
                    callback.success(MainActivity.this.selectedCountry);
                }*/
                if (state == VPNState.CONNECTED) {
                    UnifiedSDK.getStatus(new Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {
                            callback.success(selectedCountry);
                            ServerIPaddress = sessionInfo.getCredentials().getServers().get(0).getAddress();
                            ShowIPaddera(ServerIPaddress);
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

            public void failure(VpnException vpnException) {
                callback.failure(vpnException);
            }
        });
    }

    @Override
    protected void checkRemainingTraffic() {
        UnifiedSDK.getInstance().getBackend().remainingTraffic(new Callback<RemainingTraffic>() {
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

    public void onRegionSelected(CountryData item) {

        final Country new_countryValue = item.getCountryvalue();
        if (!item.isPro()) {
            selectedCountry = new_countryValue.getCountry();
            preference.setStringpreference(SELECTED_COUNTRY, selectedCountry);
            Toast.makeText(this, "Click to Connect VPN", Toast.LENGTH_SHORT).show();
            updateUI();
            UnifiedSDK.getVpnState(new Callback<VPNState>() {
                @Override
                public void success(@NonNull VPNState state) {
                    if (state == VPNState.CONNECTED) {
                        showMessage("Reconnecting to VPN with " + selectedCountry);
                        UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                            @Override
                            public void complete() {
                                connectToVpn();
                            }

                            @Override
                            public void error(@NonNull VpnException e) {
                                // In this case we try to reconnect
                                selectedCountry = "";
                                preference.setStringpreference(SELECTED_COUNTRY, selectedCountry);
                                connectToVpn();
                            }
                        });
                    }
                }

                @Override
                public void failure(@NonNull VpnException e) {
                }
            });
        } else {
            /*if (!sharedPref.GET_PURCHASED().booleanValue()) {
                Intent intent = new Intent(VPNActivity.this, VPNPremiumActivity.class);
                startActivity(intent);
            } else {
                selectedCountry = new_countryValue.getCountry();
                preference.setStringpreference(SELECTED_COUNTRY, selectedCountry);
                Toast.makeText(this, "Click to Connect VPN", Toast.LENGTH_SHORT).show();
                updateUI();
                UnifiedSDK.getVpnState(new Callback<VPNState>() {
                    @Override
                    public void success(@NonNull VPNState state) {
                        if (state == VPNState.CONNECTED) {
                            showMessage("Reconnecting to VPN with " + selectedCountry);
                            UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                                @Override
                                public void complete() {
                                    connectToVpn();
                                }

                                @Override
                                public void error(@NonNull VpnException e) {
                                    // In this case we try to reconnect
                                    selectedCountry = "";
                                    preference.setStringpreference(SELECTED_COUNTRY, selectedCountry);
                                    connectToVpn();
                                }
                            });
                        }
                    }

                    @Override
                    public void failure(@NonNull VpnException e) {
                    }
                });
            }*/
            selectedCountry = new_countryValue.getCountry();
            preference.setStringpreference(SELECTED_COUNTRY, selectedCountry);
            Toast.makeText(this, "Click to Connect VPN", Toast.LENGTH_SHORT).show();
            updateUI();
            UnifiedSDK.getVpnState(new Callback<VPNState>() {
                @Override
                public void success(@NonNull VPNState state) {
                    if (state == VPNState.CONNECTED) {
                        showMessage("Reconnecting to VPN with " + selectedCountry);
                        UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                            @Override
                            public void complete() {
                                connectToVpn();
                            }

                            @Override
                            public void error(@NonNull VpnException e) {
                                // In this case we try to reconnect
                                selectedCountry = "";
                                preference.setStringpreference(SELECTED_COUNTRY, selectedCountry);
                                connectToVpn();
                            }
                        });
                    }
                }

                @Override
                public void failure(@NonNull VpnException e) {
                }
            });
        }
    }

    public void handleError(VpnException vpnException) {
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
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void setLoginParams(String hostUrl, String carrierId) {
        ((MyApplication) getApplication()).setNewHostAndCarrier(hostUrl, carrierId);
    }

    @Override
    public void loginUser() {
        loginToVpn();
    }

}
