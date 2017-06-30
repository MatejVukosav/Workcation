package com.droidsonroids.workcation.common.maps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.droidsonroids.workcation.R;

/**
 * View that make circle with number for card numeration in location fragment
 * Created by Vuki on 29.6.2017..
 */

public class CircleView extends View {

    private static final int STROKE_DIMEN = 3;
    private static final int STROKE_COLOR = android.R.color.black;
    private static final int TEXT_COLOR = android.R.color.black;
    private static final int BACKGROUND_COLOR = android.R.color.white;
    private Context context;
    private Paint backgroundPaint;
    private Paint strokeBackgroundPaint;
    private Paint textPaint;
    private String text;
    private float size;
    private Animation animationScaleLarge;
    private Animation animationScaleSmall;

    public CircleView( Context context ) {
        super( context );
        this.context = context;
        init();
    }

    public CircleView( Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
        this.context = context;
        init();
    }

    public CircleView( Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        this.context = context;
        init();
    }

    @SuppressWarnings("unused")
    public CircleView( Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes ) {
        super( context, attrs, defStyleAttr, defStyleRes );
        this.context = context;
        init();
    }

    private void init() {
        setupBackgroundColor();
        setupStrokeBackgroundColor();
        setupTextPaint();
        setupScaleAnimation();
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
        int parentWidth = MeasureSpec.getSize( widthMeasureSpec );
        int parentHeight = MeasureSpec.getSize( heightMeasureSpec );
        this.setMeasuredDimension( parentWidth, parentHeight );
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );

        size = parentHeight / 2;
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        drawBackground( canvas );
        drawStrokeBackground( canvas );
        drawText( canvas );
        super.onDraw( canvas );
    }

    private void drawText( Canvas canvas ) {
        float y = size - ( textPaint.descent() + textPaint.ascent() ) / 2;
        canvas.drawText( text, size, y, textPaint );
    }

    private void drawStrokeBackground( Canvas canvas ) {
        canvas.drawCircle( size, size, size - STROKE_DIMEN / 2.f, strokeBackgroundPaint );
    }

    private void drawBackground( Canvas canvas ) {
        canvas.drawCircle( size, size, size, backgroundPaint );
    }

    private void setupStrokeBackgroundColor() {
        strokeBackgroundPaint = new Paint();
        strokeBackgroundPaint.setColor( ContextCompat.getColor( context, STROKE_COLOR ) );
        strokeBackgroundPaint.setStyle( Paint.Style.STROKE );
        strokeBackgroundPaint.setAntiAlias( true );
        strokeBackgroundPaint.setStrokeWidth( STROKE_DIMEN );
    }

    private void setupBackgroundColor() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor( ContextCompat.getColor( context, BACKGROUND_COLOR ) );
        backgroundPaint.setAntiAlias( true );
    }

    private void setupTextPaint() {
        textPaint = new Paint();
        textPaint.setColor( ContextCompat.getColor( context, TEXT_COLOR ) );
        textPaint.setTextAlign( Paint.Align.CENTER );
        textPaint.setTypeface( Typeface.DEFAULT_BOLD );
        textPaint.setTextSize( context.getResources().getDimensionPixelSize( R.dimen.textsize_medium ) );
    }

    private void setupScaleAnimation() {
        animationScaleLarge = AnimationUtils.loadAnimation( context, R.anim.strech );
        animationScaleLarge.setDuration( 500 );
        animationScaleSmall = AnimationUtils.loadAnimation( context, R.anim.shrink );
        animationScaleSmall.setDuration( 500 );
    }

    @SuppressWarnings("unused")
    public void shrink() {
        startAnimation( animationScaleSmall );
    }

    @SuppressWarnings("unused")
    public void strech() {
        startAnimation( animationScaleLarge );
    }

    public void setNumber( int number ) {
        this.text = String.valueOf( number );
        invalidate();
    }
}
