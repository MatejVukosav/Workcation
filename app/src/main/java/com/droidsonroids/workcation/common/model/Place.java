package com.droidsonroids.workcation.common.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class Place {
    @SerializedName("name")
    private String name;
    @SerializedName("opening_hours")
    private String openingHours;
    @SerializedName("price")
    private int price;
    @SerializedName("description")
    private String description;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;
    @SerializedName("photo")
    private List<String> photo;

    private Duration durationFromCurrentLocation;
    private Distance distanceFromCurrentLocation;

    public String getName() {
        return name;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public List<String> getPhotoList() {
        return photo;
    }

    public LatLng getLatLng() {
        return new LatLng( lat, lng );
    }

    public Duration getDurationFromCurrentLocation() {
        return durationFromCurrentLocation;
    }

    public void setDurationFromCurrentLocation( Duration durationFromCurrentLocation ) {
        this.durationFromCurrentLocation = durationFromCurrentLocation;
    }

    public Distance getDistanceFromCurrentLocation() {
        return distanceFromCurrentLocation;
    }

    public void setDistanceFromCurrentLocation( Distance distanceFromCurrentLocation ) {
        this.distanceFromCurrentLocation = distanceFromCurrentLocation;
    }
}
