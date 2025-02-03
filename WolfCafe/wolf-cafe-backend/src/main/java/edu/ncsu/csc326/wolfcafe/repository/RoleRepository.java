package edu.ncsu.csc326.wolfcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Role;

/**
 * Repository interface for Roles.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
	
    /** Find role by its name
     * @param name
     * 			name of role to find
     * @return role with given name
     */
    Role findByName ( String name );
}
