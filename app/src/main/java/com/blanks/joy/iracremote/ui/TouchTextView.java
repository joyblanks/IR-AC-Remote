package com.blanks.joy.iracremote.ui;

/**
 * Created by Joy on 24/08/15.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.RemoteViews.RemoteView;
@RemoteView
public class TouchTextView extends TextView {
    private GestureDetector mGestureDetector = null;
    public TouchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new LearnGestureListener());
    }


    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
    class LearnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            Log.d("DEBUG","onSingleTapUp");
            return true;
        }
        @Override
        public void onShowPress(MotionEvent ev) {
            Log.d("DEBUG","onShowPress");
        }
        @Override
        public void onLongPress(MotionEvent ev) {
            Log.d("DEBUG","onLongPress");
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.d("DEBUG","onScroll");
            return true;
        }
        @Override
        public boolean onDown(MotionEvent ev) {
            Log.d("DEBUG","onDownd");
            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            Log.d("DEBUG","onFling");
            return true;
        }
        public boolean onDoubleTap(MotionEvent event){
            Log.d("DEBUG","onDoubleTap");
            return true;
        }
    }

}