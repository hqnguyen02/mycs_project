package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Role;

/**
 * Authorization service
 */
public interface AuthService {
    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    String register ( RegisterDto registerDto );

    /**
     * Registers the given user with a staff role
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    String addStaff ( RegisterDto registerDto );
    
    /**
     * Registers the given user with a manager role
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    String addManager ( RegisterDto registerDto );

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    JwtAuthResponse login ( LoginDto loginDto );

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete

     */
    void deleteUserById ( Long id );

    /**
     * Edits the given user
     *
     * @param id
     *            the id of the user to edit
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    String editUserById ( Long id, RegisterDto registerDto );

    /**
     * Returns a list of all users in the system
     *
     * @return a list of all users in the system
     */
    List<UserDto> getAllUsers ();

    /**
     * Returns a specified user in the system
     *
     * @param id
     *            the id of the user to return
     * @return the user with the given id
     */
    UserDto getUserById ( Long id );
    
    /**
     * Edits the given customer, replacing their registered data accordingly.
     * 
     * @param username the username of the customer to edit.
     * @param registerDto the data to replace the current data with.
     * @return message for success or failure.
     */
	String editCustomer(String username, RegisterDto registerDto);

	/**
	 * Deletes the customer with the passed ID if they are logged in.
	 * 
	 * @param username username of customer to delete.
	 */
	void deleteCustomer(String username);

	/**
	 * Gets a user's ID from their username.
	 * @param username the username of the user.
	 * @return the ID of the user.
	 */
	public Long getId(String username);

	/**
	 * Gets the currently logged in user from their ID.
	 * @param id id of the customer.
	 * @return the UserDto for that cusotmer.
	 */
	UserDto getCustomerById(Long id);
    
    /**
     * Returns a list of all users with a given role in the system
     * 
     * @param role
     * 			role to search for
     *
     * @return a list of all users with the given role in the system
     */
    List<UserDto> getUsersByRole (Role role);
}
