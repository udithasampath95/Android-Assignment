package com.example.androidassignment.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Support implements Serializable {
    @SerializedName("url")
    private String url;
    @SerializedName("text")
    private String text;
}
