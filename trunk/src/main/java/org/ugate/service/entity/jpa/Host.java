package org.ugate.service.entity.jpa;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.ugate.UGateEvent;
import org.ugate.UGateEvent.Type;
import org.ugate.UGateKeeper;
import org.ugate.service.entity.Email;
import org.ugate.service.entity.Model;


/**
 * The persistent class for the HOST database table.
 * 
 */
@Entity
@Table(name="HOST")
public class Host implements Model {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="HOST_ID_GENERATOR", sequenceName="HOST_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="HOST_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private int id;

	@Min(0)
	@Max(1)
	@Column(name="COM_ON_AT_APP_STARTUP", nullable=false)
	private int comOnAtAppStartup;

	@Column(name="COM_ADDRESS", length=100)
	private String comAddress;

	@Column(name="COM_BAUD")
	@Digits(integer=6, fraction=0)
	private int comBaud;

	@Column(name="COM_PORT", length=50)
	private String comPort;

	@Min(0)
	@Max(1)
	@Column(name="MAIL_ON_AT_COM_STARTUP", nullable=false)
	private int mailOnAtComStartup;

	@Column(name="MAIL_IMAP_HOST", length=100)
	private String mailImapHost;

	@Min(0)
	@Column(name="MAIL_IMAP_PORT")
	private int mailImapPort;

	@Column(name="MAIL_INBOX_NAME", length=100)
	private String mailInboxName;

	@Column(name="MAIL_PASSWORD", length=100)
	@Size(min=3, max=30)
	private String mailPassword;

	@Column(name="MAIL_SMTP_HOST", length=100)
	private String mailSmtpHost;

	@Min(0)
	@Column(name="MAIL_SMTP_PORT")
	private int mailSmtpPort;

	@Email
	@Column(name="MAIL_USER_NAME", length=100)
	private String mailUserName;
	
	@Column(name="MAIL_USE_SSL")
	private int mailUseSSL;

	@Column(name="MAIL_USE_TLS")
	private int mailUseTLS;

	@Column(name="USE_METRIC", nullable=false)
	private boolean useMetric;

	@Min(0)
	@Max(1)
	@Column(name="WEB_ON_AT_COM_STARTUP", nullable=false)
	private int webOnAtComStartup;

	@Column(name="WEB_HOST")
	private String webHost;

	@Min(0)
	@Column(name="WEB_PORT")
	private int webPort;

	@Column(name="WEB_HOST_LOCAL")
	private String webHostLocal;

	@Min(0)
	@Column(name="WEB_PORT_LOCAL")
	private int webPortLocal;

	@Lob
	@Basic(fetch=FetchType.EAGER)
	@Column(name="WEB_KEY_STORE")
	private byte[] webKeyStore;

	//bi-directional many-to-one association to Actor
	@OneToMany(mappedBy="host")
	private Set<Actor> actors;
	
	//bi-directional many-to-many association to Role
    @ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinTable(
		name="HOST_MAIL_RECIPIENT"
		, joinColumns={
			@JoinColumn(name="HOST_ID", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="MAIL_RECIPIENT_ID", nullable=false)
			}
		)
    @OrderBy("email")
	private LinkedHashSet<MailRecipient> mailRecipients;
    
	//bi-directional many-to-one association to RemoteNode
	@OneToMany(mappedBy="host", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private LinkedHashSet<RemoteNode> remoteNodes;

	/**
	 * Call {@linkplain UGateKeeper#notifyListeners(UGateEvent)} for
	 * {@linkplain PrePersist}
	 */
	@PrePersist
	void notifyListenersPrePresist() {
		notifyListeners(true, null, this);
	}

	/**
	 * Call {@linkplain UGateKeeper#notifyListeners(UGateEvent)} for
	 * {@linkplain PostPersist}
	 */
	@PostPersist
	void notifyListenersPostPersist() {
		notifyListeners(false, null, this);
	}

	/**
	 * Call {@linkplain UGateKeeper#notifyListeners(UGateEvent)} for
	 * {@linkplain PreUpdate}
	 */
	@PreUpdate
	void notifyListenersPreUpdate() {
		notifyListeners(true, this, this);
	}

	/**
	 * Call {@linkplain UGateKeeper#notifyListeners(UGateEvent)} for
	 * {@linkplain PostUpdate}
	 */
	@PostUpdate
	void notifyListenersPostUpdate() {
		notifyListeners(false, this, this);
	}

	/**
	 * Call {@linkplain UGateKeeper#notifyListeners(UGateEvent)} for
	 * {@linkplain PreRemove}
	 */
	@PreRemove
	void notifyListenersPreRemove() {
		notifyListeners(true, this, null);
	}

	/**
	 * Call {@linkplain UGateKeeper#notifyListeners(UGateEvent)} for
	 * {@linkplain PostRemove}
	 */
	@PostRemove
	void notifyListenersPostRemove() {
		notifyListeners(false, this, null);
	}

	/**
	 * Call {@linkplain UGateKeeper#notifyListeners(UGateEvent)} for
	 * {@linkplain PrePersist}, {@linkplain PostPersist}, {@linkplain PreUpdate}
	 * , {@linkplain PostUpdate}, {@linkplain PreRemove}, and
	 * {@linkplain PostRemove}
	 * 
	 * @param isPre
	 *            true when prior to operation, false for after
	 * @param oldValue
	 *            the old {@linkplain Host}
	 * @param newValue
	 *            the new {@linkplain Host}
	 */
	private void notifyListeners(final boolean isPre, final Host oldValue,
			final Host newValue) {
		UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(this,
				isPre ? Type.HOST_COMMIT : Type.HOST_COMMITTED, false,
				oldValue, newValue));
	}

    public Host() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getComOnAtAppStartup() {
		return comOnAtAppStartup;
	}

	public void setComOnAtAppStartup(int comOnAtAppStartup) {
		this.comOnAtAppStartup = comOnAtAppStartup;
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

	public int getMailOnAtComStartup() {
		return mailOnAtComStartup;
	}

	public void setMailOnAtComStartup(int mailOnAtComStartup) {
		this.mailOnAtComStartup = mailOnAtComStartup;
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

	public int getMailUseSSL() {
		return mailUseSSL;
	}

	public void setMailUseSSL(int mailUseSSL) {
		this.mailUseSSL = mailUseSSL;
	}

	public int getMailUseTLS() {
		return mailUseTLS;
	}

	public void setMailUseTLS(int mailUseTLS) {
		this.mailUseTLS = mailUseTLS;
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

	public LinkedHashSet<MailRecipient> getMailRecipients() {
		return mailRecipients;
	}

	public void setMailRecipients(LinkedHashSet<MailRecipient> mailRecipients) {
		this.mailRecipients = mailRecipients;
	}

	public int getWebOnAtComStartup() {
		return webOnAtComStartup;
	}

	public void setWebOnAtComStartup(int webOnAtComStartup) {
		this.webOnAtComStartup = webOnAtComStartup;
	}

	public String getWebHost() {
		return webHost;
	}

	public void setWebHost(String webHost) {
		this.webHost = webHost;
	}

	public int getWebPort() {
		return webPort;
	}

	public void setWebPort(int webPort) {
		this.webPort = webPort;
	}

	public String getWebHostLocal() {
		return webHostLocal;
	}

	public void setWebHostLocal(String webHostLocal) {
		this.webHostLocal = webHostLocal;
	}

	public int getWebPortLocal() {
		return webPortLocal;
	}

	public void setWebPortLocal(int webPortLocal) {
		this.webPortLocal = webPortLocal;
	}

	public byte[] getWebKeyStore() {
		return webKeyStore;
	}

	public void setWebKeyStore(byte[] webKeyStore) {
		this.webKeyStore = webKeyStore;
	}

	public LinkedHashSet<RemoteNode> getRemoteNodes() {
		return remoteNodes;
	}

	public void setRemoteNodes(LinkedHashSet<RemoteNode> remoteNodes) {
		this.remoteNodes = remoteNodes;
	}
	
}