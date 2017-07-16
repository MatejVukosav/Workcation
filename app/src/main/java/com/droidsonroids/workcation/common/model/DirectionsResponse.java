package com.droidsonroids.workcation.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class DirectionsResponse {

    @SerializedName("routes")
    List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }
}
