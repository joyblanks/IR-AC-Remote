package com.blanks.joy.iracremote.devices;


public class TransmissionCodes {
	private int[] transmission;
	private int frequency;
	private int id;
	
	
	public TransmissionCodes(int f, String t) {
		transmission = getIntTransmission(t);
		frequency = f;
	}

	public int[] getTransmission() {
		return count2duration(frequency,transmission);
	}

	public int getFrequency() {
		return frequency;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int[] getIntTransmission(String s) {
		if (s.length() == 0) {
			return null;
		}
		String[] strArray = s.split(",");
		int[] intArray = new int[strArray.length];
		for (int i = 0; i < strArray.length; i++) {
			intArray[i] = Integer.parseInt(strArray[i]);
		}
		return intArray;
	}

    protected int[] count2duration(int freq, int[] countPattern) {
        int pulses = 1000000/freq;
        int[] anotherFix = new int[countPattern.length];
        for (int i = 0; i < countPattern.length; i++) {
            anotherFix[i] = countPattern[i] * pulses;
        }
        return anotherFix;
    }
	
}
