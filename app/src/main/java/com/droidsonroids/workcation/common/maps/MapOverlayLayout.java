package com.droidsonroids.workcation.common.maps;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.RequiresPermission;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapOverlayLayout extends FrameLayout {

    protected List<MarkerView> markersList;
    protected Polyline currentPolyline;
    protected GoogleMap googleMap;
    protected ArrayList<LatLng> polyLines;

    public MapOverlayLayout( final Context context ) {
        this( context, null );
    }

    public MapOverlayLayout( final Context context, final AttributeSet attrs ) {
        super( context, attrs );
        markersList = new ArrayList<>();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    protected void addMarker( final MarkerView view ) {
        markersList.add( view );
        addView( view );
    }

    protected void removeMarker( final MarkerView view ) {
        markersList.remove( view );
        removeView( view );
    }

    public void showAllMarkers() {
        for ( int i = 0; i < markersList.size(); i++ ) {
            markersList.get( i ).show();
        }
    }

    public void hideAllMarkers() {
        for ( int i = 0; i < markersList.size(); i++ ) {
            markersList.get( i ).hide();
        }
    }

    public void showMarker( final int position ) {
        markersList.get( position ).show();
    }

    private void refresh( final int position, final Point point ) {
        markersList.get( position ).refresh( point );
    }

    public void setupMap( final GoogleMap googleMap ) {
        this.googleMap = googleMap;
    }

    public void refresh() {
        Projection projection = googleMap.getProjection();
        for ( int i = 0; i < markersList.size(); i++ ) {
            refresh( i, projection.toScreenLocation( markersList.get( i ).latLng() ) );
        }
    }

    public void setOnCameraIdleListener( final GoogleMap.OnCameraIdleListener listener ) {
        googleMap.setOnCameraIdleListener( listener );
    }

    public void setOnCameraMoveListener( final GoogleMap.OnCameraMoveListener listener ) {
        googleMap.setOnCameraMoveListener( listener );
    }

    public void moveCamera( final LatLngBounds latLngBounds ) {
        googleMap.moveCamera( CameraUpdateFactory.newLatLngBounds( latLngBounds, 150 ) );
    }

    public void animateCamera( final LatLngBounds bounds ) {
        int width = getWidth();
        int height = getHeight();
        int padding = MapsUtil.DEFAULT_MAP_PADDING;
        googleMap.animateCamera( CameraUpdateFactory.newLatLngBounds( bounds, width, height, padding ) );
    }

    public void animateCamera( final LatLng location ) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target( location )
                .zoom( MapsUtil.DEFAULT_MAP_PADDING )
                .build();
        googleMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );
    }

    @SuppressWarnings("unused")
    public LatLng getCurrentCameraLatLng() {
        return new LatLng( googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude );
    }

    @SuppressWarnings("MissingPermission")
    @RequiresPermission(allOf = { Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION }
    )
    public LatLng getCurrentLatLng( Context context ) {

        Criteria criteria = new Criteria();
        LocationManager locationManager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        String provider = locationManager.getBestProvider( criteria, false );
        Location location = locationManager.getLastKnownLocation( provider );
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        return new LatLng( lat, lng );
    }

    public void addPolyline( final ArrayList<LatLng> polyLines ) {
        this.polyLines = polyLines;
        PolylineOptions options = new PolylineOptions();
        for ( int i = 1; i < polyLines.size(); i++ ) {
            options.add( polyLines.get( i - 1 ), polyLines.get( i ) ).width( 10 ).color( Color.RED ).geodesic( true );
        }
        currentPolyline = googleMap.addPolyline( options );
    }

    public void removeCurrentPolyline() {
        if ( currentPolyline != null ) currentPolyline.remove();
    }
}
