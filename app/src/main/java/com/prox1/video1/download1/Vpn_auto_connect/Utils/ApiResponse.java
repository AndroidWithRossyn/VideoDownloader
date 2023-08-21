package com.prox1.video1.download1.Vpn_auto_connect.Utils;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("ip")
    private String ip;

    public String getIp() {
        return this.ip;
    }

    public void setIp(String str) {
        this.ip = str;
    }
}