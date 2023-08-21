package com.prox1.video1.download1.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Edge implements Serializable {

    @SerializedName("node")
    private Node node;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
