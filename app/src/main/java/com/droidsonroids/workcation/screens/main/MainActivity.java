package com.droidsonroids.workcation.screens.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.droidsonroids.workcation.R;
import com.droidsonroids.workcation.common.maps.MapsUtil;
import com.droidsonroids.workcation.common.mvp.MvpActivity;
import com.droidsonroids.workcation.common.mvp.MvpFragment;
import com.droidsonroids.workcation.databinding.ActivityMainBinding;
import com.droidsonroids.workcation.screens.main.map.DetailsFragment;
import com.droidsonroids.workcation.screens.main.map.GoogleSupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLngBounds;

public class MainActivity extends AppCompatActivity implements MainView, OnMapReadyCallback {

    private LatLngBounds mapLatLngBounds;

    private MainPresenter presenter;
    private ActivityMainBinding binding;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding = DataBindingUtil.setContentView( this, R.layout.activity_main );

        presenter = new MainPresenterImpl();
        presenter.attachView( this );
        presenter.provideMapLatLngBounds();

        GoogleSupportMapFragment mapFragment = new GoogleSupportMapFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace( binding.mapMainFragment.getId(), mapFragment )
                .addToBackStack( DetailsFragment.TAG )
                .commit();
        mapFragment.getMapAsync( this );
    }

    @Override
    public void onBackPressed() {
        if ( getSupportFragmentManager().getBackStackEntryCount() > 1 ) {
            triggerFragmentBackPress( getSupportFragmentManager().getBackStackEntryCount() );
        } else {
            finish();
        }
    }

    @Override
    public void setMapLatLngBounds( final LatLngBounds latLngBounds ) {
        mapLatLngBounds = latLngBounds;
    }

    @Override
    public void onMapReady( final GoogleMap googleMap ) {
        googleMap.moveCamera( CameraUpdateFactory.newLatLngBounds(
                mapLatLngBounds,
                MapsUtil.calculateWidth( getWindowManager() ),
                MapsUtil.calculateHeight( getWindowManager(), getResources().getDimensionPixelSize( R.dimen.map_margin_bottom ) ), 150 ) );
        //save bitmap for later reuse (cause flash while creating map)
        // googleMap.setOnMapLoadedCallback( () -> googleMap.snapshot( presenter::saveBitmap ) );

        //TODO permission
        googleMap.setMyLocationEnabled( true );

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled( true );
        uiSettings.setCompassEnabled( true );
        uiSettings.setMapToolbarEnabled( true );
        uiSettings.setAllGesturesEnabled( true );

        if ( getSupportFragmentManager().findFragmentByTag( DetailsFragment.TAG ) == null ) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( android.R.anim.slide_in_left, android.R.anim.slide_out_right )
                    .replace( binding.containerMain.getId(), DetailsFragment.newInstance( this ), DetailsFragment.TAG )
                    .addToBackStack( DetailsFragment.TAG )
                    .commit();
        }
    }

    private void triggerFragmentBackPress( final int count ) {
        ( (MvpFragment) getSupportFragmentManager()
                .findFragmentByTag( getSupportFragmentManager()
                        .getBackStackEntryAt( count - 1 ).getName() ) )
                .onBackPressed();
    }

    public void superOnBackPressed() {
        super.onBackPressed();
    }
}
