package com.droidsonroids.workcation.screens.main.map;

import com.droidsonroids.workcation.common.maps.MapsUtil;
import com.droidsonroids.workcation.common.data.providers.MapDataProvider;
import com.droidsonroids.workcation.common.model.Bounds;
import com.droidsonroids.workcation.common.model.DirectionsResponse;
import com.droidsonroids.workcation.common.model.Distance;
import com.droidsonroids.workcation.common.model.Duration;
import com.droidsonroids.workcation.common.data.providers.MapsApiManager;
import com.droidsonroids.workcation.common.model.Route;
import com.droidsonroids.workcation.common.mvp.MvpPresenterImpl;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetailsFragmentPresenterImpl extends MvpPresenterImpl<DetailsFragmentMvp.View>
        implements DetailsFragmentMvp.Presenter {

    private MapsApiManager mapsApiManager = MapsApiManager.instance();
    private MapDataProvider mapDataProvider = MapDataProvider.instance();

    @Override
    public void drawRoute( final LatLng first, final int position ) {

        final LatLng second = new LatLng( mapDataProvider.getLatByPosition( position ), mapDataProvider.getLngByPosition( position ) );

        mapsApiManager.getRoute( first, second, new Callback() {
            @Override
            public void onFailure( final Call call, final IOException e ) {
                e.printStackTrace();
            }

            @Override
            public void onResponse( final Call call, final Response response ) throws IOException {
                Route route = new Gson()
                        .fromJson( response.body().charStream(), DirectionsResponse.class )
                        .getRoutes()
                        .get( 0 );

                providePolylineToDraw( route.getOverviewPolyline().getPoints() );
                updateMapZoomAndRegion( route.getBounds() );
                updateData( position, route.getLegs().get( 0 ).getDistance(), route.getLegs().get( 0 ).getDuration() );
            }
        } );
    }

    @Override
    public void provideBaliData() {
        getView().provideBaliData( mapDataProvider.providePlacesList() );
    }

    @Override
    public void onBackPressedWithScene() {
        getView().onBackPressedWithScene( mapDataProvider.provideLatLngBoundsForAllPlaces() );
    }

    @Override
    public void moveMapAndAddMarker() {
        getView().moveMapAndAddMaker( mapDataProvider.provideLatLngBoundsForAllPlaces() );
    }

    private void updateMapZoomAndRegion( final Bounds bounds ) {
        bounds
                .getSouthwest()
                .setLat( MapsUtil.increaseLatitude( bounds ) );
        getView()
                .updateMapZoomAndRegion( bounds.getNortheastLatLng(), bounds.getSouthwestLatLng() );
    }

    private void providePolylineToDraw( final String points ) {
        getView()
                .drawPolyLinesOnMap( new ArrayList<>( PolyUtil.decode( points ) ) );
    }

    private void updateData( int position, Distance distance, Duration duration ) {
        getView()
                .updateData( position, distance, duration );

    }
}
