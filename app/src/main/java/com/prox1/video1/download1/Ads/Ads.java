package com.prox1.video1.download1.Ads;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Ads {

    @SerializedName("app_version")
    private String app_version;
    @SerializedName("app_name")
    private String app_name;
    @SerializedName("app_package")
    private String app_package;
    @SerializedName("app_open")
    private String app_open;
    @SerializedName("banner")
    private String banner;
    @SerializedName("interstitial_1")
    private String interstitial_1;
    @SerializedName("native_ads")
    private String native_ads;
    @SerializedName("iron")
    private String iron_id;
    @SerializedName("rewarded")
    private String rewarded;
    @SerializedName("interstitial_2")
    private String interstitial_2;
    @SerializedName("is_one_bool")
    private String is_one_bool;
    @SerializedName("is_two_bool")
    private String is_two_bool;
    @SerializedName("is_third_bool")
    private String is_third_bool;
    @SerializedName("is_fourth_bool")
    private String is_fourth_bool;
    @SerializedName("carrier_id")
    private String carrier_id;
    @SerializedName("country_code")
    private String country_code;
    @SerializedName("app_link")
    private String app_link;
    @SerializedName("force_update")
    private String force_update;

    public String getForce_redirect() {
        return force_redirect;
    }

    public void setForce_redirect(String force_redirect) {
        this.force_redirect = force_redirect;
    }

    @SerializedName("force_redirect")
    private String force_redirect;

    public String getForce_update() {
        return force_update;
    }

    public void setForce_update(String force_update) {
        this.force_update = force_update;
    }

    List<Ads> ads = null;

    public String getIron() {
        return iron_id;
    }

    public void setIron(String iron) {
        this.iron_id = iron;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getApp_link() {
        return app_link;
    }

    public void setApp_link(String app_link) {
        this.app_link = app_link;
    }

    public Ads(String AppVersion, String Appname, String AppPackage, String AppOpen, String Banner, String FullScreen, String Native_ads, String Rewarded, String interstitial_2, String Iron, String is_one_bool, String is_two_bool, String is_third_bool, String is_fourth_bool, String Carrier_Id, String Country_Code, String Force_Update, String App_Link) {
        this.app_version = AppVersion;
        this.app_name = Appname;
        this.app_package = AppPackage;
        this.app_open = AppOpen;
        this.banner = Banner;
        this.interstitial_1 = FullScreen;
        this.native_ads = Native_ads;
        this.rewarded = Rewarded;
        this.interstitial_2 = interstitial_2;
        this.iron_id = Iron;
        this.is_one_bool = is_one_bool;
        this.is_two_bool = is_two_bool;
        this.is_third_bool = is_third_bool;
        this.is_fourth_bool = is_fourth_bool;
        this.carrier_id = Carrier_Id;
        this.country_code = Country_Code;
        this.force_update = Force_Update;
        this.app_link = App_Link;
    }

    public Ads(String s) {

    }

    public String getAppName() {
        return app_name;
    }

    public String getApp_package() {
        return app_package;
    }

    public String getApp_open() {
        return app_open;
    }

    public String getBanner() {
        return banner;
    }

    public String getInterstitial_1() {
        return interstitial_1;
    }

    public String getNative_ads() {
        return native_ads;
    }

    public String getRewarded() {
        return rewarded;
    }

    public String getInterstitial_2() {
        return interstitial_2;
    }


    public String getIs_one_bool() {
        return is_one_bool;
    }

    public void setIs_one_bool(String is_one_bool) {
        this.is_one_bool = is_one_bool;
    }

    public String getIs_two_bool() {
        return is_two_bool;
    }

    public void setIs_two_bool(String is_two_bool) {
        this.is_two_bool = is_two_bool;
    }

    public String getIs_third_bool() {
        return is_third_bool;
    }

    public void setIs_third_bool(String is_third_bool) {
        this.is_third_bool = is_third_bool;
    }

    public String getCarrier_id() {
        return carrier_id;
    }

    public void setCarrier_id(String carrier_id) {
        this.carrier_id = carrier_id;
    }

    public String getIs_fourth_bool() {
        return is_fourth_bool;
    }

    public void setIs_fourth_bool(String is_fourth_bool) {
        this.is_fourth_bool = is_fourth_bool;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }
}