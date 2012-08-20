package org.ugate.service.entity;

import org.ugate.service.entity.jpa.Role;

/**
 * {@linkplain Role} types
 */
public enum RoleType {
	ADMIN;
	
	/**
	 * @return the names of {@linkplain RoleType}
	 */
	public static String[] names() {
		final String[] names = new String[RoleType.values().length];
		int i = -1;
		for (final RoleType rt : RoleType.values()) {
			names[++i] = rt.name();
		}
		return names;
	}

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
