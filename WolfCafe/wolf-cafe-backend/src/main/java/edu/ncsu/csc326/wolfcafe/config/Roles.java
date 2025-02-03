package edu.ncsu.csc326.wolfcafe.config;

/**
 * Defines user roles for WolfCafe
 */
public class Roles {
	
	/** Admin role name */
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	
	/**
	 * Defines all roles in the system, EXCEPT for the Admin role.
	 */
	public enum UserRoles {
		
		/** Staff for WolfCafe - can create items and fulfill orders */
		ROLE_STAFF,
		/** Customer for WolfCafe */
		ROLE_CUSTOMER,
		/** Manager for WolfCafe - can manage staff and inventory */
		ROLE_MANAGER

	}
	
}
