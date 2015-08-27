/*
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2013 HTC Corporation
 *
 * All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.blanks.joy.iracremote.htc;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.htc.circontrol.CIRControl;
import com.htc.htcircontrol.HtcIrData;

import java.util.Arrays;
import java.util.UUID;

public class ConsumerIrManagerHtc extends ConsumerIrManagerCompat {

	private static final String TAG = "ConsumerIrManagerHtc";
	private CIRControl mControl;
	private Context mContext;
	private HtcIrData mLearntKey;

	public ConsumerIrManagerHtc(Context context) {
		super(context);
		mContext = context;
		mControl = new CIRControl(context, mHandler);
		supportedAPIs = supportedAPIs & HTCSUPPORT;
	}

	Handler mHandler = new Handler(Looper.getMainLooper())
	{ 
		@Override
		public void handleMessage(Message msg)
		{
			UUID rid;
			String text = null;
			switch (msg.what) {
			case CIRControl.MSG_RET_LEARN_IR:
				//CIR APP developer can check UUID to check the reply message  
				rid = (UUID) msg.getData().getSerializable(CIRControl.KEY_RESULT_ID);
				Log.i(TAG, "Receive IR Returned UUID: " + rid);
				
				//TODO: check learning IR data which is in HtcIrData object.
				//If data is null, the learning is not successful, so check error type.
				mLearntKey = (HtcIrData) msg.getData().getSerializable(CIRControl.KEY_CMD_RESULT);
				
				SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
				if(mLearntKey!=null) {
					text="Repeat:" + mLearntKey.getRepeatCount() + " Freq:" 
				      + mLearntKey.getFrequency() + " Frame length:" + mLearntKey.getFrame().length
				      + " Frame= "+ Arrays.toString(mLearntKey.getFrame());
					Editor editor = preferences.edit();
					editor.putInt(PREFERENCE_KEY_FREQUENCY, mLearntKey.getFrequency());
					editor.putString(PREFERENCE_KEY_FRAME, Arrays.toString(mLearntKey.getFrame()));
					editor.commit();
					
				}
				else {
					switch(msg.arg1) {
					case CIRControl.ERR_LEARNING_TIMEOUT:
						//TODO: timeout error because of CIR do not receive IR data.
						text = "Learn IR Error: ERR_LEARNING_TIMEOUT";
						break;
					case CIRControl.ERR_PULSE_ERROR:
						//CIR receives IR data but data is unusable.
						//The common error is caused by user he/she does not align the phone's CIR receiver
						// with CIR transmitter of plastic remote.		
						text="Learn IR Error: ERR_PULSE_ERROR";
						break;
					case CIRControl.ERR_OUT_OF_FREQ:
						//This error is to warn user that the device is not supported or
						// the phone's CIR receiver does not align with CIR transmitter of the device.
						text="Learn IR Error: ERR_OUT_OF_FREQ";
						break;
					case CIRControl.ERR_IO_ERROR:
						//CIR hardware component is busy in doing early CIR activity.
						text="Learn IR Error: ERR_IO_ERROR";
						break;
					default:
						text="";
						break;
					}
				}
				break;
			case CIRControl.MSG_RET_TRANSMIT_IR:
				rid = (UUID) msg.getData().getSerializable(CIRControl.KEY_RESULT_ID);
				Log.i(TAG, "Send IR Returned UUID: " + rid);
				switch(msg.arg1) {
				case CIRControl.ERR_IO_ERROR:
					//CIR hardware component is busy in doing early CIR command.
					text="Send IR Error=ERR_IO_ERROR";
					break;
				case CIRControl.ERR_INVALID_VALUE:
					text="Send IR Error=ERR_INVALID_VALUE";
					break;
				case CIRControl.ERR_CMD_DROPPED:
					//SDK might be too busy to send IR key, developer can try later, or send IR key with non-droppable setting  
					text="Send IR Error=ERR_CMD_DROPPED";
					break;
				default:
					text="";
					break;
				}
				break;
			case CIRControl.MSG_RET_CANCEL:
				switch(msg.arg1) {
				case CIRControl.ERR_IO_ERROR:
					//CIR hardware component is busy in doing early CIR command.
					text="Cancel Error: ERR_IO_ERROR";
					break;
				case CIRControl.ERR_CANCEL_FAIL:
					//CIR hardware component is busy in doing early CIR command.
					text="Cancel Error: ERR_CANCEL_FAIL";
					break;
				default:
					text="";
					break;
				}
				break;				
			default:
				super.handleMessage(msg);
			}
			if(text!=null) {
				final String text1 = text;
				//textView.post(new Runnable() { public void run() {textView.setText(text1);}});
			}
	    }
	};
    private class SendRunnable implements Runnable {
    	private int frequency;
		private int[] frame;

		public SendRunnable(int frequency, int[] frame) {
			this.frequency = frequency;
			this.frame = frame;
    	}
    	
    	public void run() {
			if (mLearntKey != null) {
				mControl.transmitIRCmd (mLearntKey, true);
			} else {
    			try {
    				HtcIrData ird = new HtcIrData (1, frequency, frame);
    				mControl.transmitIRCmd (ird, false); //no drop command
    			}
    			catch(IllegalArgumentException iae) {
    				//TODO: developer will get exception if any argument of HtcIrData is incorrect  
    				Log.e(TAG, "new HtcIrData: " + iae);
    				throw iae;
    			}
			}
    	}
    }

	@Override
	public void transmit(int carrierFrequency, int[] pattern) {
		mHandler.post(new SendRunnable(carrierFrequency, pattern));
	}

	@Override
	public CarrierFrequencyRange[] getCarrierFrequencies() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//TODO: call getCarrierFrequencies() via reflection in this case
			// or call standard API from a separate activity
		} else {
			Log.i(TAG, "getCarrierFrequencies() is not available via the HTC CIR APIs");
		}
		return null;
	}
	@TargetApi(19)
	@Override
	public boolean hasIrEmitter() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
		    return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR);
        } else if(mControl!=null) {
        	return true;
	    }		
		return false;
	}
	
	@Override
	public UUID learnIRCmd(int timeout) {
		if (mControl != null) {
			return mControl.learnIRCmd(timeout);
		}
		return null;
	}
	@Override
	public void start() {
		if (mControl != null) {
			mControl.start();
		} else {
			Log.w(TAG, "There is no CIRModule in this device , can't do start!");
		}
	}	
	@Override
	public void stop() {
		if (mControl != null) {
			mControl.stop();
		} else {
			Log.w(TAG, "There is no CIRModule in this device , can't do stop!");
		}
	}		
	@Override
	public boolean isStarted() {
		if (mControl != null) {
			return mControl.isStarted();
		}
		return false;
	}
	@Override
	public UUID cancelCommand() {
		if (mControl != null) {
			return mControl.cancelCommand();
		} else {
			Log.w(TAG, "There is no CIRModule in this device , can't do cancelCommand!");
		}
		return null;
	}
	@Override
	public UUID discardCommand(UUID uuid) {
		if (mControl != null) {
			return mControl.discardCommand(uuid);
		} else {
			Log.w(TAG, "There is no CIRModule in this device , can't do discardCommand!");
		}
		return null;
	}
}
