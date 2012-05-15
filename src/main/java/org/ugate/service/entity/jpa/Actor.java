package org.ugate.service.entity.jpa;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Set;


/**
 * The persistent class for the ACTOR database table.
 * 
 */
@Entity
@Table(name="ACTOR")
public class Actor implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ACTOR_ID_GENERATOR", sequenceName="ACTOR_ID")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ACTOR_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private int id;

	@Column(unique=true, nullable=false, length=100)
	private String login;

	@Column(nullable=false, length=64)
	private String pwd;
	
	//bi-directional many-to-one association to Host
    @ManyToOne
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