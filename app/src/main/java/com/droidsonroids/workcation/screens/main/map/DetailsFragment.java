package com.droidsonroids.workcation.screens.main.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.view.View;
import android.widget.FrameLayout;

import com.droidsonroids.workcation.R;
import com.droidsonroids.workcation.common.maps.MapBitmapCache;
import com.droidsonroids.workcation.common.maps.PulseOverlayLayout;
import com.droidsonroids.workcation.common.model.Distance;
import com.droidsonroids.workcation.common.model.Duration;
import com.droidsonroids.workcation.common.model.Place;
import com.droidsonroids.workcation.common.mvp.MvpFragment;
import com.droidsonroids.workcation.common.transitions.ScaleDownImageTransition;
import com.droidsonroids.workcation.common.transitions.TransitionUtils;
import com.droidsonroids.workcation.common.views.HorizontalRecyclerViewScrollListener;
import com.droidsonroids.workcation.common.views.TranslateItemAnimator;
import com.droidsonroids.workcation.screens.main.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Location fragment showing locations on map
 */
public class DetailsFragment extends MvpFragment<DetailsFragmentView, DetailsFragmentPresenter>
        implements DetailsFragmentView,
        OnMapReadyCallback,
        BaliPlacesAdapter.OnPlaceClickListener,
        HorizontalRecyclerViewScrollListener.OnItemCoverListener,
        PulseOverlayLayout.OnClick,
        MapInterface {

    public static final String TAG = DetailsFragment.class.getSimpleName();

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.container)
    FrameLayout containerLayout;
    @BindView(R.id.mapOverlayLayout)
    PulseOverlayLayout mapOverlayLayout;

    private List<Place> baliPlaces;
    private BaliPlacesAdapter baliAdapter;
    private String currentTransitionName;
    private Scene detailsScene;
    private DetailsLayout detailsLayout;
    public boolean isDetailsLayoutHidden = true;

    public static Fragment newInstance( final Context ctx ) {
        DetailsFragment fragment = new DetailsFragment();
        ScaleDownImageTransition transition = new ScaleDownImageTransition( ctx, MapBitmapCache.instance().getBitmap() );
        transition.addTarget( ctx.getString( R.string.mapPlaceholderTransition ) );
        transition.setDuration( 600 );
        fragment.setEnterTransition( transition );
        return fragment;
    }

    @Override
    protected DetailsFragmentPresenter createPresenter() {
        return new DetailsFragmentPresenterImpl();
    }

    @Override
    public void onBackPressed() {
        if( detailsLayout != null ) {
            presenter.onBackPressedWithScene();
        } else {
            ( (MainActivity) getActivity() ).superOnBackPressed();
        }
    }

    private View getSharedViewByPosition( final int childPosition ) {
        for( int i = 0; i < recyclerView.getChildCount(); i++ ) {
            if( childPosition == recyclerView.getChildAdapterPosition( recyclerView.getChildAt( i ) ) ) {
                return recyclerView.getChildAt( i );
            }
        }
        return null;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_details;
    }

    @Override
    public void onViewCreated( final View view, @Nullable final Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );
        setupBaliData();
        setupMapFragment();
        setupRecyclerView();

    }

    private void setupBaliData() {
        presenter.provideBaliData();
    }

    private void setupMapFragment() {
        MySupportMapFragment mySupportMapFragment = ( (MySupportMapFragment) getChildFragmentManager()
                .findFragmentById( R.id.mapFragment ) );

        mySupportMapFragment.setListener( this );
        mySupportMapFragment.getMapAsync( this );
    }

    private boolean isRecyclerHiden = false;

    private void translateLowRecycler( View view ) {
        ViewCompat.animate( view )
                .translationYBy( recyclerView.getHeight() / 2.5f )
                .setDuration( 100 )
                .start();
    }

    private void translateNormalRecycler( View view ) {
        ViewCompat.animate( view )
                .translationYBy( -recyclerView.getHeight() / 2.5f )
                .setDuration( 100 )
                .start();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(
                new LinearLayoutManager( getContext(), LinearLayoutManager.HORIZONTAL, false ) );
        baliAdapter = new BaliPlacesAdapter( this, getActivity() );
    }

    @Override
    public void onMapReady( final GoogleMap googleMap ) {
        mapOverlayLayout.setupMap( googleMap );
        setupGoogleMap();
        addDataToRecyclerView();
    }

    private void setupGoogleMap() {
        presenter.moveMapAndAddMarker();
    }

    private void addDataToRecyclerView() {
        recyclerView.setItemAnimator( new TranslateItemAnimator() );
        recyclerView.setAdapter( baliAdapter );
        baliAdapter.setPlacesList( baliPlaces );
        recyclerView.addOnScrollListener( new HorizontalRecyclerViewScrollListener( this ) );
    }

    @Override
    public void onPlaceClicked( final View sharedView, final String transitionName, final int position ) {
        currentTransitionName = transitionName;
        detailsLayout = DetailsLayout
                .showScene( getActivity(), containerLayout, sharedView, transitionName, baliPlaces.get( position ) );

        drawRoute( position );
        hideAllMarkers();
        isDetailsLayoutHidden = false;
    }

    private void drawRoute( final int position ) {
        if( ActivityCompat
                .checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat
                .checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: permission
            presenter.drawRoute( mapOverlayLayout.getCurrentLatLng( getContext() ), position );
        }
    }

    private void hideAllMarkers() {
        mapOverlayLayout.setOnCameraIdleListener( null );
        mapOverlayLayout.hideAllMarkers();
    }

    @Override
    public void drawPolyLinesOnMap( final ArrayList<LatLng> polyLines ) {
        getActivity().runOnUiThread( () -> mapOverlayLayout.addPolyline( polyLines ) );
    }

    @Override
    public void provideBaliData( final List<Place> places ) {
        baliPlaces = places;
    }

    @Override
    public void onBackPressedWithScene( final LatLngBounds latLngBounds ) {
        int childPosition = TransitionUtils.getItemPositionFromTransition( currentTransitionName );
        DetailsLayout
                .hideScene( getActivity(), containerLayout, getSharedViewByPosition( childPosition ), currentTransitionName );
        notifyLayoutAfterBackPress( childPosition );
        mapOverlayLayout.onBackPressed( latLngBounds );
        detailsScene = null;
        detailsLayout = null;
        isDetailsLayoutHidden = true;

    }

    private void notifyLayoutAfterBackPress( final int childPosition ) {
        containerLayout.removeAllViews();
        containerLayout.addView( recyclerView );
        recyclerView.requestLayout();
        baliAdapter.notifyItemChanged( childPosition );
    }

    @Override
    public void moveMapAndAddMaker( final LatLngBounds latLngBounds ) {
        mapOverlayLayout.moveCamera( latLngBounds );
        mapOverlayLayout.setOnCameraIdleListener( () -> {
            for( int i = 0; i < baliPlaces.size(); i++ ) {
                mapOverlayLayout.createAndShowMarker( this, i, baliPlaces.get( i ).getLatLng() );
            }
            mapOverlayLayout.setOnCameraIdleListener( null );
        } );
        mapOverlayLayout.setOnCameraMoveListener( mapOverlayLayout::refresh );
    }

    @Override
    public void updateMapZoomAndRegion( final LatLng northeastLatLng, final LatLng southwestLatLng ) {
        getActivity().runOnUiThread( () -> {
            mapOverlayLayout.animateCamera( new LatLngBounds( southwestLatLng, northeastLatLng ) );
            mapOverlayLayout.setOnCameraIdleListener( () -> {
                if( !isDetailsLayoutHidden ) {
                    mapOverlayLayout.drawStartAndFinishMarker();
                }
            } );
        } );
    }

    @Override
    public void updateData( int position, Distance distance, Duration duration ) {
        Place place = baliPlaces.get( position );
        place.setDistanceFromCurrentLocation( distance );
        place.setDurationFromCurrentLocation( duration );
        DetailsLayout.setDurationText( getActivity(), containerLayout, duration );
    }

    @Override
    public void onItemCover( final int position ) {
        // mapOverlayLayout.showMarker( position );
    }

    @Override
    public void onNumberClicked( int position ) {
        mapOverlayLayout.showMarker( position );

        LatLng latLng = baliPlaces.get( position ).getLatLng();
        getActivity().runOnUiThread( () -> {
            mapOverlayLayout.animateCamera( latLng );
        } );

    }

    @Override
    public void onMarkerClick( int position ) {
        recyclerView.smoothScrollToPosition( position );
        mapOverlayLayout.showMarker( position );
    }

    @Override
    public void onMapClicked() {
        if( !isDetailsLayoutHidden ) {
            presenter.onBackPressedWithScene();
        } else {
            if( isRecyclerHiden ) {
                translateNormalRecycler( recyclerView );
                isRecyclerHiden = false;
            } else {
                translateLowRecycler( recyclerView );
                isRecyclerHiden = true;

            }
        }
    }
}
