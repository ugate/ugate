package org.ugate.service.entity.jpa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.ugate.service.entity.Model;

/**
 * The persistent class for the APP_INFO database table.
 */
@Entity
@Table(name = "APP_INFO", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"CREATED_DATE", "version" }) })
public class AppInfo implements Model {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(nullable = false, length = 20)
	private String version;

	@Column(name = "CREATED_DATE", nullable=false)
	private Date createdDate;

	//bi-directional many-to-one association to Actor
    @ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DEFAULT_ACTOR_ID", nullable=true)
	private Actor defaultActor;

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Actor getDefaultActor() {
		return defaultActor;
	}

	public void setDefaultActor(Actor defaultActor) {
		this.defaultActor = defaultActor;
	}

	@Override
	@Transient
	public int getId() {
		return -1;
	}
}
