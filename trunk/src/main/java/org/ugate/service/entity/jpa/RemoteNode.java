package org.ugate.service.entity.jpa;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.ugate.UGateEvent;
import org.ugate.UGateEvent.Type;
import org.ugate.UGateKeeper;
import org.ugate.service.entity.Model;


/**
 * The persistent class for the REMOTE_NODE database table.
 * 
 */
@Entity
@Table(name="REMOTE_NODE")
public class RemoteNode implements Model {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="REMOTE_NODE_ID_GENERATOR", sequenceName="SQ_REMOTE_NODE_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REMOTE_NODE_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private int id;

	@Basic
	@Column(name="CREATED_DATE", nullable=false)
	private Date createdDate;

	@Column(unique=true, nullable=false, length=100)
	private String address;

	@Min(value=0)
	@Max(value=181)
	@Column(name="CAM_ANGLE_PAN", nullable=false)
	private int camAnglePan;

	@Min(value=0)
	@Max(value=181)
	@Column(name="CAM_ANGLE_TILT", nullable=false)
	private int camAngleTilt;

	@Min(value=0)
	@Max(value=181)
	@Column(name="CAM_LASER_TRIP_ANGLE_PAN", nullable=false)
	private int camLaserTripAnglePan;

	@Min(value=0)
	@Max(value=4)
	@Column(name="CAM_LASER_TRIP_ANGLE_PRIORITY", nullable=false)
	private int camLaserTripAnglePriority;

	@Min(value=0)
	@Max(value=181)
	@Column(name="CAM_LASER_TRIP_ANGLE_TILT", nullable=false)
	private int camLaserTripAngleTilt;

	@Min(value=0)
	@Max(value=181)
	@Column(name="CAM_MW_TRIP_ANGLE_PAN", nullable=false)
	private int camMwTripAnglePan;

	@Min(value=0)
	@Max(value=4)
	@Column(name="CAM_MW_TRIP_ANGLE_PRIORITY", nullable=false)
	private int camMwTripAnglePriority;

	@Min(value=0)
	@Max(value=181)
	@Column(name="CAM_MW_TRIP_ANGLE_TILT", nullable=false)
	private int camMwTripAngleTilt;

	@Min(value=0)
	@Max(value=181)
	@Column(name="CAM_PIR_TRIP_ANGLE_PAN", nullable=false)
	private int camPirTripAnglePan;

	@Min(value=0)
	@Max(value=4)
	@Column(name="CAM_PIR_TRIP_ANGLE_PRIORITY", nullable=false)
	private int camPirTripAnglePriority;

	@Min(value=0)
	@Max(value=181)
	@Column(name="CAM_PIR_TRIP_ANGLE_TILT", nullable=false)
	private int camPirTripAngleTilt;

	@Min(value=0)
	@Column(name="CAM_RESOLUTION", nullable=false)
	private int camResolution;

	@Min(value=0)
	@Max(value=181)
	@Column(name="CAM_SONAR_TRIP_ANGLE_PAN", nullable=false)
	private int camSonarTripAnglePan;

	@Min(value=0)
	@Max(value=4)
	@Column(name="CAM_SONAR_TRIP_ANGLE_PRIORITY", nullable=false)
	private int camSonarTripAnglePriority;

	@Min(value=0)
	@Max(value=181)
	@Column(name="CAM_SONAR_TRIP_ANGLE_TILT", nullable=false)
	private int camSonarTripAngleTilt;

	@Min(value=0)
	@Column(name="CAM_IMG_CAPTURE_RETRY_CNT", nullable=false)
	private int camImgCaptureRetryCnt;

	@Min(value=0)
	@Max(value=1)
	@Column(name="DEVICE_SOUNDS_ON", nullable=false)
	private int deviceSoundsOn;

	@Min(value=0)
	@Max(value=1)
	@Column(name="GATE_ACCESS_ON", nullable=false)
	private int gateAccessOn;

	@Min(value=0)
	@Max(value=181)
	@Column(name="LASER_ANGLE_PAN", nullable=false)
	private int laserAnglePan;

	@Min(value=0)
	@Max(value=181)
	@Column(name="LASER_ANGLE_TILT", nullable=false)
	private int laserAngleTilt;

	@Min(value=0)
	@Column(name="LASER_DELAY_BTWN_TRIPS", nullable=false)
	private int laserDelayBtwnTrips;

	@Min(value=0)
	@Column(name="LASER_DISTANCE_THRES_FEET", nullable=false)
	private int laserDistanceThresFeet;

	@Min(value=0)
	@Column(name="LASER_DISTANCE_THRES_INCHES", nullable=false)
	private int laserDistanceThresInches;

	@Min(value=0)
	@Max(value=1)
	@Column(name="MAIL_ALERT_ON", nullable=false)
	private int mailAlertOn;

	@Min(value=0)
	@Max(value=15)
	@Column(name="MULTI_ALARM_TRIP_STATE", nullable=false)
	private int multiAlarmTripState;

	@Min(value=0)
	@Max(value=181)
	@Column(name="MW_ANGLE_PAN", nullable=false)
	private int mwAnglePan;

	@Min(value=0)
	@Column(name="MW_DELAY_BTWN_TRIPS", nullable=false)
	private int mwDelayBtwnTrips;

	@Min(value=0)
	@Column(name="MW_SPEED_THRES_CYCLES_PER_SEC", nullable=false)
	private int mwSpeedThresCyclesPerSec;

	@Min(value=0)
	@Column(name="PIR_DELAY_BTWN_TRIPS", nullable=false)
	private int pirDelayBtwnTrips;

	@Min(value=0)
	@Column(name="SONAR_DELAY_BTWN_TRIPS", nullable=false)
	private int sonarDelayBtwnTrips;

	@Min(value=0)
	@Column(name="SONAR_DISTANCE_THRES_FEET", nullable=false)
	private int sonarDistanceThresFeet;

	@Min(value=0)
	@Column(name="SONAR_DISTANCE_THRES_INCHES", nullable=false)
	private int sonarDistanceThresInches;

	@Min(value=0)
	@Max(value=181)
	@Column(name="SONAR_PIR_ANGLE_PAN", nullable=false)
	private int sonarPirAnglePan;

	@Min(value=0)
	@Max(value=181)
	@Column(name="SONAR_PIR_ANGLE_TILT", nullable=false)
	private int sonarPirAngleTilt;

	@Min(value=0)
	@Max(value=9)
	@Column(name="UNIVERSAL_REMOTE_ACCESS_CODE_1", nullable=false)
	private int universalRemoteAccessCode1;

	@Min(value=0)
	@Max(value=9)
	@Column(name="UNIVERSAL_REMOTE_ACCESS_CODE_2", nullable=false)
	private int universalRemoteAccessCode2;

	@Min(value=0)
	@Max(value=9)
	@Column(name="UNIVERSAL_REMOTE_ACCESS_CODE_3", nullable=false)
	private int universalRemoteAccessCode3;

	@Min(value=0)
	@Max(value=1)
	@Column(name="UNIVERSAL_REMOTE_ACCESS_ON", nullable=false)
	private int universalRemoteAccessOn;

	@Column(name="WORKING_DIR", nullable=false, length=100)
	private String workingDir;

	@Min(value=0)
	@Max(value=1)
	@Column(name="ALARMS_ON", nullable=false)
	private int alarmsOn;

	@Min(value=0)
	@Max(value=1)
	@Column(name="REPORT_READINGS", nullable=false)
	private int reportReadings;

	@Column(name="DEVICE_AUTO_SYNCHRONIZE")
	private boolean deviceAutoSynchronize;

	@Column(name="DEVICE_SYNCHRONIZED")
	private boolean deviceSynchronized;

	//bi-directional many-to-one association to Host
	@ManyToOne
	@JoinColumn(name="HOST_ID", nullable=false)
	private Host host;

	//bi-directional many-to-one association to RemoteNodeReading
	@OneToMany(mappedBy="remoteNode")
	private Set<RemoteNodeReading> remoteNodeReadings;

	/**
	 * Call {@linkplain UGateKeeper#notifyListeners(UGateEvent)} when any
	 * changes are committed
	 */
	@PostPersist
	@PostUpdate
	@PostRemove
	void notifyListeners() {
		UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(this,
				Type.WIRELESS_REMOTE_NODE_COMMITTED, false));
	}

	public RemoteNode() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getCamAnglePan() {
		return this.camAnglePan;
	}

	public void setCamAnglePan(int camAnglePan) {
		this.camAnglePan = camAnglePan;
	}

	public int getCamAngleTilt() {
		return this.camAngleTilt;
	}

	public void setCamAngleTilt(int camAngleTilt) {
		this.camAngleTilt = camAngleTilt;
	}

	public int getCamLaserTripAnglePan() {
		return this.camLaserTripAnglePan;
	}

	public void setCamLaserTripAnglePan(int camLaserTripAnglePan) {
		this.camLaserTripAnglePan = camLaserTripAnglePan;
	}

	public int getCamLaserTripAnglePriority() {
		return this.camLaserTripAnglePriority;
	}

	public void setCamLaserTripAnglePriority(int camLaserTripAnglePriority) {
		this.camLaserTripAnglePriority = camLaserTripAnglePriority;
	}

	public int getCamLaserTripAngleTilt() {
		return this.camLaserTripAngleTilt;
	}

	public void setCamLaserTripAngleTilt(int camLaserTripAngleTilt) {
		this.camLaserTripAngleTilt = camLaserTripAngleTilt;
	}

	public int getCamMwTripAnglePan() {
		return this.camMwTripAnglePan;
	}

	public void setCamMwTripAnglePan(int camMwTripAnglePan) {
		this.camMwTripAnglePan = camMwTripAnglePan;
	}

	public int getCamMwTripAnglePriority() {
		return this.camMwTripAnglePriority;
	}

	public void setCamMwTripAnglePriority(int camMwTripAnglePriority) {
		this.camMwTripAnglePriority = camMwTripAnglePriority;
	}

	public int getCamMwTripAngleTilt() {
		return this.camMwTripAngleTilt;
	}

	public void setCamMwTripAngleTilt(int camMwTripAngleTilt) {
		this.camMwTripAngleTilt = camMwTripAngleTilt;
	}

	public int getCamPirTripAnglePan() {
		return this.camPirTripAnglePan;
	}

	public void setCamPirTripAnglePan(int camPirTripAnglePan) {
		this.camPirTripAnglePan = camPirTripAnglePan;
	}

	public int getCamPirTripAnglePriority() {
		return this.camPirTripAnglePriority;
	}

	public void setCamPirTripAnglePriority(int camPirTripAnglePriority) {
		this.camPirTripAnglePriority = camPirTripAnglePriority;
	}

	public int getCamPirTripAngleTilt() {
		return this.camPirTripAngleTilt;
	}

	public void setCamPirTripAngleTilt(int camPirTripAngleTilt) {
		this.camPirTripAngleTilt = camPirTripAngleTilt;
	}

	public int getCamResolution() {
		return this.camResolution;
	}

	public void setCamResolution(int camResolution) {
		this.camResolution = camResolution;
	}

	public int getCamSonarTripAnglePan() {
		return this.camSonarTripAnglePan;
	}

	public void setCamSonarTripAnglePan(int camSonarTripAnglePan) {
		this.camSonarTripAnglePan = camSonarTripAnglePan;
	}

	public int getCamSonarTripAnglePriority() {
		return this.camSonarTripAnglePriority;
	}

	public void setCamSonarTripAnglePriority(int camSonarTripAnglePriority) {
		this.camSonarTripAnglePriority = camSonarTripAnglePriority;
	}

	public int getCamSonarTripAngleTilt() {
		return this.camSonarTripAngleTilt;
	}

	public void setCamSonarTripAngleTilt(int camSonarTripAngleTilt) {
		this.camSonarTripAngleTilt = camSonarTripAngleTilt;
	}

	public int getCamImgCaptureRetryCnt() {
		return camImgCaptureRetryCnt;
	}

	public void setCamImgCaptureRetryCnt(int camImgCaptureRetryCnt) {
		this.camImgCaptureRetryCnt = camImgCaptureRetryCnt;
	}

	public int getDeviceSoundsOn() {
		return this.deviceSoundsOn;
	}

	public void setDeviceSoundsOn(int deviceSoundsOn) {
		this.deviceSoundsOn = deviceSoundsOn;
	}

	public int getGateAccessOn() {
		return this.gateAccessOn;
	}

	public void setGateAccessOn(int gateAccessOn) {
		this.gateAccessOn = gateAccessOn;
	}

	public int getLaserAnglePan() {
		return this.laserAnglePan;
	}

	public void setLaserAnglePan(int laserAnglePan) {
		this.laserAnglePan = laserAnglePan;
	}

	public int getLaserAngleTilt() {
		return this.laserAngleTilt;
	}

	public void setLaserAngleTilt(int laserAngleTilt) {
		this.laserAngleTilt = laserAngleTilt;
	}

	public int getLaserDelayBtwnTrips() {
		return this.laserDelayBtwnTrips;
	}

	public void setLaserDelayBtwnTrips(int laserDelayBtwnTrips) {
		this.laserDelayBtwnTrips = laserDelayBtwnTrips;
	}

	public int getLaserDistanceThresFeet() {
		return this.laserDistanceThresFeet;
	}

	public void setLaserDistanceThresFeet(int laserDistanceThresFeet) {
		this.laserDistanceThresFeet = laserDistanceThresFeet;
	}

	public int getLaserDistanceThresInches() {
		return this.laserDistanceThresInches;
	}

	public void setLaserDistanceThresInches(int laserDistanceThresInches) {
		this.laserDistanceThresInches = laserDistanceThresInches;
	}

	public int getMailAlertOn() {
		return this.mailAlertOn;
	}

	public void setMailAlertOn(int mailAlertOn) {
		this.mailAlertOn = mailAlertOn;
	}

	public int getMultiAlarmTripState() {
		return this.multiAlarmTripState;
	}

	public void setMultiAlarmTripState(int multiAlarmTripState) {
		this.multiAlarmTripState = multiAlarmTripState;
	}

	public int getMwAnglePan() {
		return this.mwAnglePan;
	}

	public void setMwAnglePan(int mwAnglePan) {
		this.mwAnglePan = mwAnglePan;
	}

	public int getMwDelayBtwnTrips() {
		return this.mwDelayBtwnTrips;
	}

	public void setMwDelayBtwnTrips(int mwDelayBtwnTrips) {
		this.mwDelayBtwnTrips = mwDelayBtwnTrips;
	}

	public int getMwSpeedThresCyclesPerSec() {
		return this.mwSpeedThresCyclesPerSec;
	}

	public void setMwSpeedThresCyclesPerSec(int mwSpeedThresCyclesPerSec) {
		this.mwSpeedThresCyclesPerSec = mwSpeedThresCyclesPerSec;
	}

	public int getPirDelayBtwnTrips() {
		return this.pirDelayBtwnTrips;
	}

	public void setPirDelayBtwnTrips(int pirDelayBtwnTrips) {
		this.pirDelayBtwnTrips = pirDelayBtwnTrips;
	}

	public int getSonarDelayBtwnTrips() {
		return this.sonarDelayBtwnTrips;
	}

	public void setSonarDelayBtwnTrips(int sonarDelayBtwnTrips) {
		this.sonarDelayBtwnTrips = sonarDelayBtwnTrips;
	}

	public int getSonarDistanceThresFeet() {
		return this.sonarDistanceThresFeet;
	}

	public void setSonarDistanceThresFeet(int sonarDistanceThresFeet) {
		this.sonarDistanceThresFeet = sonarDistanceThresFeet;
	}

	public int getSonarDistanceThresInches() {
		return this.sonarDistanceThresInches;
	}

	public void setSonarDistanceThresInches(int sonarDistanceThresInches) {
		this.sonarDistanceThresInches = sonarDistanceThresInches;
	}

	public int getSonarPirAnglePan() {
		return this.sonarPirAnglePan;
	}

	public void setSonarPirAnglePan(int sonarPirAnglePan) {
		this.sonarPirAnglePan = sonarPirAnglePan;
	}

	public int getSonarPirAngleTilt() {
		return this.sonarPirAngleTilt;
	}

	public void setSonarPirAngleTilt(int sonarPirAngleTilt) {
		this.sonarPirAngleTilt = sonarPirAngleTilt;
	}

	public int getUniversalRemoteAccessCode1() {
		return this.universalRemoteAccessCode1;
	}

	public void setUniversalRemoteAccessCode1(int universalRemoteAccessCode1) {
		this.universalRemoteAccessCode1 = universalRemoteAccessCode1;
	}

	public int getUniversalRemoteAccessCode2() {
		return this.universalRemoteAccessCode2;
	}

	public void setUniversalRemoteAccessCode2(int universalRemoteAccessCode2) {
		this.universalRemoteAccessCode2 = universalRemoteAccessCode2;
	}

	public int getUniversalRemoteAccessCode3() {
		return this.universalRemoteAccessCode3;
	}

	public void setUniversalRemoteAccessCode3(int universalRemoteAccessCode3) {
		this.universalRemoteAccessCode3 = universalRemoteAccessCode3;
	}

	public int getUniversalRemoteAccessOn() {
		return this.universalRemoteAccessOn;
	}

	public void setUniversalRemoteAccessOn(int universalRemoteAccessOn) {
		this.universalRemoteAccessOn = universalRemoteAccessOn;
	}

	public String getWorkingDir() {
		return this.workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}

	public int getAlarmsOn() {
		return alarmsOn;
	}

	public void setAlarmsOn(int alarmsOn) {
		this.alarmsOn = alarmsOn;
	}

	public int getReportReadings() {
		return reportReadings;
	}

	public void setReportReadings(int reportReadings) {
		this.reportReadings = reportReadings;
	}

	public boolean isDeviceAutoSynchronize() {
		return deviceAutoSynchronize;
	}

	public void setDeviceAutoSynchronize(boolean deviceAutoSynchronize) {
		this.deviceAutoSynchronize = deviceAutoSynchronize;
	}

	public boolean isDeviceSynchronized() {
		return deviceSynchronized;
	}

	public void setDeviceSynchronized(boolean deviceSynchronized) {
		this.deviceSynchronized = deviceSynchronized;
	}

	public Host getHost() {
		return this.host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public Set<RemoteNodeReading> getRemoteNodeReadings() {
		return this.remoteNodeReadings;
	}

	public void setRemoteNodeReadings(Set<RemoteNodeReading> remoteNodeReadings) {
		this.remoteNodeReadings = remoteNodeReadings;
	}
}