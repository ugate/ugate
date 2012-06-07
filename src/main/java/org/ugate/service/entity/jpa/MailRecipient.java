package org.ugate.service.entity.jpa;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.ugate.service.entity.Model;

/**
 * The persistent class for the ACTOR database table.
 * 
 */
@Entity
@Table(name="MAIL_RECIPIENT")
public class MailRecipient implements Model {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="MAIL_RECIPIENT_ID_GENERATOR", sequenceName="MAIL_RECIPIENT_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MAIL_RECIPIENT_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private int id;

	@Column(unique=true, nullable=false, length=100)
	private String email;
	
	@Column(name="FIRST_NAME", length=100)
	private String firstName;
	
	@Column(name="LAST_NAME", length=100)
	private String lastName;
	
	//bi-directional many-to-many association to Actor
	@ManyToMany(mappedBy="mailRecipients")
	private Set<Host> hosts;
	
	public MailRecipient() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Set<Host> getHosts() {
		return hosts;
	}

	public void setHosts(Set<Host> hosts) {
		this.hosts = hosts;
	}

}
