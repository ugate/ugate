package org.ugate.service.entity.jpa;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.ugate.UGateEvent;
import org.ugate.UGateEvent.Type;
import org.ugate.UGateKeeper;
import org.ugate.service.entity.Email;
import org.ugate.service.entity.Model;


/**
 * The persistent class for the ACTOR database table.
 * 
 */
@Entity
@Table(name="ACTOR")
public class Actor implements Model {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ACTOR_ID_GENERATOR", sequenceName="ACTOR_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ACTOR_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private int id;

	@Column(unique=true, nullable=false, length=100)
	@Email
	@NotNull
	private String username;

	@Column(nullable=false, length=64)
	@Size(min=4, max=64)
	private String password;

	@Column(nullable=false, length=1024)
	@Size(min=32, max=1024)
	private String passPhrase;
	
	//bi-directional many-to-one association to Host
    @ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="HOST_ID", nullable=false)
    @NotNull
	private Host host;

	//bi-directional many-to-one association to AppInfo
	@OneToMany(mappedBy="defaultActor")
	private Set<AppInfo> appInfos;

	//bi-directional many-to-many association to Role
    @ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinTable(
		name="ACTOR_ROLE"
		, joinColumns={
			@JoinColumn(name="ACTOR_ID", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="ROLE_ID", nullable=false)
			}
		)
	private Set<Role> roles;

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
	 *            the old {@linkplain Actor}
	 * @param newValue
	 *            the new {@linkplain Actor}
	 */
	private void notifyListeners(final boolean isPre, final Actor oldValue,
			final Actor newValue) {
		UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(this,
				isPre ? Type.ACTOR_COMMIT : Type.ACTOR_COMMITTED, false,
				oldValue, newValue));
	}

    public Actor() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}

	public Host getHost() {
		return this.host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public Set<AppInfo> getAppInfos() {
		return appInfos;
	}

	public void setAppInfos(Set<AppInfo> appInfos) {
		this.appInfos = appInfos;
	}

	public Set<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
}