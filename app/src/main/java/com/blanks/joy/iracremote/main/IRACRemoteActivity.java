package com.blanks.joy.iracremote.main;

import android.app.Activity;
import android.graphics.Typeface;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.blanks.joy.iracremote.R;
import com.blanks.joy.iracremote.constants.Constants;
import com.blanks.joy.iracremote.devices.TransmissionCode;
import com.blanks.joy.iracremote.instance.Singleton;
import com.blanks.joy.iracremote.interfaces.VolButtonListener;
import com.blanks.joy.iracremote.ui.VolBtn;
import com.blanks.joy.iracremote.utils.ScaryUtil;

public class IRACRemoteActivity extends Activity {
    private static final String TAG = "JoyIR";

    Singleton m_Inst = Singleton.getInstance();
    VolBtn rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.swing).setOnClickListener(swingSendClickListener);
        findViewById(R.id.power).setOnClickListener(powerSendClickListener);
        findViewById(R.id.sequence).setOnClickListener(seqSendClickListener);
        findViewById(R.id.fan).setOnClickListener(fanSendClickListener);
        findViewById(R.id.mode).setOnClickListener(modeSendClickListener);

        ((TextView) findViewById(R.id.sequence)).setText(String.valueOf(m_Inst.sequence));
        ((ImageView) findViewById(R.id.fan)).setImageResource(m_Inst.getFan());
        ((ImageView) findViewById(R.id.mode)).setImageResource(m_Inst.getMode());
        ((ImageView) findViewById(R.id.swing)).setImageResource(m_Inst.getSwing());

        m_Inst.InitGUIFrame(this);

        RelativeLayout panel = (RelativeLayout) findViewById((R.id.remote));
        setContentView(panel);

        LayoutParams lp;

        rv = new VolBtn(this, R.drawable.rotoron, R.drawable.rotoroff,m_Inst.Scale(250), m_Inst.Scale(250));
        lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        panel.addView(rv, lp);

        rv.setRotorPosAngle(m_Inst.tempAngle);
        rv.setState(m_Inst.power);
        rv.SetListener(new VolButtonListener() {
            // trigger IR transmission for temp control
            public void onTriggerChange() {
                findViewById(R.id.temp).post(new Runnable() {
                    public void run() {
                        ConsumerIrManager mCIR = ScaryUtil.getConsumerIRService(getApplicationContext());
                        if (m_Inst.power) {
                            TransmissionCode data = ScaryUtil.getIRCode(m_Inst.sequence, m_Inst.temp); // (TransmissionCode)samsung.get(m_Inst.temp);
                            ScaryUtil.transmit(getApplicationContext(),data);
                        }
                    }
                });
            }

            public void onRotate(final int percentage, final int angle) {
                findViewById(R.id.temp).post(new Runnable() {
                    public void run() {
                        ScaryUtil.changeTemp(percentage, angle, m_Inst,(TextView)findViewById(R.id.temp));
                    }
                });
            }

        });
        TextView tempCount = ((TextView) findViewById(R.id.temp));
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Digital.otf");
        tempCount.setTypeface(tf);
        tempCount.setText(!m_Inst.power ? "--" : String.valueOf(m_Inst.temp));
    }


    // swing btn
    View.OnClickListener swingSendClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ConsumerIrManager mCIR = ScaryUtil.getConsumerIRService(getApplicationContext());
            if (!mCIR.hasIrEmitter()) {
                Toast.makeText(getApplicationContext(), "No IR Emitter found", Toast.LENGTH_LONG).show();
                Log.e(TAG, "No IR Emitter found\n");
                return;
            }
            if (!m_Inst.power) {
                return;
            }

            m_Inst.swing = !m_Inst.swing;
            TransmissionCode data = ScaryUtil.getIRCode(m_Inst.sequence, m_Inst.swing ? Constants.swing + 1 : Constants.swing);
            Toast.makeText(getApplicationContext(), (m_Inst.swing ? "Swing: ON" : "Swing: OFF"), Toast.LENGTH_SHORT).show();
            ((ImageView)findViewById(R.id.swing)).setImageResource(m_Inst.swing  ? R.drawable.swingon : R.drawable.swingoff);

            ScaryUtil.transmit(getApplicationContext(),data);
        }
    };

    // Power btn
    View.OnClickListener powerSendClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ConsumerIrManager mCIR = ScaryUtil.getConsumerIRService(getApplicationContext());
            if (!mCIR.hasIrEmitter()) {
                Toast.makeText(getApplicationContext(), "No IR Emitter found", Toast.LENGTH_LONG).show();
                Log.e(TAG, "No IR Emitter found");
                return;
            }
            try {
                TransmissionCode data;
                TextView tv = ((TextView) findViewById(R.id.temp));

                if (m_Inst.power) {
                    data = ScaryUtil.getIRCode(m_Inst.sequence, Constants.power + 1);
                    rv.setState(false);
                    m_Inst.power = false;
                    tv.setText("--");

                } else {
                    // poweron
                    data = ScaryUtil.getIRCode(m_Inst.sequence, Constants.power);
                    rv.setState(true);
                    tv.setText(String.valueOf(m_Inst.temp));
                    m_Inst.power = true;
                    ((ImageView) findViewById(R.id.swing)).setImageResource(m_Inst.swing ? R.drawable.swingon : R.drawable.swingoff);
                }
                ScaryUtil.transmit(getApplicationContext(),data);

            }catch (Exception e){
                Log.e(TAG,e.toString());
            }
        }
    };


    // sequence btn
    View.OnClickListener seqSendClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            TextView tvSeq = ((TextView) findViewById(R.id.sequence));
            int seq = m_Inst.sequence;
            seq =  (seq == 3) ? 1 : seq+1;
            tvSeq.setText(String.valueOf(seq));
            m_Inst.sequence = seq;
            Toast.makeText(getApplicationContext(), "AC Transmit sequence: "+seq, Toast.LENGTH_SHORT).show();
        }
    };


    // fan btn
    View.OnClickListener fanSendClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ConsumerIrManager mCIR = ScaryUtil.getConsumerIRService(getApplicationContext());
            if (!mCIR.hasIrEmitter()) {
                Toast.makeText(getApplicationContext(), "No IR Emitter found", Toast.LENGTH_LONG).show();
                Log.e(TAG, "No IR Emitter found");
                return;
            }
            if (!m_Inst.power) {
                return;
            }
            ImageView tv = ((ImageView) findViewById(R.id.fan));
            int seq = m_Inst.fan;
            String mode = "";
            seq =  (seq == 3) ?  0 :  seq+1;
            switch(seq){
                case 0://auto
                    mode = "Auto";
                    tv.setImageResource(R.drawable.fan_auto);
                    break;
                case 1://low
                    mode = "Low";
                    tv.setImageResource(R.drawable.fan_low);
                    break;
                case 2://med
                    mode = "Medium";
                    tv.setImageResource(R.drawable.fan_medium);
                    break;
                case 3://high
                    mode = "High";
                    tv.setImageResource(R.drawable.fan_high);
                    break;
            }
            m_Inst.fan = seq;
            Toast.makeText(getApplicationContext(), "Fan Speed "+mode, Toast.LENGTH_SHORT).show();
            TransmissionCode data = ScaryUtil.getIRCode(m_Inst.sequence, Constants.fan + seq);

            ScaryUtil.transmit(getApplicationContext(),data);
        }
    };

    // fan btn
    View.OnClickListener modeSendClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ConsumerIrManager mCIR = ScaryUtil.getConsumerIRService(getApplicationContext());
            if (!mCIR.hasIrEmitter()) {
                Toast.makeText(getApplicationContext(), "No IR Emitter found", Toast.LENGTH_LONG).show();
                Log.e(TAG, "No IR Emitter found");
                return;
            }
            if (!m_Inst.power) {
                return;
            }
            ImageView tv = ((ImageView) findViewById(R.id.mode));
            int mode = m_Inst.mode;
            String what = "";

            mode =  (mode == 4) ?  0 : mode+1;
            switch(mode){
                case 0://auto
                    what = "Auto";
                    tv.setImageResource(R.drawable.mode_a);
                    break;
                case 1://cool
                    what = "Cool";
                    tv.setImageResource(R.drawable.mode_c);
                    break;
                case 2://dry
                    what = "Dry";
                    tv.setImageResource(R.drawable.mode_d);
                    break;
                case 3://fan
                    what = "Fan";
                    tv.setImageResource(R.drawable.mode_f);
                    break;
                case 4://heat
                    what = "Heat";
                    tv.setImageResource(R.drawable.mode_h);
                    break;
            }
            m_Inst.mode = mode;
            Toast.makeText(getApplicationContext(), "Operation Mode "+what, Toast.LENGTH_SHORT).show();
            TransmissionCode data = ScaryUtil.getIRCode(m_Inst.sequence, Constants.mode + mode);
            ScaryUtil.transmit(getApplicationContext(),data);
        }
    };

}
