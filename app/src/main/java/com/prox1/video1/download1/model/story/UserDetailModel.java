package com.prox1.video1.download1.model.story;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDetailModel implements Serializable {

    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

