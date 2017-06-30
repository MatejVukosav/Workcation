package com.droidsonroids.workcation.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by droids on 21.11.2016.
 */
public class Leg {

    @SerializedName("distance")
    Distance distance;
    @SerializedName("duration")
    Duration duration;
    @SerializedName("steps")
    List<Step> steps;

    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public List<Step> getSteps() {
        return steps;
    }
}
