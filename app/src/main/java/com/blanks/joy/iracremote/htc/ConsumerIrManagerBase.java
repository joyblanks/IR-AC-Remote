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
import android.hardware.ConsumerIrManager;
import android.util.Log;

import java.util.UUID;

@TargetApi(19)
public class ConsumerIrManagerBase extends ConsumerIrManagerCompat {
	private final static String TAG = "ConsumerIrManagerBase";
	private ConsumerIrManager mCIR;
	
	public ConsumerIrManagerBase(Context context) {
    	super(context);
        // Get a reference to the ConsumerIrManager
        mCIR = (ConsumerIrManager)context.getSystemService(Context.CONSUMER_IR_SERVICE);
    }
		
	ConsumerIrManager getConsumerIrManager() {
		return mCIR;
	}
	
	// ConsumerIrManagerCompat:
	@Override
	public void transmit(int carrierFrequency, int [] pattern) {
		if(mCIR!=null) {
			mCIR.transmit(carrierFrequency, pattern);
		}
	}
	@Override
	public CarrierFrequencyRange[] getCarrierFrequencies() {
		CarrierFrequencyRange[] result = null;
		if(mCIR!=null) {
			ConsumerIrManager.CarrierFrequencyRange[] ranges = mCIR.getCarrierFrequencies();
			result = new CarrierFrequencyRange[ranges.length];
			int n=0;
			for (ConsumerIrManager.CarrierFrequencyRange range : ranges) {
				result[n++] = new CarrierFrequencyRange(range.getMinFrequency(),
						                              range.getMaxFrequency());
            }
		}		
		return result;
	}	
	@Override
	public boolean hasIrEmitter() {
		if(mCIR!=null) {
			return mCIR.hasIrEmitter();
		}
		return false;
	}

	@Override
	public UUID learnIRCmd(int timeout) {
		Log.w(TAG, "learn IR command is not available on this device");
		return null;
	}			
	@Override
	public void start() {
		Log.w(TAG, "start() is not available on this device");
	}
	@Override
	public void stop() {
		Log.w(TAG, "stop() is not available on this device");
	}
	@Override
	public boolean isStarted() {
		if(mCIR!=null) {
			return true;
		}
		return false;
	}
	@Override
	public UUID cancelCommand() {
		Log.w(TAG, "cancelCommand() is not available on this device");
		return null;
	}
	@Override
	public UUID discardCommand(UUID uuid) {
		Log.w(TAG, "discardCommand(UUID) is not available on this device");
		return null;
	}
}
