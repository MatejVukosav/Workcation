package com.droidsonroids.workcation.common.data.providers;

import android.content.res.AssetManager;

import com.droidsonroids.workcation.common.WorkcationApp;
import com.droidsonroids.workcation.common.model.Place;
import com.droidsonroids.workcation.common.model.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class MapDataProvider {

    private final static String JSON_PATH = "bali.json";
    private static MapDataProvider sInstance;
    private Places mPlaces;

    private MapDataProvider() {
    }

    public static MapDataProvider instance() {
        if( sInstance == null ) {
            sInstance = new MapDataProvider();
            return sInstance;
        }
        return sInstance;
    }

    public void initialize() {
        try {
            AssetManager assetManager = WorkcationApp.getInstance().getAssets();
            InputStream inputStream;

            inputStream = assetManager.open( JSON_PATH );

            Gson gson = new Gson();
            Reader reader = new InputStreamReader( inputStream );

            mPlaces = gson.fromJson( reader, Places.class );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public LatLngBounds provideLatLngBoundsForAllPlaces() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for( Place place : mPlaces.getPlacesList() ) {
            builder.include( new LatLng( place.getLat(), place.getLng() ) );
        }
        return builder.build();
    }

    public List<Place> providePlacesList() {
        return mPlaces.getPlacesList();
    }

    public double getLatByPosition( final int position ) {
        return mPlaces.getPlacesList().get( position ).getLat();
    }

    public double getLngByPosition( final int position ) {
        return mPlaces.getPlacesList().get( position ).getLng();
    }
}
