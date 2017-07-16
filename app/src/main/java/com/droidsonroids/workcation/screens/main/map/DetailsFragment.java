package com.droidsonroids.workcation.screens.main.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.droidsonroids.workcation.databinding.FragmentDetailsBinding;
import com.droidsonroids.workcation.screens.main.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * Location fragment showing locations on map
 * <p>
 * To make: - button to center user view as beginning.
 * - user location on map
 */
public class DetailsFragment extends MvpFragment<DetailsFragmentMvp.View, DetailsFragmentMvp.Presenter>
        implements DetailsFragmentMvp.View,
        OnMapReadyCallback,
        PlacesAdapter.OnPlaceClickListener,
        HorizontalRecyclerViewScrollListener.OnItemCoverListener,
        PulseOverlayLayout.OnClick,
        MapInterface {

    public static final String TAG = DetailsFragment.class.getCanonicalName();

    private static final String MAP_TRANSITION_PLACEHOLDER = "mapPlaceholderTransition";

    private List<Place> places;
    private PlacesAdapter placesAdapter;
    private String currentTransitionName;
    public boolean isDetailsLayoutHidden = true;
    private boolean isRecyclerHidden = false;
    private DetailsCoordinatorLayout detailsCoordinatorLayout;
    private FragmentDetailsBinding binding;

    public static Fragment newInstance( final Context context ) {
        DetailsFragment fragment = new DetailsFragment();
        fragment.setEnterTransition( fragment.getScaleDownImageTransition( context ) );
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        binding = DataBindingUtil.inflate( inflater, getLayout(), container, false );
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        if ( detailsCoordinatorLayout != null ) {
            presenter.onBackPressedWithScene();
        } else {
            ( (MainActivity) getActivity() ).superOnBackPressed();
        }
    }

    @Override
    protected DetailsFragmentMvp.Presenter createPresenter() {
        return new DetailsFragmentPresenterImpl();
    }

    private View getSharedViewByPosition( final int childPosition ) {
        for ( int i = 0; i < binding.recyclerView.getChildCount(); i++ ) {
            if ( childPosition == binding.recyclerView.getChildAdapterPosition( binding.recyclerView.getChildAt( i ) ) ) {
                return binding.recyclerView.getChildAt( i );
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
        GoogleSupportMapFragment googleSupportMapFragment = ( (GoogleSupportMapFragment) getChildFragmentManager()
                .findFragmentById( R.id.mapFragment ) );

        googleSupportMapFragment.setListener( this );
        googleSupportMapFragment.getMapAsync( this );
    }

    private void translateLowRecycler( View view ) {
        ViewCompat.animate( view )
                .translationYBy( binding.recyclerView.getHeight() / 2.5f )
                .setDuration( 100 )
                .start();
    }

    private void translateNormalRecycler( View view ) {
        ViewCompat.animate( view )
                .translationYBy( -binding.recyclerView.getHeight() / 2.5f )
                .setDuration( 100 )
                .start();
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(
                new LinearLayoutManager( getContext(), LinearLayoutManager.HORIZONTAL, false ) );
        placesAdapter = new PlacesAdapter( this, getActivity() );
    }

    @Override
    public void onMapReady( final GoogleMap googleMap ) {
        binding.mapOverlayLayout.setupMap( googleMap );
        setupGoogleMap();
        addDataToRecyclerView();
    }

    private void setupGoogleMap() {
        presenter.moveMapAndAddMarker();
    }

    private void addDataToRecyclerView() {
        binding.recyclerView.setItemAnimator( new TranslateItemAnimator() );
        binding.recyclerView.setAdapter( placesAdapter );
        placesAdapter.setPlacesList( places );
        binding.recyclerView.addOnScrollListener( new HorizontalRecyclerViewScrollListener( this ) );
    }

    @Override
    public void onPlaceClicked( final View sharedView, final String transitionName, final int position ) {
        currentTransitionName = transitionName;
        detailsCoordinatorLayout = (DetailsCoordinatorLayout) getActivity()
                .getLayoutInflater()
                .inflate( R.layout.item_place, binding.container, false );

        detailsCoordinatorLayout.showScene( binding.container, sharedView, transitionName, places.get( position ) );

        drawRoute( position );
        hideAllMarkers();
        isDetailsLayoutHidden = false;
    }

    private void drawRoute( final int position ) {
        if ( ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED ) {

            // TODO: permission
            presenter.drawRoute( binding.mapOverlayLayout.getCurrentLatLng( getContext() ), position );
        }
    }

    private void hideAllMarkers() {
        binding.mapOverlayLayout.setOnCameraIdleListener( null );
        binding.mapOverlayLayout.hideAllMarkers();
    }

    @Override
    public void drawPolyLinesOnMap( final ArrayList<LatLng> polyLines ) {
        getActivity().runOnUiThread( () -> binding.mapOverlayLayout.addPolyline( polyLines ) );
    }

    @Override
    public void provideBaliData( final List<Place> places ) {
        this.places = places;
    }

    @Override
    public void onBackPressedWithScene( final LatLngBounds latLngBounds ) {
        int childPosition = TransitionUtils.getItemPositionFromTransition( currentTransitionName );
        detailsCoordinatorLayout
                .hideScene( binding.container, getSharedViewByPosition( childPosition ), currentTransitionName );
        notifyLayoutAfterBackPress( childPosition );
        binding.mapOverlayLayout.onBackPressed( latLngBounds );
        detailsCoordinatorLayout = null;
        isDetailsLayoutHidden = true;
    }

    private void notifyLayoutAfterBackPress( final int childPosition ) {
        binding.container.removeAllViews();
        binding.container.addView( binding.recyclerView );
        binding.recyclerView.requestLayout();
        placesAdapter.notifyItemChanged( childPosition );
    }

    @Override
    public void moveMapAndAddMaker( final LatLngBounds latLngBounds ) {
        binding.mapOverlayLayout.moveCamera( latLngBounds );
        binding.mapOverlayLayout.setOnCameraIdleListener( () -> {
            for ( int i = 0; i < places.size(); i++ ) {
                binding.mapOverlayLayout.createAndShowMarker( this, i, places.get( i ).getLatLng() );
            }
            binding.mapOverlayLayout.setOnCameraIdleListener( null );
        } );
        binding.mapOverlayLayout.setOnCameraMoveListener( binding.mapOverlayLayout::refresh );
    }

    @Override
    public void updateMapZoomAndRegion( final LatLng northeastLatLng, final LatLng southwestLatLng ) {
        getActivity().runOnUiThread( () -> {
            binding.mapOverlayLayout.animateCamera( new LatLngBounds( southwestLatLng, northeastLatLng ) );
            binding.mapOverlayLayout.setOnCameraIdleListener( () -> {
                if ( !isDetailsLayoutHidden ) {
                    binding.mapOverlayLayout.drawStartAndFinishMarker();
                }
            } );
        } );
    }

    @Override
    public void updateData( int position, Distance distance, Duration duration ) {
        Place place = places.get( position );
        place.setDistanceFromCurrentLocation( distance );
        place.setDurationFromCurrentLocation( duration );
        if ( detailsCoordinatorLayout != null ) {
            detailsCoordinatorLayout.setDurationText( getActivity(), duration );
        }
    }

    @Override
    public void onItemCover( final int position ) {
        // mapOverlayLayout.showMarker( position );
    }

    @Override
    public void onNumberClicked( int position ) {
        binding.mapOverlayLayout.showMarker( position );

        LatLng latLng = places.get( position ).getLatLng();
        getActivity().runOnUiThread( () -> binding.mapOverlayLayout.animateCamera( latLng ) );
    }

    @Override
    public void onMarkerClick( int position ) {
        binding.recyclerView.smoothScrollToPosition( position );
        binding.mapOverlayLayout.showMarker( position );
    }

    @Override
    public void onMapClicked() {
        if ( !isDetailsLayoutHidden ) {
            presenter.onBackPressedWithScene();
        } else {
            if ( isRecyclerHidden ) {
                translateNormalRecycler( binding.recyclerView );
                isRecyclerHidden = false;
            } else {
                translateLowRecycler( binding.recyclerView );
                isRecyclerHidden = true;

            }
        }
    }

    private ScaleDownImageTransition getScaleDownImageTransition( Context context ) {
        ScaleDownImageTransition transition =
                new ScaleDownImageTransition( context, MapBitmapCache.instance().getBitmap() );
        transition.addTarget( MAP_TRANSITION_PLACEHOLDER );
        transition.setDuration( 600 );
        return transition;
    }

}
