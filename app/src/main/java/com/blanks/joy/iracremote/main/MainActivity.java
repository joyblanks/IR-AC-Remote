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

import com.blanks.joy.iracremote.R;
import com.blanks.joy.iracremote.constants.Constants;
import com.blanks.joy.iracremote.devices.TransmissionCodes;
import com.blanks.joy.iracremote.instance.Singleton;
import com.blanks.joy.iracremote.interfaces.VolButtonListener;
import com.blanks.joy.iracremote.ui.VolBtn;

public class MainActivity extends Activity {
	private static final String TAG = "JoyIR";
	ConsumerIrManager mCIR;

	Singleton m_Inst = Singleton.getInstance();
	VolBtn rv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mCIR = (ConsumerIrManager) getSystemService(android.content.Context.CONSUMER_IR_SERVICE);

		findViewById(R.id.swing).setOnClickListener(swingSendClickListener);
		findViewById(R.id.power).setOnClickListener(powerSendClickListener);
		findViewById(R.id.sequence).setOnClickListener(seqSendClickListener);
		findViewById(R.id.fan).setOnClickListener(fanSendClickListener);
		findViewById(R.id.mode).setOnClickListener(modeSendClickListener);
		
		((TextView) findViewById(R.id.sequence)).setText("" + m_Inst.sequence);
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
		rv.SetState(m_Inst.power);
		rv.SetListener(new VolButtonListener() {
			// trigger IR transmission for temp control
			public void onTriggerChange() {
				findViewById(R.id.temp).post(new Runnable() {
					public void run() {

						if (m_Inst.power) {
							TransmissionCodes data = m_Inst.getIrCodesAll()
									.get(m_Inst.sequence, m_Inst.temp); // (TransmissionCodes)samsung.get(m_Inst.temp);
							int freq = data.getFrequency();
                            int[] c = (data.getTransmission());
							if (c != null) {
								mCIR.transmit(freq, c);
							}
						}
					}
				});
			}

			public void onRotate(final int percentage, final int angle) {
				findViewById(R.id.temp).post(new Runnable() {
					public void run() {
						changeTemp(percentage, angle);

					}
				});
			}

		});
		TextView tempCount = ((TextView) findViewById(R.id.temp));
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Digital.otf");
		tempCount.setTypeface(tf);
		tempCount.setText(!m_Inst.power ? "--" : m_Inst.temp + "");
	}


	// swing btn
	View.OnClickListener swingSendClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (!mCIR.hasIrEmitter()) {
				Log.e(TAG, "No IR Emitter found\n");
				return;
			}
			if (!m_Inst.power) {
				return;
			}
			ImageView swingView = (ImageView)findViewById(R.id.swing);

			TransmissionCodes data;
			if (m_Inst.swing) {
				data = m_Inst.getIrCodesAll().get(m_Inst.sequence, Constants.swing);
				//((TextView) findViewById(R.id.swingtext)).setText("Swing:OFF");
				swingView.setImageResource(R.drawable.swingoff);
			} else {
				data = m_Inst.getIrCodesAll().get(
						m_Inst.sequence, Constants.swing + 1);
				//((TextView) findViewById(R.id.swingtext)).setText("Swing:ON");
				swingView.setImageResource(R.drawable.swingon);
			}
			m_Inst.swing = !m_Inst.swing;

            int freq = data.getFrequency();

            int[] c = (data.getTransmission());
			mCIR.transmit(freq, c);
		}
	};

	// Power btn
	View.OnClickListener powerSendClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (!mCIR.hasIrEmitter()) {
				Log.e(TAG, "No IR Emitter found\n");
				return;
			}
            try {
                TransmissionCodes data;
                TextView tv = ((TextView) findViewById(R.id.temp));

                if (rv.ismState()) {
                    data = m_Inst.getIrCodesAll().get(m_Inst.sequence, Constants.power + 1);
                    rv.SetState(false);
                    m_Inst.power = false;
                    tv.setText("--");

                } else {
                    // poweron
                    data = m_Inst.getIrCodesAll().get(m_Inst.sequence, Constants.power);
                    rv.SetState(true);
                    tv.setText("" + m_Inst.temp);
                    m_Inst.power = true;
                    ((ImageView) findViewById(R.id.swing)).setImageResource(m_Inst.swing ? R.drawable.swingon : R.drawable.swingoff);

                }
                int freq = data.getFrequency();
                int[] c = (data.getTransmission());
                mCIR.transmit(freq, c);
            }catch (Exception e){
                Log.e(TAG,e.toString());
            }
		}
	};


	// sequence btn
	View.OnClickListener seqSendClickListener = new View.OnClickListener() {
		public void onClick(View v) {

			TextView tv = ((TextView) findViewById(R.id.sequence));
			int seq = Integer.parseInt((String) tv.getText());
			if (seq == 3) {
				seq = 1;
			} else
				seq++;
			tv.setText("" + seq);
			m_Inst.sequence = seq;
		}
	};


	// fan btn
	View.OnClickListener fanSendClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (!mCIR.hasIrEmitter()) {
				Log.e(TAG, "No IR Emitter found\n");
				return;
			}
			if (!m_Inst.power) {
				return;
			}
			ImageView tv = ((ImageView) findViewById(R.id.fan));
			int seq = m_Inst.fan;

			if (seq == 3) {
				seq = 0;
			} else
				seq++;
			switch(seq){
			case 0://auto
				tv.setImageResource(R.drawable.fan_auto);
				break;
			case 1://low
				tv.setImageResource(R.drawable.fan_low);
				break;
			case 2://med
				tv.setImageResource(R.drawable.fan_medium);
				break;
			case 3://high
				tv.setImageResource(R.drawable.fan_high);
				break;
			}
			m_Inst.fan = seq;

			TransmissionCodes data = m_Inst.getIrCodesAll().get(m_Inst.sequence, Constants.fan + seq);

			int freq = data.getFrequency();
            int[] c = (data.getTransmission());
			mCIR.transmit(freq, c);


		}
	};

	// fan btn
	View.OnClickListener modeSendClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (!mCIR.hasIrEmitter()) {
				Log.e(TAG, "No IR Emitter found\n");
				return;
			}
			if (!m_Inst.power) {
				return;
			}
			ImageView tv = ((ImageView) findViewById(R.id.mode));
			int mode = m_Inst.mode;

			if (mode == 4) {
				mode = 0;
			} else
				mode++;
			switch(mode){
			case 0://auto
				tv.setImageResource(R.drawable.mode_a);
				break;
			case 1://cool
				tv.setImageResource(R.drawable.mode_c);
				break;
			case 2://dry
				tv.setImageResource(R.drawable.mode_d);
				break;
			case 3://fan
				tv.setImageResource(R.drawable.mode_f);
				break;
			case 4://heat
				tv.setImageResource(R.drawable.mode_h);
				break;
			}
			m_Inst.mode = mode;

			TransmissionCodes data = m_Inst.getIrCodesAll().get(m_Inst.sequence, Constants.mode + mode);


			int freq = data.getFrequency();

            int[] c = data.getTransmission();
			mCIR.transmit(freq, c);


		}
	};

	// volume change call
	private void changeTemp(int p, int a) {
		int temp = p * 15 / 100 + 16;
		TextView tempView = ((TextView) findViewById(R.id.temp));

		if (m_Inst.power) {
			tempView.setText(temp + "");
			m_Inst.temp = temp;
			m_Inst.tempAngle = a;

		} else
			tempView.setText("--");
        //Log.i(TAG, "Hello\n"+m_Inst.temp);
	}






}
