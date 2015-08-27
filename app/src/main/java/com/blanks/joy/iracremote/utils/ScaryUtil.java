package com.blanks.joy.iracremote.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.blanks.joy.iracremote.R;
import com.blanks.joy.iracremote.constants.Constants;
import com.blanks.joy.iracremote.devices.IRdata;
import com.blanks.joy.iracremote.devices.TransmissionCode;
import com.blanks.joy.iracremote.htc.ConsumerIrManagerCompat;
import com.blanks.joy.iracremote.instance.Singleton;


/**
 * Created by Joy on 24/08/15.
 *
 * ScaryUtil
 *  - contains all the utilities and methods
 */
public class ScaryUtil {
    private static final String TAG = "JoyIR";

    public static Bitmap buildUpdate(Context context, String text){
        Bitmap myBitmap = Bitmap.createBitmap(160, 84, Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface font = Typeface.createFromAsset(context.getAssets(),"fonts/Digital.otf");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(font);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(65);
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(text, 80, 60, paint);
        return myBitmap;
    }

    public static Bitmap buildSequence(Context context, String text){
        Bitmap myBitmap = Bitmap.createBitmap(160, 84, Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        //Typeface font = Typeface.createFromAsset(context.getAssets(),"fonts/7LED.ttf");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        //paint.setTypeface(font);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setTextSize(65);
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(text, 80, 60, paint);
        return myBitmap;
    }

    //for Remote Edge activities.
    public static void power(Context context, Singleton inst,  RemoteViews rv){
        Vibrator vibrator       = (Vibrator) context.getSystemService(android.content.Context.VIBRATOR_SERVICE);
        try {

            long[] pattern = {0 , 150, 50, 200};

            inst.power = !inst.power;
            TransmissionCode data = getIRCode(inst.sequence, !inst.power ? Constants.power + 1 : Constants.power);
            rv.setImageViewResource(R.id.swing, !inst.power ? R.drawable.swingoff : (inst.swing ? R.drawable.swingon : R.drawable.swingoff));
            rv.setInt(R.id.edge,"setBackgroundResource", !inst.power ? R.drawable.remote_off : R.drawable.remote_on);
            rv.setImageViewBitmap(R.id.temp, ScaryUtil.buildUpdate(context, !inst.power ? "--" : String.valueOf(inst.temp)));

            ScaryUtil.transmit(context, data);
            vibrator.vibrate(pattern, -1);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    public static void swing(Context context, Singleton inst,  RemoteViews rv){
        if (!inst.power) {
            return;
        }

        inst.swing = !inst.swing;
        TransmissionCode data = getIRCode(inst.sequence, inst.swing ? Constants.swing + 1 : Constants.swing);
        rv.setImageViewResource(R.id.swing, (inst.swing ? R.drawable.swingon : R.drawable.swingoff));

        ScaryUtil.transmit(context, data);
    }

    public static void fan(Context context, Singleton inst,  RemoteViews rv){
        if (!inst.power) {
            return;
        }
        inst.fan  =  (inst.fan == 3) ?  0 :  inst.fan+1;
        TransmissionCode data = getIRCode(inst.sequence, Constants.fan + inst.fan);

        rv.setImageViewResource(R.id.fan, inst.getFan());
        ScaryUtil.transmit(context, data);
    }


    public static void mode(Context context, Singleton inst,  RemoteViews rv){
        if (!inst.power) {
            return;
        }


        inst.mode  =  (inst.mode == 4) ?  0 : inst.mode+1;
        TransmissionCode data = getIRCode(inst.sequence, Constants.mode + inst.mode);

        rv.setImageViewResource(R.id.mode, inst.getMode());
        ScaryUtil.transmit(context, data);
    }


    // volume change call
    public static void changeTemp(int p, int a,Singleton m_Inst, TextView tempView) {
        int temp = p * 15 / 100 + 16;
        //TextView tempView = ((TextView) findViewById(R.id.temp));

        if (m_Inst.power) {
            tempView.setText(String.valueOf(temp));
            m_Inst.temp = temp;
            m_Inst.tempAngle = a;

        } else
            tempView.setText("--");
    }

    //get service from context
    public static ConsumerIrManager getConsumerIRService(Context context){
        ConsumerIrManager consumerIrManager  = (ConsumerIrManager) context.getSystemService(android.content.Context.CONSUMER_IR_SERVICE);
        return consumerIrManager;
    }

    public static ConsumerIrManagerCompat getConsumerIrManagerCompat(Context context){
        ConsumerIrManagerCompat mCIR = ConsumerIrManagerCompat.createInstance(context);
        mCIR.start(); //for HTC - noop otherwise (also see onResume()/onPause() )
        return mCIR;
    }

    public static void transmit(Context context, TransmissionCode data){
        if (Build.MANUFACTURER.equalsIgnoreCase("HTC")) {
            //TargetApi(19)
            ScaryUtil.getConsumerIrManagerCompat(context).transmit(data.getFrequency(),data.getTransmissionPulses());
        } else {
            //TargetApi(21+)
            ScaryUtil.getConsumerIRService(context).transmit(data.getFrequency(),data.getTransmission());
        }

    }
    //get Transmission codes
    public static TransmissionCode getIRCode(int sequence, int what){
        IRdata ird = new IRdata();
        SparseArray<TransmissionCode> transmitCodes = ird.initTransmitCodes(sequence);
        TransmissionCode code = transmitCodes.get(what);
        return code;
    }
}
