package org.ugate.wireless.data;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.ugate.Settings;
import org.ugate.UGateKeeper;


/**
 * Settings data
 */
public class SettingsData extends RxData {

	private int universalRemoteOn;
	private KeyCodes keyCodes;
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
		super(Status.NORMAL, 0);
		setPreferenceValues();
	}
	
	/**
	 * Full constructor
	 * 
	 * @param status
	 * @param universalRemoteOn
	 * @param keyCodes
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
	public SettingsData(final Status status, final int signalStrength, int universalRemoteOn, int keyCode1, int keyCode2, int keyCode3,
			int camResolution, int sonarAlarmOn, int pirAlarmOn, int mwAlarmOn,
			int gateAlarmOn, int sonarDistanceThresholdFeet,
			int sonarDistanceThresholdInches, int sonarDelayBetweenTrips,
			int irDistanceThresholdFeet, int irDistanceThresholdInches,
			int irDelayBetweenTrips, int mwSpeedThresholdCyclesPerSecond,
			int mwDelayBetweenTrips, int multiAlarmTripState,
			int camSonarTripPanAngle, int camSonarTripTiltAngle,
			int camIrTripPanAngle, int camIrTripTiltAngle,
			int camMicrowaveTripPanAngle, int camMicrowaveTripTiltAngle) {
		super(status, signalStrength);
		this.universalRemoteOn = universalRemoteOn;
		this.keyCodes = new KeyCodes(status, signalStrength, keyCode1, keyCode2, keyCode3);
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

	/**
	 * Sets the parameter values from data stored in the preferences
	 */
	public void setPreferenceValues() {
		universalRemoteOn = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.UNIVERSAL_REMOTE_ACCESS_ON));
		keyCodes.setKeyCode1(Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.ACCESS_CODE_1)));
		keyCodes.setKeyCode2(Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.ACCESS_CODE_2)));
		keyCodes.setKeyCode3(Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.ACCESS_CODE_3)));
		camResolution = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_RES));
		sonarAlarmOn = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.SONAR_ALARM_ON));
		pirAlarmOn = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.IR_ALARM_ON));
		mwAlarmOn = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.MW_ALARM_ON));
		gateAlarmOn = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.GATE_ACCESS_ON));
		sonarDistanceThresholdFeet = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.SONAR_DISTANCE_THRES_FEET));
		sonarDistanceThresholdInches = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.SONAR_DISTANCE_THRES_INCHES));
		sonarDelayBetweenTrips = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.SONAR_DELAY_BTWN_TRIPS));
		irDistanceThresholdFeet = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.IR_DISTANCE_THRES_FEET));
		irDistanceThresholdInches = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.IR_DISTANCE_THRES_INCHES));
		irDelayBetweenTrips = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.IR_DELAY_BTWN_TRIPS));
		mwSpeedThresholdCyclesPerSecond = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.MW_SPEED_THRES_CYCLES_PER_SEC));
		mwDelayBetweenTrips = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.MW_DELAY_BTWN_TRIPS));
		multiAlarmTripState = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.MULTI_ALARM_TRIP_STATE));
		camSonarTripPanAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_SONAR_TRIP_ANGLE_PAN));
		camSonarTripTiltAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_SONAR_TRIP_ANGLE_TILT));
		camIrTripPanAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_IR_TRIP_ANGLE_PAN));
		camIrTripTiltAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_IR_TRIP_ANGLE_TILT));
		camMicrowaveTripPanAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_MW_TRIP_ANGLE_PAN));
		camMicrowaveTripTiltAngle = Integer.parseInt(UGateKeeper.DEFAULT.preferencesGet(Settings.CAM_MW_TRIP_ANGLE_TILT));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final Field[] fields = getClass().getDeclaredFields();
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(" [");
		for (final Field field : fields) {
			try {
				sb.append(field.getName());
				sb.append('=');
				sb.append(field.getInt(this));
				sb.append(", ");
			} catch (final Throwable t) {
				t.printStackTrace();
			}
		}
		sb.append(']');
		return sb.toString();
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
	 * @return the universalRemoteOn
	 */
	public int getGniversalRemoteOn() {
		return universalRemoteOn;
	}

	/**
	 * @return the keyCodes
	 */
	public KeyCodes getKeyCodes() {
		return keyCodes;
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
