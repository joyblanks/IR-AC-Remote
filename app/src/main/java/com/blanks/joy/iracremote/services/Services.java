package com.blanks.joy.iracremote.services;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.blanks.joy.iracremote.R;
import com.blanks.joy.iracremote.constants.Constants;
import com.blanks.joy.iracremote.devices.TransmissionCode;
import com.blanks.joy.iracremote.instance.Singleton;
import com.blanks.joy.iracremote.utils.ScaryUtil;

/**
 * Created by Joy on 23/08/15.
 */
public class Services {
    private static final String TAG = "JoyIR";

    public static void power(Context context, Singleton inst,  RemoteViews rv){
        ConsumerIrManager cirm  = (ConsumerIrManager) context.getSystemService(android.content.Context.CONSUMER_IR_SERVICE);
        Vibrator vibrator       = (Vibrator) context.getSystemService(android.content.Context.VIBRATOR_SERVICE);
        boolean isPoweredOn = inst.power;
        try {

            long[] pattern = {0 , 150, 50, 200};

            inst.power = !inst.power;
            TransmissionCode data = inst.getIrCodesAll().get(inst.sequence, !inst.power ? Constants.power + 1 : Constants.power);
            rv.setImageViewResource(R.id.swing, !inst.power ? R.drawable.swingoff : (inst.swing ? R.drawable.swingon : R.drawable.swingoff));
            rv.setInt(R.id.edge,"setBackgroundResource", !inst.power ? R.drawable.remote_off : R.drawable.remote_on);
            rv.setImageViewBitmap(R.id.temp, ScaryUtil.buildUpdate(context, !inst.power ? "--" : String.valueOf(inst.temp)));

            cirm.transmit(data.getFrequency(), data.getTransmission());
            vibrator.vibrate(pattern, -1);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    public static void swing(Context context, Singleton inst,  RemoteViews rv){
        if (!inst.power) {
            return;
        }
        ConsumerIrManager cirm  = (ConsumerIrManager) context.getSystemService(android.content.Context.CONSUMER_IR_SERVICE);

        inst.swing = !inst.swing;
        TransmissionCode data = inst.getIrCodesAll().get(inst.sequence, inst.swing ? Constants.swing + 1 : Constants.swing);
        rv.setImageViewResource(R.id.swing, (inst.swing ? R.drawable.swingon : R.drawable.swingoff));

        cirm.transmit(data.getFrequency(), data.getTransmission());
    }

    public static void fan(Context context, Singleton inst,  RemoteViews rv){
        if (!inst.power) {
            return;
        }
        ConsumerIrManager cirm  = (ConsumerIrManager) context.getSystemService(android.content.Context.CONSUMER_IR_SERVICE);

        inst.fan  =  (inst.fan == 3) ?  0 :  inst.fan+1;
        TransmissionCode data = inst.getIrCodesAll().get(inst.sequence, Constants.fan + inst.fan);

        rv.setImageViewResource(R.id.fan, inst.getFan());
        cirm.transmit(data.getFrequency(), data.getTransmission());
    }


    public static void mode(Context context, Singleton inst,  RemoteViews rv){
        if (!inst.power) {
            return;
        }
        ConsumerIrManager cirm  = (ConsumerIrManager) context.getSystemService(android.content.Context.CONSUMER_IR_SERVICE);

        inst.mode  =  (inst.mode == 4) ?  0 : inst.mode+1;
        TransmissionCode data = inst.getIrCodesAll().get(inst.sequence, Constants.mode + inst.mode);

        rv.setImageViewResource(R.id.mode, inst.getMode());
        cirm.transmit(data.getFrequency(), data.getTransmission());
    }


}
