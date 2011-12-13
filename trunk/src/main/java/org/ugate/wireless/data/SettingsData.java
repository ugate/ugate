package org.ugate.wireless.data;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.ugate.Settings;
import org.ugate.UGateKeeper;

/**
 * Settings data
 */
public class SettingsData {

	private int keyCode1;
	private int keyCode2;
	private int keyCode3;
	private int camResolution;
	private int sonarAlarmOn;
	private int pirAlarmOn;
	private int mwAlarmOn;
	private int gateAlarmOn;
	private int sonarDistanceThresholdFeet;
	private int sonarDistanceThresholdInches;
	private int sonarDelayBetweenTrips;
	private int irDistanceThresholdFeet;
	private int irDistanceThresholdInches;
	private int irDelayBetweenTrips;
	private int mwSpeedThresholdCyclesPerSecond;
	private int mwDelayBetweenTrips;
	private int multiAlarmTripState;
	private int camSonarTripPanAngle;
	private int camSonarTripTiltAngle;
	private int camIrTripPanAngle;
	private int camIrTripTiltAngle;
	private int camMicrowaveTripPanAngle;
	private int camMicrowaveTripTiltAngle;
	
	/**
	 * Constructs settings data with the current preference data
	 */
	public SettingsData() {
		setPreferenceValues();
	}
	
	/**
	 * Full constructor
	 * 
	 * @param keyCode1
	 * @param keyCode2
	 * @param keyCode3
	 * @param camResolution
	 * @param sonarAlarmOn
	 * @param pirAlarmOn
	 * @param mwAlarmOn
	 * @param gateAlarmOn
	 * @param sonarDistanceThresholdFeet
	 * @param sonarDistanceThresholdInches
	 * @param sonarDelayBetweenTrips
	 * @param irDistanceThresholdFeet
	 * @param irDistanceThresholdInches
	 * @param irDelayBetweenTrips
	 * @param mwSpeedThresholdCyclesPerSecond
	 * @param mwDelayBetweenTrips
	 * @param multiAlarmTripState
	 * @param camSonarTripPanAngle
	 * @param camSonarTripTiltAngle
	 * @param camIrTripPanAngle
	 * @param camIrTripTiltAngle
	 * @param camMicrowaveTripPanAngle
	 * @param camMicrowaveTripTiltAngle
	 */
	public SettingsData(int keyCode1, int keyCode2, int keyCode3,
			int camResolution, int sonarAlarmOn, int pirAlarmOn, int mwAlarmOn,
			int gateAlarmOn, int sonarDistanceThresholdFeet,
			int sonarDistanceThresholdInches, int sonarDelayBetweenTrips,
			int irDistanceThresholdFeet, int irDistanceThresholdInches,
			int irDelayBetweenTrips, int mwSpeedThresholdCyclesPerSecond,
			int mwDelayBetweenTrips, int multiAlarmTripState,
			int camSonarTripPanAngle, int camSonarTripTiltAngle,
			int camIrTripPanAngle, int camIrTripTiltAngle,
			int camMicrowaveTripPanAngle, int camMicrowaveTripTiltAngle) {
		super();
		this.keyCode1 = keyCode1;
		this.keyCode2 = keyCode2;
		this.keyCode3 = keyCode3;
		this.camResolution = camResolution;
		this.sonarAlarmOn = sonarAlarmOn;
		this.pirAlarmOn = pirAlarmOn;
		this.mwAlarmOn = mwAlarmOn;
		this.gateAlarmOn = gateAlarmOn;
		this.sonarDistanceThresholdFeet = sonarDistanceThresholdFeet;
		this.sonarDistanceThresholdInches = sonarDistanceThresholdInches;
		this.sonarDelayBetweenTrips = sonarDelayBetweenTrips;
		this.irDistanceThresholdFeet = irDistanceThresholdFeet;
		this.irDistanceThresholdInches = irDistanceThresholdInches;
		this.irDelayBetweenTrips = irDelayBetweenTrips;
		this.mwSpeedThresholdCyclesPerSecond = mwSpeedThresholdCyclesPerSecond;
		this.mwDelayBetweenTrips = mwDelayBetweenTrips;
		this.multiAlarmTripState = multiAlarmTripState;
		this.camSonarTripPanAngle = camSonarTripPanAngle;
		this.camSonarTripTiltAngle = camSonarTripTiltAngle;
		this.camIrTripPanAngle = camIrTripPanAngle;
		this.camIrTripTiltAngle = camIrTripTiltAngle;
		this.camMicrowaveTripPanAngle = camMicrowaveTripPanAngle;
		this.camMicrowaveTripTiltAngle = camMicrowaveTripTiltAngle;
	}

	public void setPreferenceValues() {
		keyCode1 = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.ACCESS_CODE_1_KEY));
		keyCode2 = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.ACCESS_CODE_2_KEY));
		keyCode3 = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.ACCESS_CODE_3_KEY));
		camResolution = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_RES_KEY));
		sonarAlarmOn = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.SONAR_ALARM_ON_KEY));
		pirAlarmOn = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.IR_ALARM_ON_KEY));
		mwAlarmOn = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.MW_ALARM_ON_KEY));
		gateAlarmOn = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.GATE_ACCESS_ON_KEY));
		sonarDistanceThresholdFeet = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.SONAR_DISTANCE_THRES_FEET_KEY));
		sonarDistanceThresholdInches = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.SONAR_DISTANCE_THRES_INCHES_KEY));
		sonarDelayBetweenTrips = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.SONAR_DELAY_BTWN_TRIPS_KEY));
		irDistanceThresholdFeet = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.IR_DISTANCE_THRES_FEET_KEY));
		irDistanceThresholdInches = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.IR_DISTANCE_THRES_INCHES_KEY));
		irDelayBetweenTrips = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.IR_DELAY_BTWN_TRIPS_KEY));
		mwSpeedThresholdCyclesPerSecond = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.MW_SPEED_THRES_CYCLES_PER_SEC_KEY));
		mwDelayBetweenTrips = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.MW_DELAY_BTWN_TRIPS_KEY));
		multiAlarmTripState = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.MULTI_ALARM_TRIP_STATE_KEY));
		camSonarTripPanAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_SONAR_TRIP_ANGLE_PAN_KEY));
		camSonarTripTiltAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_SONAR_TRIP_ANGLE_TILT_KEY));
		camIrTripPanAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_IR_TRIP_ANGLE_PAN_KEY));
		camIrTripTiltAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_IR_TRIP_ANGLE_TILT_KEY));
		camMicrowaveTripPanAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_MW_TRIP_ANGLE_PAN_KEY));
		camMicrowaveTripTiltAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_MW_TRIP_ANGLE_TILT_KEY));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return SettingsData.class.getSimpleName() + " [keyCode1=" + keyCode1 + ", keyCode2=" + keyCode2
				+ ", keyCode3=" + keyCode3 + ", camResolution=" + camResolution
				+ ", sonarAlarmOn=" + sonarAlarmOn + ", pirAlarmOn="
				+ pirAlarmOn + ", mwAlarmOn=" + mwAlarmOn + ", gateAlarmOn="
				+ gateAlarmOn + ", sonarDistanceThresholdFeet="
				+ sonarDistanceThresholdFeet
				+ ", sonarDistanceThresholdInches="
				+ sonarDistanceThresholdInches + ", sonarDelayBetweenTrips="
				+ sonarDelayBetweenTrips + ", irDistanceThresholdFeet="
				+ irDistanceThresholdFeet + ", irDistanceThresholdInches="
				+ irDistanceThresholdInches + ", irDelayBetweenTrips="
				+ irDelayBetweenTrips + ", mwSpeedThresholdCyclesPerSecond="
				+ mwSpeedThresholdCyclesPerSecond + ", mwDelayBetweenTrips="
				+ mwDelayBetweenTrips + ", multiAlarmTripState="
				+ multiAlarmTripState + ", camSonarTripPanAngle="
				+ camSonarTripPanAngle + ", camSonarTripTiltAngle="
				+ camSonarTripTiltAngle + ", camIrTripPanAngle="
				+ camIrTripPanAngle + ", camIrTripTiltAngle="
				+ camIrTripTiltAngle + ", camMicrowaveTripPanAngle="
				+ camMicrowaveTripPanAngle + ", camMicrowaveTripTiltAngle="
				+ camMicrowaveTripTiltAngle + "]";
	}
	
	/**
	 * @return the list of field values in the order they are declared
	 */
	public int[] toArray() {
		final Field[] fields = getClass().getDeclaredFields();
		final int[] list = new int[fields.length];
		int i = 0;
		for (final Field field : fields) {
			try {
				list[i++] = field.getInt(this);
				//System.out.println("Value: " + list[i - 1]);
			} catch (final Throwable t) {
				t.printStackTrace();
			}
		}
		return Arrays.copyOf(list, i);
	}

	/**
	 * @return the keyCode1
	 */
	public int getKeyCode1() {
		return keyCode1;
	}

	/**
	 * @param keyCode1 the keyCode1 to set
	 */
	public void setKeyCode1(int keyCode1) {
		this.keyCode1 = keyCode1;
	}

	/**
	 * @return the keyCode2
	 */
	public int getKeyCode2() {
		return keyCode2;
	}

	/**
	 * @param keyCode2 the keyCode2 to set
	 */
	public void setKeyCode2(int keyCode2) {
		this.keyCode2 = keyCode2;
	}

	/**
	 * @return the keyCode3
	 */
	public int getKeyCode3() {
		return keyCode3;
	}

	/**
	 * @param keyCode3 the keyCode3 to set
	 */
	public void setKeyCode3(int keyCode3) {
		this.keyCode3 = keyCode3;
	}

	/**
	 * @return the camResolution
	 */
	public int getCamResolution() {
		return camResolution;
	}

	/**
	 * @param camResolution the camResolution to set
	 */
	public void setCamResolution(int camResolution) {
		this.camResolution = camResolution;
	}

	/**
	 * @return the sonarAlarmOn
	 */
	public int getSonarAlarmOn() {
		return sonarAlarmOn;
	}

	/**
	 * @param sonarAlarmOn the sonarAlarmOn to set
	 */
	public void setSonarAlarmOn(int sonarAlarmOn) {
		this.sonarAlarmOn = sonarAlarmOn;
	}

	/**
	 * @return the pirAlarmOn
	 */
	public int getPirAlarmOn() {
		return pirAlarmOn;
	}

	/**
	 * @param pirAlarmOn the pirAlarmOn to set
	 */
	public void setPirAlarmOn(int pirAlarmOn) {
		this.pirAlarmOn = pirAlarmOn;
	}

	/**
	 * @return the mwAlarmOn
	 */
	public int getMwAlarmOn() {
		return mwAlarmOn;
	}

	/**
	 * @param mwAlarmOn the mwAlarmOn to set
	 */
	public void setMwAlarmOn(int mwAlarmOn) {
		this.mwAlarmOn = mwAlarmOn;
	}

	/**
	 * @return the gateAlarmOn
	 */
	public int getGateAlarmOn() {
		return gateAlarmOn;
	}

	/**
	 * @param gateAlarmOn the gateAlarmOn to set
	 */
	public void setGateAlarmOn(int gateAlarmOn) {
		this.gateAlarmOn = gateAlarmOn;
	}

	/**
	 * @return the sonarDistanceThresholdFeet
	 */
	public int getSonarDistanceThresholdFeet() {
		return sonarDistanceThresholdFeet;
	}

	/**
	 * @param sonarDistanceThresholdFeet the sonarDistanceThresholdFeet to set
	 */
	public void setSonarDistanceThresholdFeet(int sonarDistanceThresholdFeet) {
		this.sonarDistanceThresholdFeet = sonarDistanceThresholdFeet;
	}

	/**
	 * @return the sonarDistanceThresholdInches
	 */
	public int getSonarDistanceThresholdInches() {
		return sonarDistanceThresholdInches;
	}

	/**
	 * @param sonarDistanceThresholdInches the sonarDistanceThresholdInches to set
	 */
	public void setSonarDistanceThresholdInches(int sonarDistanceThresholdInches) {
		this.sonarDistanceThresholdInches = sonarDistanceThresholdInches;
	}

	/**
	 * @return the sonarDelayBetweenTrips
	 */
	public int getSonarDelayBetweenTrips() {
		return sonarDelayBetweenTrips;
	}

	/**
	 * @param sonarDelayBetweenTrips the sonarDelayBetweenTrips to set
	 */
	public void setSonarDelayBetweenTrips(int sonarDelayBetweenTrips) {
		this.sonarDelayBetweenTrips = sonarDelayBetweenTrips;
	}

	/**
	 * @return the irDistanceThresholdFeet
	 */
	public int getIrDistanceThresholdFeet() {
		return irDistanceThresholdFeet;
	}

	/**
	 * @param irDistanceThresholdFeet the irDistanceThresholdFeet to set
	 */
	public void setIrDistanceThresholdFeet(int irDistanceThresholdFeet) {
		this.irDistanceThresholdFeet = irDistanceThresholdFeet;
	}

	/**
	 * @return the irDistanceThresholdInches
	 */
	public int getIrDistanceThresholdInches() {
		return irDistanceThresholdInches;
	}

	/**
	 * @param irDistanceThresholdInches the irDistanceThresholdInches to set
	 */
	public void setIrDistanceThresholdInches(int irDistanceThresholdInches) {
		this.irDistanceThresholdInches = irDistanceThresholdInches;
	}

	/**
	 * @return the irDelayBetweenTrips
	 */
	public int getIrDelayBetweenTrips() {
		return irDelayBetweenTrips;
	}

	/**
	 * @param irDelayBetweenTrips the irDelayBetweenTrips to set
	 */
	public void setIrDelayBetweenTrips(int irDelayBetweenTrips) {
		this.irDelayBetweenTrips = irDelayBetweenTrips;
	}

	/**
	 * @return the mwSpeedThresholdCyclesPerSecond
	 */
	public int getMwSpeedThresholdCyclesPerSecond() {
		return mwSpeedThresholdCyclesPerSecond;
	}

	/**
	 * @param mwSpeedThresholdCyclesPerSecond the mwSpeedThresholdCyclesPerSecond to set
	 */
	public void setMwSpeedThresholdCyclesPerSecond(
			int mwSpeedThresholdCyclesPerSecond) {
		this.mwSpeedThresholdCyclesPerSecond = mwSpeedThresholdCyclesPerSecond;
	}

	/**
	 * @return the mwDelayBetweenTrips
	 */
	public int getMwDelayBetweenTrips() {
		return mwDelayBetweenTrips;
	}

	/**
	 * @param mwDelayBetweenTrips the mwDelayBetweenTrips to set
	 */
	public void setMwDelayBetweenTrips(int mwDelayBetweenTrips) {
		this.mwDelayBetweenTrips = mwDelayBetweenTrips;
	}

	/**
	 * @return the multiAlarmTripState
	 */
	public int getMultiAlarmTripState() {
		return multiAlarmTripState;
	}

	/**
	 * @param multiAlarmTripState the multiAlarmTripState to set
	 */
	public void setMultiAlarmTripState(int multiAlarmTripState) {
		this.multiAlarmTripState = multiAlarmTripState;
	}

	/**
	 * @return the camSonarTripPanAngle
	 */
	public int getCamSonarTripPanAngle() {
		return camSonarTripPanAngle;
	}

	/**
	 * @param camSonarTripPanAngle the camSonarTripPanAngle to set
	 */
	public void setCamSonarTripPanAngle(int camSonarTripPanAngle) {
		this.camSonarTripPanAngle = camSonarTripPanAngle;
	}

	/**
	 * @return the camSonarTripTiltAngle
	 */
	public int getCamSonarTripTiltAngle() {
		return camSonarTripTiltAngle;
	}

	/**
	 * @param camSonarTripTiltAngle the camSonarTripTiltAngle to set
	 */
	public void setCamSonarTripTiltAngle(int camSonarTripTiltAngle) {
		this.camSonarTripTiltAngle = camSonarTripTiltAngle;
	}

	/**
	 * @return the camIrTripPanAngle
	 */
	public int getCamIrTripPanAngle() {
		return camIrTripPanAngle;
	}

	/**
	 * @param camIrTripPanAngle the camIrTripPanAngle to set
	 */
	public void setCamIrTripPanAngle(int camIrTripPanAngle) {
		this.camIrTripPanAngle = camIrTripPanAngle;
	}

	/**
	 * @return the camIrTripTiltAngle
	 */
	public int getCamIrTripTiltAngle() {
		return camIrTripTiltAngle;
	}

	/**
	 * @param camIrTripTiltAngle the camIrTripTiltAngle to set
	 */
	public void setCamIrTripTiltAngle(int camIrTripTiltAngle) {
		this.camIrTripTiltAngle = camIrTripTiltAngle;
	}

	/**
	 * @return the camMicrowaveTripPanAngle
	 */
	public int getCamMicrowaveTripPanAngle() {
		return camMicrowaveTripPanAngle;
	}

	/**
	 * @param camMicrowaveTripPanAngle the camMicrowaveTripPanAngle to set
	 */
	public void setCamMicrowaveTripPanAngle(int camMicrowaveTripPanAngle) {
		this.camMicrowaveTripPanAngle = camMicrowaveTripPanAngle;
	}

	/**
	 * @return the camMicrowaveTripTiltAngle
	 */
	public int getCamMicrowaveTripTiltAngle() {
		return camMicrowaveTripTiltAngle;
	}

	/**
	 * @param camMicrowaveTripTiltAngle the camMicrowaveTripTiltAngle to set
	 */
	public void setCamMicrowaveTripTiltAngle(int camMicrowaveTripTiltAngle) {
		this.camMicrowaveTripTiltAngle = camMicrowaveTripTiltAngle;
	}
}
