package org.ugate.service.entity.jpa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.ugate.service.entity.Model;


/**
 * The persistent class for the REMOTE_NODE_READING database table.
 * 
 */
@Entity
@Table(name="REMOTE_NODE_READING")
public class RemoteNodeReading implements Model {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="REMOTE_NODE_RD_ID_GENERATOR", sequenceName="SQ_REMOTE_NODE_RD_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REMOTE_NODE_RD_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private int id;

	@Min(0)
	@Max(15)
	@Column(name="FROM_MULTI_STATE")
	private int fromMultiState;

	@Min(0)
	@Max(1)
	@Column(name="GATE_STATE")
	private int gateState;

	@Min(0)
	@Column(name="LASER_FEET")
	private int laserFeet;

	@Min(0)
	@Max(11)
	@Column(name="LASER_INCHES")
	private int laserInches;

	@Min(0)
	@Column(name="MICROWAVE_CYCLE_COUNT")
	private int microwaveCycleCount;

	@Min(0)
	@Column(name="PIR_INTENSITY")
	private int pirIntensity;

	@Column(name="READ_DATE", nullable=false)
	private Date readDate;

	@Min(0)
	@Column(name="SONAR_FEET")
	private int sonarFeet;

	@Min(0)
	@Max(11)
	@Column(name="SONAR_INCHES")
	private int sonarInches;

	//bi-directional many-to-one association to RemoteNode
	@ManyToOne
	@JoinColumn(name="REMOTE_NODE_ID")
	private RemoteNode remoteNode;

	public RemoteNodeReading() {
	}

	@Override
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFromMultiState() {
		return fromMultiState;
	}

	public void setFromMultiState(int fromMultiState) {
		this.fromMultiState = fromMultiState;
	}

	public int getGateState() {
		return this.gateState;
	}

	public void setGateState(int gateState) {
		this.gateState = gateState;
	}

	public int getLaserFeet() {
		return this.laserFeet;
	}

	public void setLaserFeet(int laserFeet) {
		this.laserFeet = laserFeet;
	}

	public int getLaserInches() {
		return this.laserInches;
	}

	public void setLaserInches(int laserInches) {
		this.laserInches = laserInches;
	}

	public int getMicrowaveCycleCount() {
		return this.microwaveCycleCount;
	}

	public void setMicrowaveCycleCount(int microwaveCycleCount) {
		this.microwaveCycleCount = microwaveCycleCount;
	}

	public int getPirIntensity() {
		return this.pirIntensity;
	}

	public void setPirIntensity(int pirIntensity) {
		this.pirIntensity = pirIntensity;
	}

	public Date getReadDate() {
		return this.readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}

	public int getSonarFeet() {
		return this.sonarFeet;
	}

	public void setSonarFeet(int sonarFeet) {
		this.sonarFeet = sonarFeet;
	}

	public int getSonarInches() {
		return this.sonarInches;
	}

	public void setSonarInches(int sonarInches) {
		this.sonarInches = sonarInches;
	}

	public RemoteNode getRemoteNode() {
		return this.remoteNode;
	}

	public void setRemoteNode(RemoteNode remoteNode) {
		this.remoteNode = remoteNode;
	}

	/**
	 * @return the distance that the sonar was read at (in <code>meters</code>)
	 */
	//@Transient
	public double getSonarMeters() {
		return ((getSonarFeet() * 12d) + getSonarInches()) * 0.0254d;
	}

	/**
	 * @param laserMeters
	 *            the distance that the laser was read at (in
	 *            <code>meters</code>)
	 */
	//@Transient
	public void setLaserMeters(final double laserMeters) {
		final double totalInches = (laserMeters * 100d) / 2.54d;
		final int feet = (int) java.lang.Math.floor(totalInches / 12.0d);
		setLaserFeet(feet);
		setLaserInches((int) (totalInches - feet * 12.0));
	}

	/**
	 * @return the distance that the sonar was read at (in <code>meters</code>)
	 */
	//@Transient
	public double getLaserMeters() {
		return ((getLaserFeet() * 12d) + getLaserInches()) * 0.0254d;
	}

	/**
	 * @param sonarMeters
	 *            the distance that the sonar was read at (in
	 *            <code>meters</code>)
	 */
	//@Transient
	public void setSonarMeters(final double sonarMeters) {
		final double totalInches = (sonarMeters * 100d) / 2.54d;
		final int feet = (int) java.lang.Math.floor(totalInches / 12.0d);
		setSonarFeet(feet);
		setSonarInches((int) (totalInches - feet * 12.0));
	}

	/**
	 * @return the speed clocked when the microwave sensor was read (mm/second) 
	 * 		(calculated from {@link #getMicrowaveCycleCount()}
	 */
	//@Transient
	public long getMicrowaveSpeedMillimetersPerSec() {
		return getMicrowaveCycleCount() * 30000 / 2105;
	}

	/**
	 * 
	 * @param microwaveSpeedMillimetersPerSec
	 *            the speed clocked when the microwave sensor was read
	 *            (mm/second) (calculated from {@link #getMicrowaveCycleCount()}
	 */
	//@Transient
	public void setMicrowaveSpeedMillimetersPerSec(
			final long microwaveSpeedMillimetersPerSec) {
		setMicrowaveCycleCount((int) (microwaveSpeedMillimetersPerSec / 30000) * 2105);
	}

	/**
	 * @return the speed clocked when the microwave sensor was read
	 *         (inches/second) (calculated from
	 *         {@linkplain #getMicrowaveCycleCount()}
	 */
	//@Transient
	public long getMicrowaveSpeedInchesPerSec() {
		return getMicrowaveCycleCount() * 30000 / 53467;
	}

	/**
	 * @param microwaveSpeedInchesPerSec
	 *            the speed clocked when the microwave sensor was read
	 *            (inches/second) (calculated from
	 *            {@linkplain #getMicrowaveCycleCount()}
	 */
	//@Transient
	public void setMicrowaveSpeedInchesPerSec(final long microwaveSpeedInchesPerSec) {
		setMicrowaveCycleCount((int) (microwaveSpeedInchesPerSec / 30000) * 53467);
	}

	/**
	 * @return the speed clocked when the microwave sensor was read (miles/hour) 
	 * 		(calculated from {@link #getMicrowaveCycleCnt()}
	 */
	//@Transient
	public double getMicrowaveSpeedMPH() {
		return getMicrowaveSpeedInchesPerSec() * 30000d / 53467d;
	}

	/**
	 * @param microwaveSpeedMPH
	 *            the speed clocked when the microwave sensor was read
	 *            (miles/hour) (calculated from {@link #getMicrowaveCycleCnt()}
	 */
	//@Transient
	public void setMicrowaveSpeedMPH(final double microwaveSpeedMPH) {
		setMicrowaveCycleCount((int) ((microwaveSpeedMPH / 30000d) * 53467d));
	}
}