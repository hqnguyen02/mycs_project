/**
 * Test Recipe Service
 */
package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;

/**
 * Tests Auth Service class
 */
@SpringBootTest
class AuthServiceTest {

    /** Reference to auth service */
    @Autowired
    private AuthService    authService;

    /** Reference to user repository */
    @Autowired
    private UserRepository userRepository;
    
    /** Reference to role repository */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Really long string used to test if a username is too long - this string
     * is 260 characters long
     */
    private final String   LONG_STRING = "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa"
            + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa"
            + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa"
            + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa" + "aaaaaaaaaa";

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        userRepository.deleteAll();
    }

    /**
     * Test method for addStaff as an Admin.
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    @Transactional
    void testAddStaffAsAdmin () {
        final RegisterDto registerDto = new RegisterDto( "jdoe", "jdoe@gmail.com", "pass", "staff" );
        final RegisterDto registerDto2 = new RegisterDto( "jdoe", "janicedoe@gmail.com", "pass", "staff" );
        final RegisterDto registerDto3 = new RegisterDto( "jobdoe", "jdoe@gmail.com", "pass", "staff" );

        final String result1 = authService.addStaff( registerDto );

        // Add a staff successfully
        assertEquals( "Staff account registered successfully.", result1 );
        assertTrue( userRepository.existsByUsername( "jdoe" ) );
        assertTrue( userRepository.existsByEmail( "jdoe@gmail.com" ) );

        // Add a staff with duplicate username case
        final WolfCafeAPIException exception1 = assertThrows( WolfCafeAPIException.class, () -> {
            authService.addStaff( registerDto2 );
        } );
        assertEquals( "Username already exists.", exception1.getMessage() );

        final WolfCafeAPIException exception2 = assertThrows( WolfCafeAPIException.class, () -> {
            authService.addStaff( registerDto3 );
        } );

        assertEquals( "Email already exists.", exception2.getMessage() );
    }
    
    /**
     * Test method for addStaff as a Manager.
     */
    @Test
    @WithMockUser ( username = "man", roles = "MANAGER" )
    @Transactional
    void testAddStaffAsManager () {
        final RegisterDto registerDto = new RegisterDto( "jdoe", "jdoe@gmail.com", "pass", "staff" );
        final RegisterDto registerDto2 = new RegisterDto( "jdoe", "janicedoe@gmail.com", "pass", "staff" );
        final RegisterDto registerDto3 = new RegisterDto( "jobdoe", "jdoe@gmail.com", "pass", "staff" );

        final String result1 = authService.addStaff( registerDto );

        // Add a staff successfully
        assertEquals( "Staff account registered successfully.", result1 );
        assertTrue( userRepository.existsByUsername( "jdoe" ) );
        assertTrue( userRepository.existsByEmail( "jdoe@gmail.com" ) );

        // Add a staff with duplicate username case
        final WolfCafeAPIException exception1 = assertThrows( WolfCafeAPIException.class, () -> {
            authService.addStaff( registerDto2 );
        } );
        assertEquals( "Username already exists.", exception1.getMessage() );

        final WolfCafeAPIException exception2 = assertThrows( WolfCafeAPIException.class, () -> {
            authService.addStaff( registerDto3 );
        } );

        assertEquals( "Email already exists.", exception2.getMessage() );
    }

    /**
     * Test method for addStaff as a Customer.
     */
    @Test
    @WithMockUser ( username = "bob", roles = "USER" )
    void testAddStaffAsNotAdmin () {
        final RegisterDto registerDto = new RegisterDto( "jdoe", "jdoe@gmail.com", "pass", "staff" );

        final WolfCafeAPIException exception = assertThrows( WolfCafeAPIException.class, () -> {
            authService.addStaff( registerDto );
        } );

        assertEquals( "Only admins can create user with staff role.", exception.getMessage() );
    }
    
    /**
     * Test method for adding a Manager.
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    @Transactional
    void testAddManagerAsAdmin () {
        final RegisterDto registerDto = new RegisterDto( "jdoe", "jdoe@gmail.com", "pass", "manager" );
        final RegisterDto registerDto2 = new RegisterDto( "jdoe", "janicedoe@gmail.com", "pass", "manager" );
        final RegisterDto registerDto3 = new RegisterDto( "jobdoe", "jdoe@gmail.com", "pass", "manager" );

        final String result1 = authService.addManager( registerDto );

        // Add a staff successfully
        assertEquals( "Manager account registered successfully.", result1 );
        assertTrue( userRepository.existsByUsername( "jdoe" ) );
        assertTrue( userRepository.existsByEmail( "jdoe@gmail.com" ) );

        // Add a manager with duplicate username case
        final WolfCafeAPIException exception1 = assertThrows( WolfCafeAPIException.class, () -> {
            authService.addManager( registerDto2 );
        } );
        assertEquals( "Username already exists.", exception1.getMessage() );

        final WolfCafeAPIException exception2 = assertThrows( WolfCafeAPIException.class, () -> {
            authService.addManager( registerDto3 );
        } );

        assertEquals( "Email already exists.", exception2.getMessage() );
    }
    
    /**
     * Test method for addManager as a Customer.
     */
    @Test
    @WithMockUser ( username = "bob", roles = "USER" )
    void testAddManagerAsNotAdmin () {
        final RegisterDto registerDto = new RegisterDto( "jdoe", "jdoe@gmail.com", "pass", "manager" );

        final WolfCafeAPIException exception = assertThrows( WolfCafeAPIException.class, () -> {
            authService.addManager( registerDto );
        } );

        assertEquals( "Only admins can create user with manager role.", exception.getMessage() );
    }

    /**
     * Test method for AuthService.register()
     */
    @Test
    @WithMockUser
    @Transactional
    void testRegister () {

        // Register a user successfully
        final RegisterDto registerDto = new RegisterDto( "username", "e@m.ail", "abc123", "Customer" );
        assertEquals( "User registered successfully.", authService.register( registerDto ) );

        // Make sure the user is successfully added
        final User user = userRepository.findByUsername( "username" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        assertEquals( "username", user.getUsername() );
        assertEquals( "e@m.ail", user.getEmail() );
        // This is assertNotEquals because we should NOT store the user's
        // password as plaintext
        assertNotEquals( "abc123", user.getPassword() );

        // Try adding some invalid users

        // Invalid role
        assertThrows( ResourceNotFoundException.class, () -> {
            final RegisterDto newRegisterDto = new RegisterDto( "abcd", "e@m.ail2", "abc123", "abc" );
            authService.register( newRegisterDto );
        } );
        assertFalse( userRepository.existsByUsername( "abcd" ) );

        // Name too short
        assertThrows( WolfCafeAPIException.class, () -> {
            final RegisterDto newRegisterDto = new RegisterDto( "a", "e@m.ail2", "abc123", "Customer" );
            authService.register( newRegisterDto );
        } );
        assertFalse( userRepository.existsByUsername( "a" ) );

        // Name too long
        assertThrows( WolfCafeAPIException.class, () -> {
            final RegisterDto newRegisterDto = new RegisterDto( LONG_STRING, "e@m.ail2", "abc123",
                    "Customer" );
            authService.register( newRegisterDto );
        } );
        assertFalse( userRepository.existsByUsername( LONG_STRING ) );

        // Name has non-alphanumeric characters
        assertThrows( WolfCafeAPIException.class, () -> {
            final RegisterDto newRegisterDto = new RegisterDto( "abcd@", "e@m.ail2", "abc123", "Customer" );
            authService.register( newRegisterDto );
        } );
        assertFalse( userRepository.existsByUsername( "abcd@" ) );

        // Duplicate username
        assertThrows( WolfCafeAPIException.class, () -> {
            final RegisterDto newRegisterDto = new RegisterDto( "username", "e@m.ail2", "abc123",
                    "Customer" );
            authService.register( newRegisterDto );
        } );
        assertTrue( userRepository.existsByUsername( "username" ) );
        assertFalse( userRepository.existsByEmail( "e@m.ail2" ) );

        // password too short
        assertThrows( WolfCafeAPIException.class, () -> {
            final RegisterDto newRegisterDto = new RegisterDto( "newUsername", "e@m.ail2", "a", "Customer" );
            authService.register( newRegisterDto );
        } );
        assertFalse( userRepository.existsByUsername( "newUsername" ) );

        // password too long
        assertThrows( WolfCafeAPIException.class, () -> {
            final RegisterDto newRegisterDto = new RegisterDto( "newUsername", "e@m.ail2", LONG_STRING,
                    "Customer" );
            authService.register( newRegisterDto );
        } );
        assertFalse( userRepository.existsByUsername( "newUsername" ) );

        // Testing a bunch of invalid emails
        final String[] invalidEmails = new String[] { "", "a", "@.", "a@bc.", "@ab.c", "ab@.c", "a.b@c", };
        for ( final String email : invalidEmails ) {

            assertThrows( WolfCafeAPIException.class, () -> {
                final RegisterDto newRegisterDto = new RegisterDto( "newUsername", email, "abc123",
                        "Customer" );
                authService.register( newRegisterDto );
            } );
            assertFalse( userRepository.existsByUsername( "newUsername" ) );
        }
    }

    /**
     * Test method for AuthService.login()
     */
    @Test
    @WithMockUser
    @Transactional
    void testLogin () {

        // Make a user
        final RegisterDto registerDto = new RegisterDto( "username", "e@m.ail", "abc123", "Customer" );
        assertEquals( "User registered successfully.", authService.register( registerDto ) );

        // Try to log in
        final JwtAuthResponse response = authService.login( new LoginDto( "username", "abc123" ) );

        // Make sure we were successfully logged in
        assertEquals( "ROLE_CUSTOMER", response.getRole() );
        assertNotNull( response.getAccessToken() );
    }

    /**
     * Test method for AuthService.editUserById()
     */
    @Test
    @WithMockUser
    @Transactional
    void testEditUserById () {

        RegisterDto registerDto = new RegisterDto( "username", "e@m.ail", "abc123", "Customer" );
        assertEquals( "User registered successfully.", authService.register( registerDto ) );

        // Try to edit them
        final RegisterDto newDto = new RegisterDto( "username2", "e2@m.ail", "abc1234", "Customer" );
        User user = userRepository.findByUsername( "username" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        final Long oldId = user.getId();
        final String oldHash = user.getPassword();
        assertEquals( "User successfully edited.", authService.editUserById( user.getId(), newDto ) );

        // Make sure they were successfully edited
        user = userRepository.findByUsername( "username2" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        assertEquals( "username2", user.getUsername() );
        assertEquals( "e2@m.ail", user.getEmail() );
        assertEquals( oldId, user.getId() );
        assertNotEquals( oldHash, user.getPassword() );

        // Make another user

        registerDto = new RegisterDto( "username", "e@m.ail", "abc123", "Customer" );
        assertEquals( "User registered successfully.", authService.register( registerDto ) );
        user = userRepository.findByUsername( "username" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );

        // This user should be able to choose not change their username or
        // password
        registerDto = new RegisterDto( "username", "e@m.ail", "abc1234", "Customer" );
        assertEquals( "User successfully edited.", authService.editUserById( user.getId(), registerDto ) );

        // This user should not be able to edit their username to another
        // existing user's username
        assertThrows( WolfCafeAPIException.class, () -> {

            final User newUser = userRepository.findByUsername( "username" )
                    .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );

            final RegisterDto newRegisterDto = new RegisterDto( "username2", "e@m.ail", "abc1234",
                    "Customer" );
            authService.editUserById( newUser.getId(), newRegisterDto );
        } );
    }

    /**
     * Test method for AuthService.editCustomer()
     */
    @Test
    @WithMockUser
    @Transactional
    void testEditCustomer () {

        RegisterDto registerDto = new RegisterDto( "username", "e@m.ail", "abc123", "Customer" );
        assertEquals( "User registered successfully.", authService.register( registerDto ) );
        
        authService.login( new LoginDto( "username", "abc123" ) );

        // Try to edit them
        final RegisterDto newDto = new RegisterDto( "username2", "e2@m.ail", "abc1234", "Customer" );
        User user = userRepository.findByUsername( "username" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        final Long oldId = user.getId();
        final String oldHash = user.getPassword();
        assertEquals( "Customer successfully edited.", authService.editCustomer( "username", newDto ) );

        // Make sure they were successfully edited
        user = userRepository.findByUsername( "username2" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        assertEquals( "username2", user.getUsername() );
        assertEquals( "e2@m.ail", user.getEmail() );
        assertEquals( oldId, user.getId() );
        assertNotEquals( oldHash, user.getPassword() );

        // Make another user
        registerDto = new RegisterDto( "username", "e@m.ail", "abc123", "Customer" );
        assertEquals( "User registered successfully.", authService.register( registerDto ) );
        user = userRepository.findByUsername( "username" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );

        // This user should be able to choose not change their username or
        // password
        registerDto = new RegisterDto( "username", "e@m.ail", "abc1234", "Customer" );
        assertEquals( "Customer successfully edited.", authService.editCustomer( user.getUsername(), registerDto ) );

        // This user should not be able to edit their username to another
        // existing user's username
        assertThrows( WolfCafeAPIException.class, () -> {

            final User newUser = userRepository.findByUsername( "username" )
                    .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );

            final RegisterDto newRegisterDto = new RegisterDto( "username2", "e@m.ail", "abc1234",
                    "Customer" );
            authService.editCustomer( newUser.getUsername(), newRegisterDto );
        } );
    }

    
    /**
     * Test method for AuthService.deleteUserById()
     */
    @Test
    @WithMockUser
    @Transactional
    void testDeleteUserById() {

        // Make a user
        final RegisterDto registerDto = new RegisterDto( "username", "e@m.ail", "abc123", "Customer" );
        assertEquals( "User registered successfully.", authService.register( registerDto ) );
        final User user = userRepository.findByUsername( "username" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );

        // Delete them
        authService.deleteUserById( user.getId() );

        // Make sure they no longer exist
        assertFalse( userRepository.existsByUsername( "username" ) );

        // Try to delete a user that doesn't exist
        assertThrows( ResourceNotFoundException.class, () -> {
            authService.deleteUserById( user.getId() );
        } );
    }
    
    /**
     * Test method for AuthService.deleteUserById()
     */
    @Test
    @WithMockUser
    @Transactional
    void testDeleteCustomer() {
        // Make a user
        final RegisterDto registerDto = new RegisterDto( "username", "e@m.ail", "abc123", "Customer" );
        assertEquals( "User registered successfully.", authService.register( registerDto ) );
        final User user = userRepository.findByUsername( "username" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        
        authService.login( new LoginDto( "username", "abc123" ) );
        
        // Delete them
        authService.deleteCustomer( user.getUsername() );
        
     // Make sure they no longer exist
        assertFalse( userRepository.existsByUsername( "username" ) );

        // Try to delete a user that doesn't exist
        assertThrows( ResourceNotFoundException.class, () -> {
            authService.deleteCustomer( user.getUsername() );
        } );
    }
    
    /**
     * Test method for getAllUsers as an Admin.
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    @Transactional
    void testGetAllUsersAsAdmin () {
        final RegisterDto registerDto = new RegisterDto( "jdoe", "jdoe@gmail.com", "pass", "staff" );
        final RegisterDto registerDto2 = new RegisterDto( "jan", "janicedoe@gmail.com", "pass", "staff" );
        final RegisterDto registerDto3 = new RegisterDto( "jobdoe", "job@gmail.com", "pass", "manager" );

        // Add a staff successfully
        final String result1 = authService.addStaff( registerDto );

        assertEquals( "Staff account registered successfully.", result1 );
        assertTrue( userRepository.existsByUsername( "jdoe" ) );
        assertTrue( userRepository.existsByEmail( "jdoe@gmail.com" ) );

        // Add a another staff successfully
        final String result2 = authService.addStaff( registerDto2 );

        assertEquals( "Staff account registered successfully.", result2 );
        assertTrue( userRepository.existsByUsername( "jan" ) );
        assertTrue( userRepository.existsByEmail( "janicedoe@gmail.com" ) );
        
        // Add a manager successfully
        final String result3 = authService.addManager( registerDto3 );

        assertEquals( "Manager account registered successfully.", result3 );
        assertTrue( userRepository.existsByUsername( "jobdoe" ) );
        assertTrue( userRepository.existsByEmail( "job@gmail.com" ) );
        
        List<UserDto> listResult = authService.getAllUsers();
        assertEquals("jdoe", listResult.get(0).getUserName());
        assertEquals("jan", listResult.get(1).getUserName());
        assertEquals("jobdoe", listResult.get(2).getUserName());
    }
    
    /**
     * Test method for getAllUsers as an Admin.
     */
    @Test
    @WithMockUser ( username = "man", roles = "MANAGER" )
    @Transactional
    void testGetUsersByRoleAsManager () {
        final RegisterDto registerDto = new RegisterDto( "jdoe", "jdoe@gmail.com", "pass", "staff" );
        final RegisterDto registerDto2 = new RegisterDto( "jan", "janicedoe@gmail.com", "pass", "staff" );
        final RegisterDto registerDto3 = new RegisterDto( "jobdoe", "job@gmail.com", "pass", "staff" );

        // Add a staff successfully
        final String result1 = authService.addStaff( registerDto );

        assertEquals( "Staff account registered successfully.", result1 );
        assertTrue( userRepository.existsByUsername( "jdoe" ) );
        assertTrue( userRepository.existsByEmail( "jdoe@gmail.com" ) );

        // Add a another staff successfully
        final String result2 = authService.addStaff( registerDto2 );

        assertEquals( "Staff account registered successfully.", result2 );
        assertTrue( userRepository.existsByUsername( "jan" ) );
        assertTrue( userRepository.existsByEmail( "janicedoe@gmail.com" ) );
        
        // Add a third staff successfully
        final String result3 = authService.addStaff( registerDto3 );

        assertEquals( "Staff account registered successfully.", result3 );
        assertTrue( userRepository.existsByUsername( "jobdoe" ) );
        assertTrue( userRepository.existsByEmail( "job@gmail.com" ) );
        
        List<UserDto> listResult = authService.getUsersByRole(roleRepository.findByName("ROLE_STAFF"));
        assertEquals("jdoe", listResult.get(0).getUserName());
        assertEquals("jan", listResult.get(1).getUserName());
        assertEquals("jobdoe", listResult.get(2).getUserName());
    }
    
    /**
     * Test method for AuthService.getCustomerById()
     */
    @Test
    @WithMockUser
    @Transactional
    void testGetCustomerById() {
        // Make a user
        final RegisterDto registerDto = new RegisterDto( "username", "e@m.ail", "abc123", "Customer" );
        assertEquals( "User registered successfully.", authService.register( registerDto ) );
        
        final User user = userRepository.findByUsername( "username" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        
        authService.login( new LoginDto( "username", "abc123" ) );
        
        // Get the customer using their ID
        final UserDto userCheck = authService.getUserById(user.getId());
        
        assertEquals(userCheck.getUserName(), user.getUsername());
        assertEquals(userCheck.getId(), user.getId());
        assertEquals(userCheck.getEmail(), user.getEmail());
    }
    
    /**
     * Test method for AuthService.getId()
     */
    @Test
    @WithMockUser
    @Transactional
    void testGetId() {
        // Make a user
        final RegisterDto registerDto = new RegisterDto( "username", "e@m.ail", "abc123", "Customer" );
        assertEquals( "User registered successfully.", authService.register( registerDto ) );
        
        final User user = userRepository.findByUsername( "username" )
                .orElseThrow( () -> new ResourceNotFoundException( "Failed to save user" ) );
        
        authService.login( new LoginDto( "username", "abc123" ) );
        
        // Get the customer using their ID
        final Long id = authService.getId(user.getUsername());
        
        assertEquals(id, user.getId());
    }
}
