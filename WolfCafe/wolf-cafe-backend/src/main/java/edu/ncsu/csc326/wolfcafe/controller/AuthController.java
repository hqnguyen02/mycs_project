package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Controller for authentication functionality.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/auth" )
@AllArgsConstructor
public class AuthController {

    /** Link to AuthService */
    private final AuthService authService;
    
    /** Role repository */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Registers a new customer user with the system.
     *
     * @param registerDto
     *            object with registration info
     * @return response indicating success or failure
     */
    @PostMapping ( "/register" )
    public ResponseEntity<String> register ( @RequestBody final RegisterDto registerDto ) {

        // Make sure that the customer is registering as a customer. Since
        // anyone can use this endpoint, don't want to allow staff or admins to
        // be made using this endpoint.
        if ( !registerDto.getRole().toUpperCase().equals( "CUSTOMER" ) ) {
            return new ResponseEntity<String>( "You can only register as a customer through this API endpoint.",
                    HttpStatus.BAD_REQUEST );
        }
        final String response = authService.register( registerDto );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Logs in the given user
     *
     * @param loginDto
     *            user information for login
     * @return object representing the logged in user
     */
    @PostMapping ( "/login" )
    public ResponseEntity<JwtAuthResponse> login ( @RequestBody final LoginDto loginDto ) {
        final JwtAuthResponse jwtAuthResponse = authService.login( loginDto );
        return new ResponseEntity<>( jwtAuthResponse, HttpStatus.OK );
    }
    
    /**
     * Gets the customer's ID from their username.
     *
     * @param username
     *            the username of the customer whose ID should be returned.
     * @return the user's ID.
     */
   @GetMapping ( "/customer-id/{username}" )
    public ResponseEntity<Long> getCustomerId ( @PathVariable ("username") final String username ) {
        return ResponseEntity.ok( authService.getId(username) );
    }

    /**
     * Deletes the given user. Requires the ADMIN role.
     *
     * @param id
     *            id of user to delete
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @DeleteMapping ( "/user/{id}" )
    public ResponseEntity<String> deleteUser ( @PathVariable ( "id" ) final Long id ) {
        authService.deleteUserById( id );
        return ResponseEntity.ok( "User deleted successfully." );
    }
    
    /**
     * Deletes the given customer. Requires the CUSTOMER role.
     *
     * @param username
     *            username of customer to delete
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('CUSTOMER')" )
    @DeleteMapping ( "/delete-customer" )
    public ResponseEntity<String> deleteCustomer ( @CurrentSecurityContext(expression = "authentication?.name") String username ) {
        authService.deleteCustomer( username );
        return ResponseEntity.ok( "Customer deleted successfully." );
    }

    /**
     * Registers a new staff user with the system. Requires MANAGER or ADMIN role.
     *
     * @param registerDto
     *            object with staff registration info
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasAnyRole('MANAGER', 'ADMIN')" )
    @PostMapping ( "/register/staff" )
    public ResponseEntity<String> addStaff ( @RequestBody final RegisterDto registerDto ) {
        final String response = authService.addStaff( registerDto );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }
    
    /**
     * Registers a new manager user with the system. Requires ADMIN role.
     *
     * @param registerDto
     *            object with manager registration info
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PostMapping ( "/register/manager" )
    public ResponseEntity<String> addManager ( @RequestBody final RegisterDto registerDto ) {
        final String response = authService.addManager( registerDto );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Edits the given user. Requires the ADMIN role.
     *
     * @param id
     *            id of user to edit
     * @param registerDto
     *            the new information of the user
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PutMapping ( "/user/{id}" )
    public ResponseEntity<String> editUser ( @PathVariable ( "id" ) final Long id,
            @RequestBody final RegisterDto registerDto ) {
        return ResponseEntity.ok( authService.editUserById( id, registerDto ) );
    }
    
    /**
     * Edits the given customer. Requires the CUSTOMER role.
     *
     * @param username
     * 			username of the authenticated user
     * @param registerDto
     *            the new information of the customer
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('CUSTOMER')" )
    @PutMapping ( "/edit-customer" )
    public ResponseEntity<String> editCustomer (@CurrentSecurityContext(expression = "authentication?.name") String username, 
    		@RequestBody final RegisterDto registerDto ) { 
        return ResponseEntity.ok( authService.editCustomer( username, registerDto ) );
    }
    
    /**
     * Returns a list of users in the system. Requires the ADMIN role.
     *
     * @return List of users in the system
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @GetMapping ( "/all-users" )
    public ResponseEntity<List<UserDto>> getAllUsers () {
        return ResponseEntity.ok( authService.getAllUsers() );
    }
    
    /**
     * Returns a list of staff in the system. Requires the MANAGER role.
     *
     * @return List of staff in the system
     */
    @PreAuthorize ( "hasRole('MANAGER')" )
    @GetMapping ( "/get-staff" )
    public ResponseEntity<List<UserDto>> getStaffList () {
        return ResponseEntity.ok( authService.getUsersByRole(roleRepository.findByName("ROLE_STAFF")) );
    }

    /**
     * Returns a specified user in the system. Requires the ADMIN role.
     *
     * @param id
     *            id of user to retrieve
     * @return List of users in the system
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @GetMapping ( "/user/{id}" )
    public ResponseEntity<UserDto> getUser ( @PathVariable ( "id" ) final Long id ) {
        return ResponseEntity.ok( authService.getUserById( id ) );
    }
    
    /**
     * Returns the currently logged in customer. Requires the CUSTOMER role.
     *
     * @param id
     *            id of customer to retrieve
     * @return Customer with that ID (UserDto)
     */
    @PreAuthorize ( "hasRole('CUSTOMER')" )
    @GetMapping ( "/customer/{id}" )
    public ResponseEntity<UserDto> getCustomer ( @PathVariable ( "id" ) final Long id ) {
        return ResponseEntity.ok( authService.getCustomerById( id ) );
    }
        
    /**
     * Returns a specified staff user in the system. Requires the MANAGER role.
     *
     * @param id
     *            id of user to retrieve
     * @return List of users in the system
     */
    @PreAuthorize ( "hasRole('MANAGER')" )
    @GetMapping ( "/staff/{id}" )
    public ResponseEntity<UserDto> getStaff ( @PathVariable ( "id" ) final Long id ) {
    	if(authService.getUserById(id).getRoles().contains("Staff")) {
    		return ResponseEntity.ok( authService.getUserById( id ) );
    	}
    	return new ResponseEntity<UserDto>(HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Edits the given staff member. Requires the MANAGER role.
     *
     * @param id
     *            id of user to delete
     * @param registerDto
     *            the new information of the user
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('MANAGER')" )
    @PutMapping ( "/staff/{id}" )
    public ResponseEntity<String> editStaff ( @PathVariable ( "id" ) final Long id,
            @RequestBody final RegisterDto registerDto ) {
    	if(authService.getUserById(id).getRoles().contains("Staff")) {
    		return ResponseEntity.ok( authService.editUserById( id, registerDto ) );
    	}
    	return new ResponseEntity<String>("Only admins can edit non-staff users.", HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Deletes the given user. Requires the MANAGER role.
     *
     * @param id
     *            id of user to delete
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('MANAGER')" )
    @DeleteMapping ( "/staff/{id}" )
    public ResponseEntity<String> deleteStaff ( @PathVariable ( "id" ) final Long id ) {
    	if(authService.getUserById(id).getRoles().contains("Staff")) {
    		authService.deleteUserById( id );
            return ResponseEntity.ok( "User deleted successfully." );
    	}
        return new ResponseEntity<String>("Only admins can delete non-staff users.", HttpStatus.UNAUTHORIZED);
    }

}
