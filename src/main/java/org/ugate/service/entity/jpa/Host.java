package org.ugate.service.entity.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the HOST database table.
 * 
 */
@Entity
@Table(name="HOST")
public class Host implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id;

	@Column(name="COM_ADDRESS", length=100)
	private String comAddress;

	@Column(name="COM_BAUD")
	private int comBaud;

	@Column(name="COM_PORT", length=50)
	private String comPort;

	@Column(name="MAIL_IMAP_HOST", length=100)
	private String mailImapHost;

	@Column(name="MAIL_IMAP_PORT")
	private int mailImapPort;

	@Column(name="MAIL_INBOX_NAME", length=100)
	private String mailInboxName;

	@Column(name="MAIL_PASSWORD", length=100)
	private String mailPassword;

	@Column(name="MAIL_SMTP_HOST", length=100)
	private String mailSmtpHost;

	@Column(name="MAIL_SMTP_PORT")
	private int mailSmtpPort;

	@Column(name="MAIL_USER_NAME", length=100)
	private String mailUserName;

	@Column(name="USE_METRIC", nullable=false)
	private boolean useMetric;

	//bi-directional many-to-one association to Actor
	@OneToMany(mappedBy="host")
	private Set<Actor> actors;

    public Host() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComAddress() {
		return this.comAddress;
	}

	public void setComAddress(String comAddress) {
		this.comAddress = comAddress;
	}

	public int getComBaud() {
		return this.comBaud;
	}

	public void setComBaud(int comBaud) {
		this.comBaud = comBaud;
	}

	public String getComPort() {
		return this.comPort;
	}

	public void setComPort(String comPort) {
		this.comPort = comPort;
	}

	public String getMailImapHost() {
		return this.mailImapHost;
	}

	public void setMailImapHost(String mailImapHost) {
		this.mailImapHost = mailImapHost;
	}

	public int getMailImapPort() {
		return this.mailImapPort;
	}

	public void setMailImapPort(int mailImapPort) {
		this.mailImapPort = mailImapPort;
	}

	public String getMailInboxName() {
		return this.mailInboxName;
	}

	public void setMailInboxName(String mailInboxName) {
		this.mailInboxName = mailInboxName;
	}

	public String getMailPassword() {
		return this.mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	public String getMailSmtpHost() {
		return this.mailSmtpHost;
	}

	public void setMailSmtpHost(String mailSmtpHost) {
		this.mailSmtpHost = mailSmtpHost;
	}

	public int getMailSmtpPort() {
		return this.mailSmtpPort;
	}

	public void setMailSmtpPort(int mailSmtpPort) {
		this.mailSmtpPort = mailSmtpPort;
	}

	public String getMailUserName() {
		return this.mailUserName;
	}

	public void setMailUserName(String mailUserName) {
		this.mailUserName = mailUserName;
	}

	public boolean getUseMetric() {
		return this.useMetric;
	}

	public void setUseMetric(boolean useMetric) {
		this.useMetric = useMetric;
	}

	public Set<Actor> getActors() {
		return this.actors;
	}

	public void setActors(Set<Actor> actors) {
		this.actors = actors;
	}
	
}