package org.ugate.service;

import org.ugate.service.entity.jpa.Role;

/**
 * Role types
 */
public enum RoleType {
	ADMIN;

	/**
	 * @return a <code>new</code> {@linkplain Role} representation of the
	 *         {@linkplain RoleType}
	 */
	public Role newRole() {
		final Role role = new Role();
		role.setRole(this.name());
		return role;
	}
}
