package com.blanks.joy.iracremote.devices;

/**
 * TransmissionCode
 * This object will hold the frequency to transmit and an array of bytes for the specific operation
 */
public class TransmissionCode {
	private int[] transmission;
	private int frequency;

	public TransmissionCode(int f, String t) {
		transmission = count2duration(f, t);
		frequency = f;
	}

	public int[] getTransmission() {
		return transmission;
	}

	public int getFrequency() {
		return frequency;
	}

    //Fix for new Lollipop API
    private int[] count2duration(int freq, String countPattern) {
        int pulses = 1000000/freq;
        String[] countArray = countPattern.split(",");
        int[] anotherFix = new int[countArray.length];
        for (int i = 0; i < countArray.length; i++) {
            anotherFix[i] = Integer.parseInt(countArray[i]) * pulses;
        }
        return anotherFix;
    }
	
}
