package com.blanks.joy.iracremote.devices;

import java.util.HashMap;

import android.util.SparseArray;

public class TransmissionList {
	private SparseArray<TransmissionCode> sequence1;
	private SparseArray<TransmissionCode> sequence2;
	private SparseArray<TransmissionCode> sequence3;
	
	
	public TransmissionList() {
		IRdata ird = new IRdata();
		HashMap<String,SparseArray<TransmissionCode>> transmitCodes = ird.initTransmitCodes();
		this.sequence1 = transmitCodes.get("sequence1");
		this.sequence2 = transmitCodes.get("sequence2");
		this.sequence3 = transmitCodes.get("sequence3");
	}
	
	public SparseArray<TransmissionCode> getSequence(String seq) {
		if(seq.equalsIgnoreCase("sequence1")){
			return sequence1;
		}else if(seq.equalsIgnoreCase("sequence2")){
			return sequence2;
		}else if(seq.equalsIgnoreCase("sequence3")){
			return sequence3;
		} else return null;
	}
	public TransmissionCode get(int seq,int key) {
		switch(seq){
		case 1: return this.sequence1.get(key);
		case 2: return this.sequence2.get(key);
		case 3: return this.sequence3.get(key);
		}
		return null;
	}
	
}
