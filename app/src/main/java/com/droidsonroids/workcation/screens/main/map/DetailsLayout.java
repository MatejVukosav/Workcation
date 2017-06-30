package com.droidsonroids.workcation.screens.main.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidsonroids.workcation.R;
import com.droidsonroids.workcation.common.model.Duration;
import com.droidsonroids.workcation.common.model.Place;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsLayout extends CoordinatorLayout {

    @BindView(R.id.cardview)
    CardView cardViewContainer;
    @BindView(R.id.headerImage)
    ImageView imageViewPlaceDetails;
    @BindView(R.id.title)
    TextView textViewTitle;
    @BindView(R.id.description)
    TextView textViewDescription;
    @BindView(R.id.price_title)
    TextView price;
    @BindView(R.id.hours)
    TextView hours;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.takeMe)
    Button takeMeBtn;



    public DetailsLayout( final Context context ) {
        this( context, null );
    }

    public DetailsLayout( final Context context, final AttributeSet attrs ) {
        super( context, attrs );
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind( this );
    }

    private void setData( Place place ) {
        imageViewPlaceDetails.setImageDrawable( ContextCompat
                .getDrawable( getContext(), getResources()
                        .getIdentifier( place.getPhotoList().get( 0 ), "drawable", getContext().getPackageName() ) ) );
        textViewTitle.setText( place.getName() );
        textViewDescription.setText( place.getDescription() );
        hours.setText( place.getOpeningHours() );
        price.setText( String.valueOf( place.getPrice() ) );

        takeMeBtn.setOnClickListener( v -> {

            // Create a Uri from an intent string. Use the result to create an Intent.
            Uri gmmIntentUri = Uri.parse( "google.navigation:q=" + place.getLat() + "," + place.getLng() + "" );
            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent( Intent.ACTION_VIEW, gmmIntentUri );
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage( "com.google.android.apps.maps" );

            // Attempt to start an activity that can handle the Intent
            if( mapIntent.resolveActivity( getContext().getPackageManager() ) != null ) {
                getContext().startActivity( mapIntent );
            }

        } );

    }

    public static DetailsLayout showScene( Activity activity, final ViewGroup container, final View sharedView, final String transitionName, final Place data ) {

        DetailsLayout detailsLayout = (DetailsLayout) activity
                .getLayoutInflater()
                .inflate( R.layout.item_place, container, false );
        detailsLayout.setData( data );

        TransitionSet set = new ShowDetailsTransitionSet( activity, transitionName, sharedView, detailsLayout );
        Scene scene = new Scene( container, (View) detailsLayout );
        TransitionManager.go( scene, set );

        return detailsLayout;
    }

    public static DetailsLayout hideScene( Activity activity, final ViewGroup container, final View sharedView, final String transitionName ) {
        DetailsLayout detailsLayout = (DetailsLayout) container.findViewById( R.id.bali_details_container );
        TransitionSet set = new HideDetailsTransitionSet( activity, transitionName, sharedView, detailsLayout );
        Scene scene = new Scene( container, (View) detailsLayout );
        TransitionManager.go( scene, set );
        return detailsLayout;
    }

    public static void setDurationText( Activity activity, final ViewGroup container, Duration duration ) {
        DetailsLayout detailsLayout = (DetailsLayout) container.findViewById( R.id.bali_details_container );
        activity.runOnUiThread( () -> detailsLayout.time.setText( duration.getText() ) );
    }
}
