package com.droidsonroids.workcation.screens.main.map.transition;

import android.content.Context;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.View;

import com.droidsonroids.workcation.R;
import com.droidsonroids.workcation.common.transitions.TextResizeTransition;
import com.droidsonroids.workcation.common.transitions.TransitionBuilder;
import com.droidsonroids.workcation.screens.main.map.DetailsCoordinatorLayout;

public class HideDetailsTransitionSet extends TransitionSet {
    private static final String TITLE_TEXT_VIEW_TRANSITION_NAME = "titleTextView";
    private static final String CARD_VIEW_TRANSITION_NAME = "cardView";
    private final String transitionName;
    private final View from;
    private final DetailsCoordinatorLayout to;
    private final Context context;

    public HideDetailsTransitionSet( final Context ctx, final String transitionName, final View from, final DetailsCoordinatorLayout to ) {
        context = ctx;
        this.transitionName = transitionName;
        this.from = from;
        this.to = to;
        addTransition( textResize() );
        addTransition( shared() );
    }

    private String titleTransitionName() {
        return transitionName + TITLE_TEXT_VIEW_TRANSITION_NAME;
    }

    private String cardViewTransitionName() {
        return transitionName + CARD_VIEW_TRANSITION_NAME;
    }

    private Transition textResize() {
        return new TransitionBuilder( new TextResizeTransition() )
                .link( from.findViewById( R.id.title ), to.binding.title, titleTransitionName() )
                .build();
    }

    private Transition shared() {
        return new TransitionBuilder( TransitionInflater.from( context ).inflateTransition( android.R.transition.move ) )
                .link( from.findViewById( R.id.headerImage ), to.binding.headerImage, transitionName )
                .link( from, to.binding.baliDetailsContainer, cardViewTransitionName() )
                .build();
    }
}