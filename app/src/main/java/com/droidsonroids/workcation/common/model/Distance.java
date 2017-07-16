package com.droidsonroids.workcation.common.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vuki on 30.6.2017..
 */

public class Distance implements Serializable {

    @SerializedName("value")
    int value;

    @SerializedName("text")
    String text;
}
