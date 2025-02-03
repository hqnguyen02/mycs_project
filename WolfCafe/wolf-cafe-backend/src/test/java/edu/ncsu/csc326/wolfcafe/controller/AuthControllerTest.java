package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;

/**
 * Tests AuthController class
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    /**
     * The string that represents the admin password
     */
    @Value ( "${app.admin-user-password}" )
    private String         adminUserPassword;

    /** Reference to the user repository */
    @Autowired
    private UserRepository userRepository;

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc        mvc;

    /**
     * Test method for AuthController.login() with admin account
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    public void testLoginAdmin () throws Exception {
        final LoginDto loginDto = new LoginDto( "admin", adminUserPassword );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_ADMIN" ) );
    }

    /**
     * Test method for AuthController.register() and AuthController.login() with
     * customer account
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    public void testCreateCustomerAndLogin () throws Exception {
        final RegisterDto registerDto = new RegisterDto( "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC",
                "Customer" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_CUSTOMER" ) );

        // Try an invalid role
        final RegisterDto staffDto = new RegisterDto( "staff", "staff@staff.com", "abc123", "Staff" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staffDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isBadRequest() );
    }

    /**
     * Test method for AuthController.editUserById() with customer account
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    public void testEditUser () throws Exception {

        // Create user
        RegisterDto registerDto = new RegisterDto( "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC",
                "Customer" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        // Log in admin
        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new LoginDto( "admin", adminUserPassword ) ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_ADMIN" ) );

        // Edit user
        final User user = userRepository.findByUsername( "jestes" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        registerDto = new RegisterDto( "newUsername", "abc123@a.b", "password", "Customer" );

        mvc.perform( put( "/api/auth/user/" + user.getId() ).with( user( "admin" ).roles( "ADMIN" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( registerDto ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( content().string( "User successfully edited." ) );

        // Log in with new credentials

        final LoginDto loginDto = new LoginDto( "newUsername", "password" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_CUSTOMER" ) );
    }
    
    /**
     * Test method for AuthController.editUserById() with customer account
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    public void testEditCustomer () throws Exception {
    	// Create user
        RegisterDto registerDto = new RegisterDto( "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC",
                "Customer" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        // User logs in
        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_CUSTOMER" ) );

        // Edit user    
        final User user = userRepository.findByUsername( "jestes" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        registerDto = new RegisterDto( "newUsername", "abc123@a.b", "password", "Customer" );

        mvc.perform( put( "/api/auth/edit-customer").with( user( "jestes" ).roles( "CUSTOMER" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( registerDto ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( content().string( "Customer successfully edited." ) );
        
        final User updatedUser = userRepository.findByUsername("newUsername")
        		.orElseThrow( () -> new ResourceNotFoundException( "Failed to update user" ) );;
        assertEquals(user.getId(), updatedUser.getId());

    }

    /**
     * Test method for AuthController.deleteUserById()
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    public void testDeleteUser () throws Exception {

        // Log in admin
        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new LoginDto( "admin", adminUserPassword ) ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_ADMIN" ) );

        // Make new user
        final RegisterDto registerDto = new RegisterDto( "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC",
                "Customer" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        // Delete user
        final User user = userRepository.findByUsername( "jestes" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );

        mvc.perform( delete( "/api/auth/user/" + user.getId() ).with( user( "admin" ).roles( "ADMIN" ) ) )
                .andExpect( status().isOk() ).andExpect( content().string( "User deleted successfully." ) );
    }
    
    /**
     * Test method for AuthController.deleteUserById()
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    public void testDeleteCustomer () throws Exception {

        // Make new user
        final RegisterDto registerDto = new RegisterDto( "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC",
                "Customer" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );
        
        // User logs in
        	final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );
        
        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_CUSTOMER" ) );

        // Delete user
        final User user = userRepository.findByUsername( "jestes" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );

        mvc.perform( delete( "/api/auth/delete-customer" ).with( user( "jestes" ).roles( "CUSTOMER" ) ) )
                .andExpect( status().isOk() ).andExpect( content().string( "Customer deleted successfully." ) );
    }

    /**
     * Test method for the getUser() and getAllUsers() methods
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    void testGetUserMethods () throws Exception {

        // Make new users
        RegisterDto registerDto = new RegisterDto( "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC",
                "Customer" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        registerDto = new RegisterDto( "user", "us@e.r", "abc123", "Customer" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        // Get list of users
        final MvcResult response = mvc.perform( get( "/api/auth/all-users" ).with( user( "admin" ).roles( "ADMIN" ) ) )
                .andExpect( status().isOk() ).andReturn();
        assertTrue( response.getResponse().getContentAsString().contains( "jestes" ) );
        assertTrue( response.getResponse().getContentAsString().contains( "user" ) );

        // Get one user
        final User user = userRepository.findByUsername( "jestes" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        mvc.perform( get( "/api/auth/user/" + user.getId() ).with( user( "admin" ).roles( "ADMIN" ) ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.email" ).value( "vitae.erat@yahoo.edu" ) );

    }
    
    /**
     * Test method for the getCustomer() and getCustomerId() methods
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    void testGetCustomerDataMethods () throws Exception {

        // Make new users
        RegisterDto registerDto = new RegisterDto( "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC",
                "Customer" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );


        // User logs in
    	final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        // Get the customer
        final User user = userRepository.findByUsername( "jestes" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        
        mvc.perform( get( "/api/auth/customer/" + user.getId() ).with( user( "jestes" ).roles( "CUSTOMER" ) ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.email" ).value( "vitae.erat@yahoo.edu" ) );
        
        mvc.perform( get( "/api/auth/customer-id/" + user.getUsername() ).with( user( "jestes" ).roles( "CUSTOMER" ) ) )
        .andExpect( status().isOk() ).andExpect( content().string( user.getId().toString() ) );
    }

    /**
     * Test method for adding a Manager and Staff as Admin.
     */
    @Test
    @Transactional
    void testAddManagerAndStaffAsAdmin () {
        final RegisterDto registerDto = new RegisterDto( "jdoe", "jdoe@gmail.com", "pass", "staff" );
        final RegisterDto registerDto2 = new RegisterDto( "jan", "janicedoe@gmail.com", "pass", "manager" );

        try {
	        // Add a staff successfully
	        mvc.perform( post( "/api/auth/register/staff").with( user( "admin" ).roles( "ADMIN" ) )
	                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( registerDto ) )
	                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().is2xxSuccessful() )
	                .andExpect( content().string( "Staff account registered successfully." ) );
        } catch(Exception e) {
        	fail("Could not add staff as admin. Exception thrown:", e);
        }

        
        try {
	        // Add a manager successfully
	        mvc.perform( post( "/api/auth/register/manager").with( user( "admin" ).roles( "ADMIN" ) )
	                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( registerDto2 ) )
	                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().is2xxSuccessful() )
	                .andExpect( content().string( "Manager account registered successfully." ) );
        } catch(Exception e) {
        	fail("Could not add manager as admin. Exception thrown:", e);
        }
    }
    
    /**
     * Test method for adding, editing, getting, and deleting staff as a manager.
     */
    @Test
    @Transactional
    void testManagerControls () {
        final RegisterDto registerDto = new RegisterDto( "jdoe", "jdoe@gmail.com", "pass", "staff" );
        final RegisterDto registerDto2 = new RegisterDto( "jan", "janicedoe@gmail.com", "pass", "staff" );
        final RegisterDto registerDto3 = new RegisterDto( "jobdoe", "job@gmail.com", "pass", "staff" );


        try {
	        // Add a staff successfully
	        mvc.perform( post( "/api/auth/register/staff").with( user( "man" ).roles( "MANAGER" ) )
	                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( registerDto ) )
	                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().is2xxSuccessful() )
	                .andExpect( content().string( "Staff account registered successfully." ) );
        } catch(Exception e) {
        	fail("Could not add staff as manager. Exception thrown:", e);
        }

        
        try {
	        // Add a staff successfully
	        mvc.perform( post( "/api/auth/register/staff").with( user( "man" ).roles( "MANAGER" ) )
	                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( registerDto2 ) )
	                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().is2xxSuccessful() )
	                .andExpect( content().string( "Staff account registered successfully." ) );
        } catch(Exception e) {
        	fail("Could not add staff as manager. Exception thrown:", e);
        }
        
        try {
	        // Get the staff list
	        String staffList = mvc.perform( get( "/api/auth/get-staff").with( user( "man" ).roles( "MANAGER" ) ) )
	        		.andExpect( status().is2xxSuccessful() ).andReturn().getResponse().getContentAsString();
	        assertTrue(staffList.contains("jdoe"));
	        assertTrue(staffList.contains("jan"));
        } catch(Exception e) {
        	fail("Could not get staff list as manager. Exception thrown:", e);
        }
        
        try {
	        // Edit the first user
        	// Get first user
            final User user = userRepository.findByUsername( "jdoe" )
                    .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
            mvc.perform( put( "/api/auth/staff/" + user.getId() ).with( user( "man" ).roles( "MANAGER" ) )
                    .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( registerDto3 ) )
                    .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                    .andExpect( content().string( "User successfully edited." ) );
            
            
            // Get first user again
            final User updatedUser = userRepository.findByUsername( "jobdoe" )
                    .orElseThrow( () -> new ResourceNotFoundException( "Failed to update user" ) );
	        mvc.perform( get( "/api/auth/staff/" + updatedUser.getId())
	        		.with( user( "man" ).roles( "MANAGER" ) ) ).andExpect( status().isOk() )
	        		.andExpect( jsonPath( "$.userName" ).value( "jobdoe") ) 
	        		.andExpect( jsonPath( "$.email" ).value( "job@gmail.com") );
	        
        } catch(Exception e) {
        	fail("Could not edit staff as manager. Exception thrown:", e);
        }
        
        try {
	        // Delete the first staff member
            final User user = userRepository.findByUsername( "jobdoe" )
                    .orElseThrow( () -> new ResourceNotFoundException( "Failed to get user" ) );
            mvc.perform( delete( "/api/auth/staff/" + user.getId())
	        		.with( user( "man" ).roles( "MANAGER" ) ) ).andExpect( status().isOk() );
            
	        String staffList = mvc.perform( get( "/api/auth/get-staff").with( user( "man" ).roles( "MANAGER" ) ) )
	        		.andExpect( status().is2xxSuccessful() ).andReturn().getResponse().getContentAsString();
	        assertFalse(staffList.contains("jdoe"));
	        assertFalse(staffList.contains("jobdoe"));
	        assertTrue(staffList.contains("jan"));
        } catch(Exception e) {
        	fail("Could not delete staff as manager. Exception thrown:", e);
        }
    }

}
