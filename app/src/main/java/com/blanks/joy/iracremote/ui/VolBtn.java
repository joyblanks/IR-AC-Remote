package com.blanks.joy.iracremote.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.blanks.joy.iracremote.interfaces.VolButtonListener;

@SuppressLint("ViewConstructor")
public class VolBtn extends RelativeLayout implements OnGestureListener {
	
	private GestureDetector gestureDetector;
	private ImageView ivRotor;
	private Bitmap bmpRotorOn, bmpRotorOff;
	private boolean mState = false;

	private int m_nWidth = 0, m_nHeight = 0;

	// private String TAG = "joyIR";

	

	public boolean ismState() {
		return mState;
	}

	private VolButtonListener m_listener;

	public void SetListener(VolButtonListener l) {
		m_listener = l;
	}

	public void SetState(boolean state) {
		mState = state;
		ivRotor.setImageBitmap(state ? bmpRotorOn : bmpRotorOff);
	}

	public VolBtn(Context context, int rotoron, int rotoroff, final int w, final int h) {
		super(context);
		m_nWidth 			= w;
		m_nHeight 			= h;
		Bitmap srcon 		= BitmapFactory.decodeResource(context.getResources(),rotoron);
		Bitmap srcoff 		= BitmapFactory.decodeResource(context.getResources(),rotoroff);
		float scaleWidth 	= ((float) w) / srcon.getWidth();
		float scaleHeight 	= ((float) h) / srcon.getHeight();
		Matrix matrix 		= new Matrix();
		ivRotor 			= new ImageView(context);
		
		matrix.postScale(scaleWidth, scaleHeight);

		bmpRotorOn 			= Bitmap.createBitmap(srcon, 0, 0, srcon.getWidth(),srcon.getHeight(), matrix, true);
		bmpRotorOff 		= Bitmap.createBitmap(srcoff, 0, 0, srcoff.getWidth(),srcoff.getHeight(), matrix, true);
		
		
		ivRotor.setImageBitmap(bmpRotorOn);
		LayoutParams lp_ivKnob = new LayoutParams(w, h);// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp_ivKnob.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(ivRotor, lp_ivKnob);
		SetState(mState);
		// enable gesture detector
		gestureDetector = new GestureDetector(getContext(), this);

	}

	
	private float cartesianToPolar(float x, float y) {
		return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//On touch up send IR nor like every angle rotae USE interface methods
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (m_listener != null)
				m_listener.onTriggerChange();
		}
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		} else
			return super.onTouchEvent(event);

	}

	

	public void setRotorPosAngle(float deg) {

		if (deg >= 210 || deg <= 150) {
			if (deg > 180)
				deg = deg - 360;
			Matrix matrix = new Matrix();
			ivRotor.setScaleType(ScaleType.MATRIX);
			matrix.postRotate((float) deg, m_nWidth / 2, m_nHeight / 2);// getWidth()/2, getHeight()/2);
			ivRotor.setImageMatrix(matrix);
		}
	}

	public void setRotorPercentage(int percentage) {
		int posDegree = percentage * 3 - 150;
		if (posDegree < 0)
			posDegree = 360 + posDegree;
		setRotorPosAngle(posDegree);
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		if (!mState) {
			return false;
		}

		float x = e2.getX() / ((float) getWidth());
		float y = e2.getY() / ((float) getHeight());
		float rotDegrees = cartesianToPolar(1 - x, 1 - y);// 1- to correct our  custom axis direction
		if (!Float.isNaN(rotDegrees)) {
			// instead of getting 0-> 180, -180 0 , we go for 0 -> 360
			float posDegrees = rotDegrees;
			if (rotDegrees < 0)
				posDegrees = 360 + rotDegrees;

			// deny full rotation, start start and stop point, and get a linear
			// scale
			if (posDegrees > 210 || posDegrees < 150) {
				setRotorPosAngle(posDegrees);
				float scaleDegrees = rotDegrees + 150; // given the current  parameters, we go  from 0 to 300
				int percent = (int) (scaleDegrees / 3);
				if (m_listener != null)
					m_listener.onRotate(percent, (int) posDegrees);
				return true; // consumed
			} else
				return false;
		} else
			return false; // not consumed
	}

	
	//These methods should be here not implemented. Dont know why??
	public void onShowPress(MotionEvent e) {}

	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,float arg3) {return false;}

	public void onLongPress(MotionEvent e) {}
	
	public boolean onDown(MotionEvent event) {return true;}

	public boolean onSingleTapUp(MotionEvent e) {return true;}

}
