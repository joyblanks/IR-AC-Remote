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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public abstract class ConsumerIrManagerCompat {
	
	public static final String PREFERENCE_FILE_NAME = "IR_Temp";
	public static final String PREFERENCE_KEY_FREQUENCY = "IR_FREQUENCY";
	public static final String PREFERENCE_KEY_FRAME = "IR_FRAME";
	public static final int HTCSUPPORT = 1;
	
	protected int supportedAPIs = 0;
    protected Context mContext;
    protected TextView textView;
	
	/**
     * Factory method for creating corresponding CIR helper class
     */
    public static ConsumerIrManagerCompat createInstance(Context context) {
        if (hasPackage("com.htc.cirmodule", context)) {
            return new ConsumerIrManagerHtc(context);
        } else {
            return new ConsumerIrManagerBase(context);
        }
    }
    public ConsumerIrManagerCompat(Context context) {
    	mContext = context;
    }
	
	public static boolean hasPackage(String packageName, Context context){
        List<ApplicationInfo> packages;
        PackageManager manager;
        manager = context.getPackageManager();          
        packages = manager.getInstalledApplications(0);   
        for (ApplicationInfo info : packages) {
        	if(info.packageName.equals(packageName)) {
        		return true;
        	}
        }        
        return false;
    }
	
	public int getSupportedAPIs() {
		return supportedAPIs;
	}
	
	//Android 4.4 CIR APIs:
	public abstract void transmit(int carrierFrequency, int[] pattern);
	public abstract CarrierFrequencyRange[] getCarrierFrequencies();
	public abstract boolean hasIrEmitter();

	//helper class for < API 19 build support
	public final class CarrierFrequencyRange {
		private int minfreq;
		private int maxfreq;

		public CarrierFrequencyRange(int min, int max) {
			minfreq=min;
			maxfreq=max;
	    }
	    public int getMinFrequency() { return minfreq; } 
	    public int getMaxFrequency() { return maxfreq; }
	}

	// some additional HTC support APIs 
	public abstract UUID learnIRCmd(int timeout);
	public abstract void start();	
	public abstract void stop();
	public abstract boolean isStarted();	
	public abstract UUID cancelCommand();
	public abstract UUID discardCommand(UUID uuid);
	
	public void setTextView(TextView textView) {
		this.textView = textView;
	}
	
	//for complete access to all of the HTC CIR APIs either:
	// add additional compatibility support here
	// or
	// use ConsumerIrManagerHtc directly in a separate activity as follows:
	//
	// ConsumerIrManagerHtc mCIR = new ConsumerIrManagerHtc(getApplicationContext());
    // CIRControl mCIRControl = ConsumerIrManagerHtc.getCIRControl();
	// remember to mCIRControl.start() and mCIRControl.stop()
}
