package org.ugate.service.entity.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the ROLE database table.
 * 
 */
@Entity
@Table(name="ROLE")
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ROLE_ID_GENERATOR", sequenceName="ROLE_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ROLE_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private int id;

	@Column(nullable=false, length=100)
	private String role;

	//bi-directional many-to-many association to Actor
	@ManyToMany(mappedBy="roles")
	private Set<Actor> actors;

    public Role() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Set<Actor> getActors() {
		return this.actors;
	}

	public void setActors(Set<Actor> actors) {
		this.actors = actors;
	}
	
}