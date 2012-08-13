package org.ugate.service.entity.jpa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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

	@Column(name = "CREATED_DATE")
	private Date createdDate;

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

	@Override
	@Transient
	public int getId() {
		return -1;
	}
}
