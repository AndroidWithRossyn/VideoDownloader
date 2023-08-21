package com.prox1.video1.download1.Vpn_auto_connect.activity;

import androidx.appcompat.app.AppCompatActivity;

/*import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;*/

public class VPNPremiumActivity extends AppCompatActivity { /* implements
        PurchasesUpdatedListener, SkuDetailsResponseListener,
        BillingClientStateListener {

    public static final String PRODUCT_ID_weekly = "sub_weekly";
    public static final String PRODUCT_ID_montly = "sub_monthly";
    public static final String PRODUCT_ID_yearly = "sub_yearly";
    //    public static final String PRODUCT_ID_onetime = "inapp_onetime";
    public static final String PRODUCT_ID_onetime = "android.test.purchased";


    private static final String TAG = "billing details";

    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();

    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {

        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if (billingResult.getResponseCode() == 0) {
                sharedPref.SET_PURCHASED(true);
                Toast.makeText(getApplicationContext(), "adsremovedone", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(PremiumActivity.this, MainActivity.class);
//                intent.setFlags(268468224);
//                startActivity(intent);
                finish();
            }
        }
    };
    private BillingClient billingClient;
    public static SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vpn_premium);

        Constant.FullScreencall(this);

        sharedPref = new SharedPref(getApplicationContext());
        this.billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        this.billingClient.startConnection(this);

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ServerActivity.class));
                finish();
            }
        });


        findViewById(R.id.btn_yearly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPref.GET_PURCHASED().booleanValue()) {
                    Toast.makeText(getApplicationContext(), "Payment Sucessfull" + "", 0).show();
                } else if (billingClient.isReady()) {
                    initiateSubPurchase_yearly();
                } else {
                    BillingClient build = BillingClient.newBuilder(getApplicationContext()).enablePendingPurchases().setListener(VPNPremiumActivity.this).build();
                    billingClient = build;
                    build.startConnection(new BillingClientStateListener() {

                        @Override
                        public void onBillingServiceDisconnected() {
                        }

                        @Override
                        public void onBillingSetupFinished(BillingResult billingResult) {
                            if (billingResult.getResponseCode() == 0) {
                                initiateSubPurchase_yearly();
                                return;
                            }
                            Context applicationContext = getApplicationContext();
                            Toast.makeText(applicationContext, "Error " + billingResult.getDebugMessage(), 0).show();
                        }
                    });
                }
            }
        });
        findViewById(R.id.btn_monthly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPref.GET_PURCHASED().booleanValue()) {
                    Toast.makeText(getApplicationContext(), "Payment Sucessfull" + "", 0).show();
                } else if (billingClient.isReady()) {
                    initiateSubPurchase_monthly();
                } else {
                    BillingClient build = BillingClient.newBuilder(getApplicationContext()).enablePendingPurchases().setListener(VPNPremiumActivity.this).build();
                    billingClient = build;
                    build.startConnection(new BillingClientStateListener() {

                        @Override
                        public void onBillingServiceDisconnected() {
                        }

                        @Override
                        public void onBillingSetupFinished(BillingResult billingResult) {
                            if (billingResult.getResponseCode() == 0) {
                                initiateSubPurchase_monthly();
                                return;
                            }
                            Context applicationContext = getApplicationContext();
                            Toast.makeText(applicationContext, "Error " + billingResult.getDebugMessage(), 0).show();
                        }
                    });
                }
            }
        });
        findViewById(R.id.btn_weekly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPref.GET_PURCHASED().booleanValue()) {
                    Toast.makeText(getApplicationContext(), "Payment Sucessfull" + "", 0).show();
                } else if (billingClient.isReady()) {
                    initiateSubPurchase_weekly();
                } else {
                    BillingClient build = BillingClient.newBuilder(getApplicationContext()).enablePendingPurchases().setListener(VPNPremiumActivity.this).build();
                    billingClient = build;
                    build.startConnection(new BillingClientStateListener() {

                        @Override
                        public void onBillingServiceDisconnected() {
                        }

                        @Override
                        public void onBillingSetupFinished(BillingResult billingResult) {
                            if (billingResult.getResponseCode() == 0) {
                                initiateSubPurchase_weekly();
                                return;
                            }
                            Context applicationContext = getApplicationContext();
                            Toast.makeText(applicationContext, "Error " + billingResult.getDebugMessage(), 0).show();
                        }
                    });
                }
            }
        });
        findViewById(R.id.btn_onetime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPref.GET_PURCHASED().booleanValue()) {
                    Toast.makeText(getApplicationContext(), "Payment Sucessfull" + "", 0).show();
                } else if (billingClient.isReady()) {
                    initiatePurchase();
                } else {
                    BillingClient build = BillingClient.newBuilder(getApplicationContext()).enablePendingPurchases().setListener(VPNPremiumActivity.this).build();
                    billingClient = build;
                    build.startConnection(new BillingClientStateListener() {

                        @Override
                        public void onBillingServiceDisconnected() {
                        }

                        @Override
                        public void onBillingSetupFinished(BillingResult billingResult) {
                            if (billingResult.getResponseCode() == 0) {
                                initiatePurchase();
                                return;
                            }
                            Context applicationContext = getApplicationContext();
                            Toast.makeText(applicationContext, "Error " + billingResult.getDebugMessage(), 0).show();
                        }
                    });
                }
            }
        });
        findViewById(R.id.txt_restore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restorePreviousPuchases();
            }
        });
        findViewById(R.id.txt_privacypolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.privacyPolicy(VPNPremiumActivity.this);
            }
        });
        findViewById(R.id.txt_termofcondition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.TermsOfService(VPNPremiumActivity.this);
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
        if (billingResult.getResponseCode() == 0 && list != null) {
            handlePurchases(list);
        } else if (billingResult.getResponseCode() == 7) {
            List<Purchase> purchasesListinapp = this.billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
            List<Purchase> purchasesListsub = this.billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
            if (purchasesListinapp != null) {
                handlePurchases(purchasesListinapp);
            }
            if (purchasesListsub != null) {
                handlePurchases(purchasesListsub);
            }
        } else if (billingResult.getResponseCode() == 1) {
            Toast.makeText(getApplicationContext(), "Purchase Canceled", 0).show();
        } else {
            Context applicationContext = getApplicationContext();
            Toast.makeText(applicationContext, "Error " + billingResult.getDebugMessage(), 0).show();
        }
    }


    private void initiatePurchase() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(PRODUCT_ID_onetime);
        SkuDetailsParams.Builder newBuilder = SkuDetailsParams.newBuilder();
        newBuilder.setSkusList(arrayList).setType(BillingClient.SkuType.INAPP);
        this.billingClient.querySkuDetailsAsync(newBuilder.build(), new SkuDetailsResponseListener() {

            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                if (billingResult.getResponseCode() == 0) {
                    billingClient.launchBillingFlow(VPNPremiumActivity.this, BillingFlowParams.newBuilder().setSkuDetails(list.get(0)).build());
                    return;
                }
                Context applicationContext = getApplicationContext();
                Toast.makeText(applicationContext, "Msg" + " " + billingResult.getDebugMessage(), 0).show();
            }
        });
    }

    private void initiateSubPurchase_weekly() {
        final List<String> skuList = new ArrayList<>();
        skuList.add(PRODUCT_ID_weekly); // SKU Id

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();
        billingClient.querySkuDetailsAsync(params,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            if (skuDetailsList.size() == 1) {
                                SkuDetails skuDetails = skuDetailsList.get(0);
                                billingClient.launchBillingFlow(VPNPremiumActivity.this, BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build());
//                                BillingFlowParams.Builder builder = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).setType(BillingClient.SkuType.SUBS);
//                                int responseCode = billingClient.launchBillingFlow(this, builder.build());
                            }
                        }
                    }
                });
    }

    private void initiateSubPurchase_monthly() {
        final List<String> skuList = new ArrayList<>();
        skuList.add(PRODUCT_ID_montly); // SKU Id

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();
        billingClient.querySkuDetailsAsync(params,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            if (skuDetailsList.size() == 1) {
                                SkuDetails skuDetails = skuDetailsList.get(0);
                                billingClient.launchBillingFlow(VPNPremiumActivity.this, BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build());
//                                BillingFlowParams.Builder builder = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).setType(BillingClient.SkuType.SUBS);
//                                int responseCode = billingClient.launchBillingFlow(this, builder.build());
                            }
                        }
                    }
                });
    }

    private void initiateSubPurchase_yearly() {
        final List<String> skuList = new ArrayList<>();
        skuList.add(PRODUCT_ID_yearly); // SKU Id

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();
        billingClient.querySkuDetailsAsync(params,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            if (skuDetailsList.size() == 1) {
                                SkuDetails skuDetails = skuDetailsList.get(0);
                                billingClient.launchBillingFlow(VPNPremiumActivity.this, BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build());
//                                BillingFlowParams.Builder builder = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).setType(BillingClient.SkuType.SUBS);
//                                int responseCode = billingClient.launchBillingFlow(this, builder.build());
                            }
                        }
                    }
                });
    }

    private void restorePreviousPuchases() {
        if (sharedPref.GET_PURCHASED().booleanValue()) {
            Toast.makeText(getApplicationContext(), "Once Payment Done", 0).show();
        } else {
            Toast.makeText(getApplicationContext(), "No Premium Service Purchased yet.. please select any one.", 0).show();
        }


        *//*billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {

                if (list != null) {

                    for (Purchase purchase : list) {
                        ArrayList<String> skus = purchase.getSkus();

                        if (skus != null) {
                            for (String sku : skus) {
                                if (!PRODUCT_ID.equals(sku) && purchase.getPurchaseState() == 0) {
                                    Toast.makeText(getApplicationContext(), "Ads Loaded", 0).show();
                                }
//                                setPurchasedItem(sku);

                            }
                        }

                    }

                } else {
                    Log.d("DEBUGGING...", "onCreate: NULL purchaseList");
                }
            }
        });*//*

    }

    public void handlePurchases(List<Purchase> list) {
        for (Purchase purchase : list) {
            List<String> purchasesSkus = purchase.getSkus();
            if (PRODUCT_ID_onetime.equals(purchasesSkus) || PRODUCT_ID_weekly.equals(purchasesSkus) || PRODUCT_ID_montly.equals(purchasesSkus) || PRODUCT_ID_yearly.equals(purchasesSkus) || purchase.getPurchaseState() != 1) {
                if (!PRODUCT_ID_onetime.equals(purchasesSkus) || !PRODUCT_ID_weekly.equals(purchasesSkus) || !PRODUCT_ID_montly.equals(purchasesSkus) || !PRODUCT_ID_yearly.equals(purchasesSkus) && purchase.getPurchaseState() == 2) {
                    Toast.makeText(getApplicationContext(), "adsremovepending", 0).show();
                } else if (!PRODUCT_ID_onetime.equals(purchasesSkus) || !PRODUCT_ID_weekly.equals(purchasesSkus) || !PRODUCT_ID_montly.equals(purchasesSkus) || !PRODUCT_ID_yearly.equals(purchasesSkus) && purchase.getPurchaseState() == 0) {
                    Toast.makeText(getApplicationContext(), "adsremovenot", 0).show();
                }
            } else if (!purchase.isAcknowledged()) {
                billingClient.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(), this.ackPurchase);
            } else if (this.sharedPref.GET_PURCHASED().booleanValue()) {
                sharedPref.SET_PURCHASED(true);
//                Toast.makeText(getApplicationContext(), "adsremovedone", 0).show();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.setFlags(268468224);
//                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BillingClient billingClient2 = this.billingClient;
        if (billingClient2 != null) {
            billingClient2.endConnection();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), ServerActivity.class));
        finish();
    }

    @Override
    public void onBillingServiceDisconnected() {

    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            List skuList = new ArrayList<>();
            skuList.add(PRODUCT_ID_weekly);
            skuList.add(PRODUCT_ID_montly);
            skuList.add(PRODUCT_ID_yearly);

            List skuListonetime = new ArrayList<>();
            skuListonetime.add(PRODUCT_ID_onetime);
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            SkuDetailsParams.Builder paramsonetime = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
            paramsonetime.setSkusList(skuListonetime).setType(BillingClient.SkuType.INAPP);
            billingClient.querySkuDetailsAsync(params.build(), this);
            billingClient.querySkuDetailsAsync(paramsonetime.build(), this);
        }
    }

    @Override
    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
        for (SkuDetails skuDetails : list) {
            String sku = skuDetails.getSku();
            String price = skuDetails.getPrice();

            mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);

            if (PRODUCT_ID_yearly.equals(sku)) {
                ((TextView) findViewById(R.id.txt_yearly)).setText(price);
            } else if (PRODUCT_ID_montly.equals(sku)) {
                ((TextView) findViewById(R.id.txt_monthly)).setText(price);
            } else if (PRODUCT_ID_weekly.equals(sku)) {
                ((TextView) findViewById(R.id.txt_weekly)).setText(price);
            } else if (PRODUCT_ID_onetime.equals(sku)) {
                ((TextView) findViewById(R.id.txt_onetime)).setText(price);
            }
        }
    }*/
}