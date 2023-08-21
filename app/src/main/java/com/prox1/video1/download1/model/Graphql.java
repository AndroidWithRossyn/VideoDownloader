package com.prox1.video1.download1.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Graphql implements Serializable {

    @SerializedName("shortcode_media")
    private ShortcodeMedia shortcode_media;

    public ShortcodeMedia getShortcode_media() {
        return shortcode_media;
    }

    public void setShortcode_media(ShortcodeMedia shortcode_media) {
        this.shortcode_media = shortcode_media;
    }
}
