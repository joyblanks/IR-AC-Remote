package com.blanks.joy.iracremote.devices;

/**
 * TransmissionCode
 * This object will hold the frequency to transmit and an array of bytes for the specific operation
 */
public class TransmissionCode {
	private String transmission;
	private int frequency;

	public TransmissionCode(int f, String t) {
		transmission = t;
		frequency = f;
	}

    /**
     * TransmissionCode :: getTransmissionPulses()
     * other fallbacks for vendor IR APIs using the pulses targetting API 19
    */
	public int[] getTransmissionPulses() {
        String[] countArray = this.transmission.split(",");
        int[] intTransmission = new int[countArray.length];
        for (int i = 0; i < countArray.length; i++) {
            intTransmission[i] = Integer.parseInt(countArray[i]);
        }
        return intTransmission;
	}

    /**
     * TransmissionCode :: getTransmission()
     * Samsung/Modern phones will use API 21 to transmit which says duration in microsecs
     *
    */
    public int[] getTransmission(){
        int pulses = 1000000/this.frequency;
        String[] countArray = this.transmission.split(",");
        int[] anotherFix = new int[countArray.length];
        for (int i = 0; i < countArray.length; i++) {
            anotherFix[i] = Integer.parseInt(countArray[i]) * pulses;
        }
        return anotherFix;
    }

	public int getFrequency() {
		return frequency;
	}
	
}
