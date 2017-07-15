package com.droidsonroids.workcation.screens.main.map;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.droidsonroids.workcation.common.views.ViewHelper;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Vuki on 30.6.2017..
 */

public class MySupportMapFragment extends SupportMapFragment implements MapInterface {

    private View originalContentView;
    private TouchableWrapper touchView;
    private MapInterface listener;


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        originalContentView = super.onCreateView( inflater, container, savedInstanceState );
        touchView = new TouchableWrapper( getActivity(), this );
        touchView.addView( originalContentView );
        return touchView;
    }

    @Override
    public void onMapClicked() {
        if ( listener != null ) {
            listener.onMapClicked();
        }
    }
    public void setListener( MapInterface listener ) {
        this.listener = listener;
    }

    @Override
    public View getView() {
        return originalContentView;
    }



    public static class TouchableWrapper extends FrameLayout {

        public MapInterface listener;

        public TouchableWrapper( Context context, MapInterface listener ) {
            super( context );
            this.listener = listener;
        }

        public TouchableWrapper( @NonNull Context context ) {
            super( context );
        }

        public TouchableWrapper( @NonNull Context context, @Nullable AttributeSet attrs ) {
            super( context, attrs );
        }

        public TouchableWrapper( @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr ) {
            super( context, attrs, defStyleAttr );
        }

        public TouchableWrapper( @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes ) {
            super( context, attrs, defStyleAttr, defStyleRes );
        }

        @Override
        public boolean dispatchTouchEvent( MotionEvent event ) {

            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN: {
                    pressStartTime = System.currentTimeMillis();
                    pressedX = event.getX();
                    pressedY = event.getY();
                    stayedWithinClickDistance = true;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if ( stayedWithinClickDistance && distance( pressedX, pressedY, event.getX(), event.getY() ) > MAX_CLICK_DISTANCE ) {
                        stayedWithinClickDistance = false;
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    long pressDuration = System.currentTimeMillis() - pressStartTime;
                    if ( pressDuration < MAX_CLICK_DURATION && stayedWithinClickDistance ) {
                        // Click event has occurred
                        if ( listener != null ) {
                            listener.onMapClicked();
                        }
                    }
                }
            }
            return super.dispatchTouchEvent( event );
        }

        /**
         * Max allowed duration for a "click", in milliseconds.
         */
        private static final int MAX_CLICK_DURATION = 1000;

        /**
         * Max allowed distance to move during a "click", in DP.
         */
        private static final int MAX_CLICK_DISTANCE = 15;

        private long pressStartTime;
        private float pressedX;
        private float pressedY;
        private boolean stayedWithinClickDistance;

        public float distance( float x1, float y1, float x2, float y2 ) {
            float dx = x1 - x2;
            float dy = y1 - y2;
            float distanceInPx = (float) Math.sqrt( dx * dx + dy * dy );
            return ViewHelper.pxToDp( distanceInPx, getResources() );
        }

    }
}