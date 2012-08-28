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
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.ugate.UGateKeeper;
import org.ugate.UGateEvent;
import org.ugate.UGateEvent.Type;
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
	private String login;

	@Column(nullable=false, length=64)
	private String pwd;
	
	//bi-directional many-to-one association to Host
    @ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="HOST_ID", nullable=false)
	private Host host;

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
	 * Call {@linkplain UGateKeeper#notifyListeners(UGateEvent)} when any
	 * changes are committed
	 */
	@PostPersist
	@PostUpdate
	@PostRemove
	void notifyListeners() {
		UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(this,
				Type.ACTOR_COMMITTED, false));
	}

    public Actor() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPwd() {
		return this.pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public Host getHost() {
		return this.host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public Set<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
}