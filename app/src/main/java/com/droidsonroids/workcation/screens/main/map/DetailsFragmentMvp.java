package com.droidsonroids.workcation.screens.main.map;

import com.droidsonroids.workcation.common.model.Distance;
import com.droidsonroids.workcation.common.model.Duration;
import com.droidsonroids.workcation.common.model.Place;
import com.droidsonroids.workcation.common.mvp.MvpPresenter;
import com.droidsonroids.workcation.common.mvp.MvpView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vuki on 15.7.2017..
 */

public interface DetailsFragmentMvp {

    interface View extends MvpView {
        void drawPolyLinesOnMap( ArrayList<LatLng> decode );

        void provideBaliData( List<Place> places );

        void onBackPressedWithScene( LatLngBounds latLngBounds );

        void moveMapAndAddMaker( LatLngBounds latLngBounds );

        void updateMapZoomAndRegion( LatLng northeastLatLng, LatLng southwestLatLng );

        void updateData( int position, Distance distance, Duration duration );
    }

    interface Presenter extends MvpPresenter<DetailsFragmentMvp.View> {

        void drawRoute( LatLng first, final int position );

        void provideBaliData();

        void onBackPressedWithScene();

        void moveMapAndAddMarker();
    }
}
