package com.droidsonroids.workcation.common.maps;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.droidsonroids.workcation.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Layout for pulse number elements over map
 */
public class PulseOverlayLayout extends MapOverlayLayout {
    private static final int ANIMATION_DELAY_FACTOR = 100;

    private PulseMarkerView startMarker, finishMarker;
    private int scaleAnimationDelay = 100;

    public PulseOverlayLayout( final Context context ) {
        this( context, null );
    }

    public PulseOverlayLayout( final Context context, final AttributeSet attrs ) {
        super( context, attrs );
        inflate( context, R.layout.pulse_wrapper_layout, this );
    }

    @SuppressWarnings("unused")
    public void setupStartAndFinishMarkers( final Point point, final LatLng latLng ) {
        startMarker = new PulseMarkerView( getContext(), latLng, point );
        finishMarker = new PulseMarkerView( getContext(), latLng, point );
    }

    public void removeStartMarker() {
        removeMarker( startMarker );
    }

    public void removeFinishMarker() {
        removeMarker( finishMarker );
    }

    @SuppressWarnings("unused")
    public void addStartMarker( final LatLng latLng ) {
        startMarker = createPulseMarkerView( latLng );
        startMarker.updatePulseViewLayoutParams( googleMap.getProjection().toScreenLocation( latLng ) );
        addMarker( startMarker );
        startMarker.show();
    }

    public void addFinishMarker( final LatLng latLng, String text ) {
        finishMarker = createPulseMarkerView( latLng );
        finishMarker.setText( text );
        finishMarker.updatePulseViewLayoutParams( googleMap.getProjection().toScreenLocation( latLng ) );
        addMarker( finishMarker );
        finishMarker.show();
    }

    @NonNull
    private PulseMarkerView createPulseMarkerView( final LatLng latLng ) {
        return new PulseMarkerView( getContext(), latLng, googleMap.getProjection().toScreenLocation( latLng ) );
    }

    @NonNull
    private PulseMarkerView createPulseMarkerView( final int position, final Point point, final LatLng latLng ) {
        PulseMarkerView pulseMarkerView = new PulseMarkerView( getContext(), latLng, point, position );
        addMarker( pulseMarkerView );
        return pulseMarkerView;
    }

    public void createAndShowMarker( OnClick onClickListener, final int position, final LatLng latLng ) {
        PulseMarkerView marker = createPulseMarkerView( position, googleMap.getProjection().toScreenLocation( latLng ), latLng );
        marker.showWithDelay( scaleAnimationDelay );
        marker.setOnClickListener( v -> onClickListener.onMarkerClick( position ) );
        scaleAnimationDelay += ANIMATION_DELAY_FACTOR;

    }

    public void showMarker( final int position ) {
        ( (PulseMarkerView) markersList.get( position ) ).pulse();
    }

    public void drawStartAndFinishMarker() {
        //   addStartMarker( (LatLng) polyLines.get( 0 ) );
        addFinishMarker( polyLines.get( polyLines.size() - 1 ), "X" );
        setOnCameraIdleListener( null );
    }

    public void onBackPressed( final LatLngBounds latLngBounds ) {
        moveCamera( latLngBounds );
        removeStartAndFinishMarkers();
        removeCurrentPolyline();
        showAllMarkers();
        refresh();
    }

    private void removeStartAndFinishMarkers() {
        removeStartMarker();
        removeFinishMarker();
    }

    public interface OnClick {
        void onMarkerClick( int position );
    }
}
