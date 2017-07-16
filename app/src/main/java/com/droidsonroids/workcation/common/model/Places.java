package com.droidsonroids.workcation.common.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Places {

    @SerializedName("places")
    List<Place> placesList;

    public List<Place> getPlacesList() {
        return placesList;
    }
}
