package com.blanks.joy.iracremote.services;

import android.hardware.ConsumerIrManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.blanks.joy.iracremote.R;
import com.blanks.joy.iracremote.constants.Constants;
import com.blanks.joy.iracremote.devices.TransmissionCode;
import com.blanks.joy.iracremote.instance.Singleton;

/**
 * Created by Joy on 23/08/15.
 */
public class Services {
    private static final String TAG = "JoyIR";
    public static void power(Singleton inst, ConsumerIrManager cirm, RemoteViews rv){
        boolean isPoweredOn = inst.power;
        try {
            TransmissionCode data;
            //TextView tv = ((TextView) findViewById(R.id.temp));

            if (isPoweredOn) {
                data = inst.getIrCodesAll().get(inst.sequence, Constants.power + 1);
                inst.power = false;
                //tv.setText("--");
                rv.setInt(R.id.edge,"setBackgroundResource",R.drawable.remote_off);
            } else {
                // poweron
                data = inst.getIrCodesAll().get(inst.sequence, Constants.power);
                //tv.setText("" + inst.temp);
                inst.power = true;
                rv.setImageViewResource(R.id.swing, (inst.swing ? R.drawable.swingon : R.drawable.swingoff));
                rv.setInt(R.id.edge,"setBackgroundResource",R.drawable.remote_on);
            }
            int freq = data.getFrequency();
            int[] c = (data.getTransmission());
            cirm.transmit(freq, c);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    public static void swing(Singleton inst, ConsumerIrManager cirm, RemoteViews rv){
        if (!inst.power) {
            return;
        }
        //ImageView swingView = (ImageView)findViewById(R.id.swing);

        TransmissionCode data;
        if (inst.swing) {
            data = inst.getIrCodesAll().get(inst.sequence, Constants.swing);
            //((TextView) findViewById(R.id.swingtext)).setText("Swing:OFF");
            //swingView.setImageViewResource().setImageResource(R.drawable.swingoff);
        } else {
            data = inst.getIrCodesAll().get(
                    inst.sequence, Constants.swing + 1);
            //((TextView) findViewById(R.id.swingtext)).setText("Swing:ON");
            //swingView.setImageResource(R.drawable.swingon);
        }
        inst.swing = !inst.swing;
        rv.setImageViewResource(R.id.swing, (inst.swing ? R.drawable.swingon : R.drawable.swingoff));

        int freq = data.getFrequency();

        int[] c = (data.getTransmission());
        cirm.transmit(freq, c);
    }

}
