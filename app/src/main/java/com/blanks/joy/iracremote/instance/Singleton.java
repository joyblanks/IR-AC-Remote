package com.blanks.joy.iracremote.instance;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;

import com.blanks.joy.iracremote.R;
import com.blanks.joy.iracremote.devices.TransmissionList;
/**
 * Created by Joy on 24/01/15.
 */


/*
//images
* http://android-ui-utils.googlecode.com/hg/asset-studio/dist/index.html
* Padding - 1 : 0
* Size : 70
* padding-2 : 8
* 
*
*/
public class Singleton extends Application {
    static final boolean SET_DEBUG 		= false;
    private final static String LOG_TAG = "Singleton";
    private static Singleton m_Instance;
    // Appscreen metrics
    public float m_fFrameS 				= 0;
    public int m_nFrameW 				= 0;
    public int m_nFrameH 				= 0;
    public int m_nTotalW 				= 0;
    public int m_nTotalH 				= 0;
    public int m_nPaddingX 				= 0;
    public int m_nPaddingY 				= 0;

    public boolean swing 				= true;
    public boolean power 				= false;
    public int temp 					= 16;
    public int tempAngle 				= 220;
    public int sequence 				= 1;
    public TransmissionList irCodesAll 	= null;
    public int fan						= 0;
    public int mode						= 0;



    public Singleton() {
        super();
        //goDoHex();
        irCodesAll 						= new TransmissionList();
        m_Instance 						= this;
    }

    public static Singleton getInstance() {

    if (m_Instance == null) {
         synchronized (Singleton.class) {
            if (m_Instance == null)
                new Singleton();
            }
         }
         return m_Instance;
    }

    public static void Debug(String tag, String message) {
        if (SET_DEBUG) {
            Log.d(tag, message);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // must be called in every oncreate
    public void InitGUIFrame(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        m_nTotalW 						= dm.widthPixels;
        m_nTotalH 						= dm.heightPixels;
        m_fFrameS 						= (float) m_nTotalW / 750.0f;// scale factor
        m_nFrameW 						= m_nTotalW;// compute our frame
        m_nFrameH 						= (int) (960.0f * m_fFrameS);// compute our frame
        m_nPaddingY 					= 0;// compute padding for our frame inside the total screen size
        m_nPaddingX 					= (m_nTotalW - m_nFrameW) / 2;// compute padding for our frame inside the total screen size
        Debug(LOG_TAG, "InitGUIFrame: frame:" + m_nFrameW + "x" + m_nFrameH + " Scale:" + m_fFrameS);
    }

    public int Px2DIP(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) ((float) value * scale);
    }

    // 9699758

    public int Scale(int v) {
        float s = (float) v * m_fFrameS;
        int rs =(s - (int) s >= 0.5) ?((int) s) + 1 : (int) s;
        return rs;
    }

    public Bitmap getScaledBitmap(Context context, float scalex, float scaley, int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        Matrix matrix = new Matrix();
        matrix.postScale(scalex, scaley);
        matrix.postRotate(0);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);
    }

    public Drawable getScaledDrawable(Context context, float scalex,float scaley, int id) {
        return new BitmapDrawable(context.getResources(), getScaledBitmap(context, scalex, scaley, id));
    }

    public int GetPercent(int value, int percent) {
    return percent * value / 100;
    }

    public int getMediumTextSize() {
    return Scale(10);
    }

    public TransmissionList getIrCodesAll() {
        return irCodesAll;
    }

    public void setIrCodesAll(TransmissionList irCodesAll) {
    this.irCodesAll = irCodesAll;
    }


    public int getFan(){
        switch(this.fan){
            case 0://auto
                return(R.drawable.fan_auto);

            case 1://low
                return(R.drawable.fan_low);

            case 2://med
              return(R.drawable.fan_medium);

            case 3://high
                return(R.drawable.fan_high);

        }
        return R.drawable.fan_auto;
    }


    public int getMode(){
        switch(this.mode){
            case 0://auto
                return(R.drawable.mode_a);

            case 1://cool
                return(R.drawable.mode_c);

            case 2://dry
                return(R.drawable.mode_d);

            case 3://fan
                return(R.drawable.mode_f);

            case 4://heat
                return(R.drawable.mode_h);
        }
        return R.drawable.mode_a;
    }

    public int getSwing(){

        return this.swing ? R.drawable.swingon : R.drawable.swingoff;
    }



}
