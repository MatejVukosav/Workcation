package com.droidsonroids.workcation.screens.main.map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidsonroids.workcation.R;
import com.droidsonroids.workcation.common.maps.CircleView;
import com.droidsonroids.workcation.common.model.Place;
import com.droidsonroids.workcation.common.transitions.TransitionUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class BaliPlacesAdapter extends RecyclerView.Adapter<BaliPlacesAdapter.BaliViewHolder> {

    private final OnPlaceClickListener listener;
    private Context context;
    private List<Place> placeList = new ArrayList<>();

    BaliPlacesAdapter( OnPlaceClickListener listener, Context context ) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    public BaliViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType ) {
        return new BaliViewHolder( LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_bali_place, parent, false ) );
    }

    @Override
    public void onBindViewHolder( final BaliViewHolder holder, final int position ) {
        holder.title.setText( placeList.get( position ).getName() );
        holder.openingHours.setText( placeList.get( position ).getOpeningHours() );
        holder.price.setText( String.valueOf( placeList.get( position ).getPrice() ) );

        Drawable drawable = ContextCompat.getDrawable( context, context.getResources().getIdentifier( placeList.get( position ).getPhotoList().get( 0 ), "drawable", context.getPackageName() ) );
        holder.placePhoto.setImageDrawable( drawable );
        holder.root.setOnClickListener( view -> listener.onPlaceClicked( holder.root, TransitionUtils.getRecyclerViewTransitionName( position ), position ) );
        holder.number.setNumber( position );

        holder.number.setOnClickListener( v -> {
            listener.onNumberClicked( position );
        } );
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    void setPlacesList( List<Place> placesList ) {
        placeList = placesList;
        for( int i = 0; i < placeList.size(); i++ ) {
            notifyItemInserted( i );
        }
    }

    interface OnPlaceClickListener {
        void onPlaceClicked( View sharedView, String transitionName, final int position );

        void onNumberClicked( int position );
    }

    static class BaliViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.opening_hours)
        TextView openingHours;
        @BindView(R.id.root)
        CardView root;
        @BindView(R.id.headerImage)
        ImageView placePhoto;
        @BindView(R.id.number)
        CircleView number;

        BaliViewHolder( final View itemView ) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }
    }
}
