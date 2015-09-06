package com.blanks.joy.iracremote.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ProgressBar;

public class VerticalProgressBar extends ProgressBar {
    private int x, y, z, w;
    private static final String TAG = "JoyIR";

    @Override
    protected void drawableStateChanged() {
        // TODO Auto-generated method stub
        super.drawableStateChanged();
    }

    public VerticalProgressBar(Context context) {
        super(context);
    }

    public VerticalProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d(TAG,"sizechanged");
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
        Log.d(TAG,"measure");
    }

    protected void onDraw(Canvas c) {
        super.onDraw(c);
        Log.d(TAG,"draw");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG,"touch");
        return super.onTouchEvent(event);

    }

    @Override
    public synchronized void setProgress(int progress) {
        Log.d(TAG,"progress");
        super.setProgress(progress);


    }
}