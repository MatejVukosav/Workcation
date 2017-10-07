package com.droidsonroids.workcation.screens.main.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.droidsonroids.workcation.common.model.Duration;
import com.droidsonroids.workcation.common.model.Place;
import com.droidsonroids.workcation.databinding.ModelItemPlaceDetailsBinding;
import com.droidsonroids.workcation.screens.main.map.transition.HideDetailsTransitionSet;
import com.droidsonroids.workcation.screens.main.map.transition.ShowDetailsTransitionSet;

public class DetailsCoordinatorLayout extends CoordinatorLayout {

    public ModelItemPlaceDetailsBinding binding;

    public DetailsCoordinatorLayout( final Context context ) {
        this( context, null );
        init();
    }

    private void init() {
    }

    public DetailsCoordinatorLayout( final Context context, final AttributeSet attrs ) {
        super( context, attrs );
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        binding = ModelItemPlaceDetailsBinding.bind( this );
    }

    private void setData( Place place ) {
        binding.headerImage.setImageDrawable( ContextCompat
                .getDrawable( getContext(), getResources()
                        .getIdentifier( place.getPhotoList().get( 0 ), "drawable", getContext().getPackageName() ) ) );
        binding.title.setText( place.getName() );
        binding.description.setText( place.getDescription() );
        binding.hours.setText( place.getOpeningHours() );
        binding.priceTitle.setText( String.valueOf( place.getPrice() ) );

        binding.takeMe.setOnClickListener( v -> {
            // Create a Uri from an intent string. Use the result to create an Intent.
            Uri gmmIntentUri = Uri.parse( "google.navigation:q=" + place.getLat() + "," + place.getLng() + "" );
            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent( Intent.ACTION_VIEW, gmmIntentUri );
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage( "com.google.android.apps.maps" );

            // Attempt to start an activity that can handle the Intent
            if ( mapIntent.resolveActivity( getContext().getPackageManager() ) != null ) {
                getContext().startActivity( mapIntent );
            }

        } );
    }

    public void showScene( final ViewGroup container, final View sharedView, final String transitionName, final Place data ) {
        setData( data );
        TransitionSet set = new ShowDetailsTransitionSet( getContext(), transitionName, sharedView, this );
        Scene scene = new Scene( container, (View) this );
        TransitionManager.go( scene, set );
    }

    public void hideScene( final ViewGroup container, final View sharedView, final String transitionName ) {
        TransitionSet set = new HideDetailsTransitionSet( getContext(), transitionName, sharedView, this );
        Scene scene = new Scene( container, (View) this );
        TransitionManager.go( scene, set );
    }

    public void setDurationText( Activity activity, Duration duration ) {
        activity.runOnUiThread( () -> binding.time.setText( duration.getText() ) );
    }
}
