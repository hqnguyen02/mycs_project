package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.mapper.UserMapper;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.security.JwtTokenProvider;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Implemented AuthService
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    /** Stores users */
    private final UserRepository        userRepository;
    /** Stores user roles */
    private final RoleRepository        roleRepository;
    /** Used to protect password privacy */
    private final PasswordEncoder       passwordEncoder;
    /** Used to manage authentication */
    private final AuthenticationManager authenticationManager;
    /** Used to keep track of which user is logged in */
    private final JwtTokenProvider      jwtTokenProvider;

    /** Max length of a username or password */
    private final int                   MAX_LEN = 255;

    /** Minimum length of a username or password */
    private final int                   MIN_LEN = 3;

    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    @Override
    public String register ( final RegisterDto registerDto ) {
        // Check for duplicates - username
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Check for duplicates - email
        if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        // Make sure that the username, password, and email are valid
        validateRegisterDto( registerDto );

        final User user = new User();
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Set<Role> roles = new HashSet<>();

        final Role userRole = roleRepository.findByName( "ROLE_" + registerDto.getRole().toUpperCase() );
        if ( userRole == null ) {
            throw new ResourceNotFoundException( registerDto.getRole() + " is not a valid role" );
        }
        roles.add( userRole );

        user.setRoles( roles );

        userRepository.save( user );

        return "User registered successfully.";
    }

    @Override
    public String addStaff ( final RegisterDto registerDto ) {
        // Authentication for admin
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch( grantedAuthority -> grantedAuthority.getAuthority().equals( "ROLE_ADMIN" ) );
        
        final boolean isManager = authentication.getAuthorities().stream()
                .anyMatch( grantedAuthority -> grantedAuthority.getAuthority().equals( "ROLE_MANAGER" ) );

        // Ensure only ADMINs and MANAGERs can add staff accounts
        if ( !isAdmin && !isManager ) {
            throw new WolfCafeAPIException( HttpStatus.FORBIDDEN, "Only admins can create user with staff role." );
        }

        // Check for duplicates - username
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Check for duplicates - email
        if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        // Create a new User object and set the fields
        final User user = new User();
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        // Assign the STAFF role
        final Set<Role> roles = new HashSet<>();
        final Role userRole = roleRepository.findByName( "ROLE_STAFF" );
        roles.add( userRole );
        user.setRoles( roles );

        // Save the new staff user in the repository
        userRepository.save( user );

        return "Staff account registered successfully.";
    }
    
    @Override
    public String addManager ( final RegisterDto registerDto ) {
        // Authentication for admin
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch( grantedAuthority -> grantedAuthority.getAuthority().equals( "ROLE_ADMIN" ) );

        // Ensure only ADMINs can add staff accounts
        if ( !isAdmin ) {
            throw new WolfCafeAPIException( HttpStatus.FORBIDDEN, "Only admins can create user with manager role." );
        }

        // Check for duplicates - username
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Check for duplicates - email
        if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        // Create a new User object and set the fields
        final User user = new User();
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        // Assign the STAFF role
        final Set<Role> roles = new HashSet<>();
        final Role userRole = roleRepository.findByName( "ROLE_MANAGER" );
        roles.add( userRole );
        user.setRoles( roles );

        // Save the new staff user in the repository
        userRepository.save( user );

        return "Manager account registered successfully.";
    }

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    @Override
    public JwtAuthResponse login ( final LoginDto loginDto ) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginDto.getUsernameOrEmail(), loginDto.getPassword() ) );

        SecurityContextHolder.getContext().setAuthentication( authentication );

        final String token = jwtTokenProvider.generateToken( authentication );

        final Optional<User> userOptional = userRepository.findByUsernameOrEmail( loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail() );

        String role = null;
        if ( userOptional.isPresent() ) {
            final User loggedInUser = userOptional.get();
            final Optional<Role> optionalRole = loggedInUser.getRoles().stream().findFirst();

            if ( optionalRole.isPresent() ) {
                final Role userRole = optionalRole.get();
                role = userRole.getName();
            }
        }

        final JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setRole( role );
        jwtAuthResponse.setAccessToken( token );

        return jwtAuthResponse;
    }

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     *
     */
    @Override
    public void deleteUserById ( final Long id ) {
        final User user = userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + id ) );

        // Delete the user's roles first
        user.setRoles( new HashSet<Role>() );
        userRepository.save( user );

        userRepository.deleteById( id );
    }

    @Override
    public String editUserById ( final Long id, final RegisterDto registerDto ) {

        // Find the user with the given id
        final User user = userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "No user exists with id " + id ) );

        // Make sure that the user's username is not taken, but don't check
        // if we aren't actually editing the username. Otherwise, the
        // existsByUsername would return true, since the user has their own
        // username.
        if ( !user.getUsername().equals( registerDto.getUsername() )
                && userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Do the same for email
        if ( !user.getEmail().equals( registerDto.getEmail() )
                && userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        // If the password was blank, then the password should not be modified
        if ( registerDto.getPassword().equals( "" ) ) {
            // Check that the email is valid
            if ( !isValidEmail( registerDto.getEmail() ) ) {
                throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                        registerDto.getEmail() + " is not a valid email address" );
            }

            // Check that the username is valid
            if ( !isValidUsername( registerDto.getUsername() ) ) {
                throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                        registerDto.getUsername() + " is not a valid username" );
            }
        }
        else {
            // Make sure that the username, password, and email are valid
            validateRegisterDto( registerDto );
        }

        // Get a new set of roles to add to the user
        final Set<Role> roles = new HashSet<Role>();
        final Role userRole = roleRepository.findByName( "ROLE_" + registerDto.getRole().toUpperCase() );
        if ( userRole == null ) {
            throw new ResourceNotFoundException( registerDto.getRole() + " is not a valid role" );
        }
        roles.add( userRole );

        // Set the user's username, password, and email
        user.setEmail( registerDto.getEmail() );
        user.setUsername( registerDto.getUsername() );
        if ( !registerDto.getPassword().equals( "" ) ) {
            user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );
        }
        user.setRoles( roles );
        userRepository.save( user );

        return "User successfully edited.";
    }

    /**
     * Checks that the username, password, and email of a given registerDto are
     * correct
     *
     * @param registerDto
     *            - the registerDto to check
     * @throws WolfCafeAPIException
     *             if the username, password, or email are not valid
     */
    private void validateRegisterDto ( final RegisterDto registerDto ) {

        // Check that the email is valid
        if ( !isValidEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                    registerDto.getEmail() + " is not a valid email address" );
        }

        // Check that the username is valid
        if ( !isValidUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                    registerDto.getUsername() + " is not a valid username" );
        }

        // Check that the password is valid
        if ( !isValidPassword( registerDto.getPassword() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                    registerDto.getPassword() + " is not a valid password" );
        }
    }

    /**
     * Determines if a username is between 3 and 255 characters and has no
     * non-alphanumeric characters
     *
     * @param username
     *            the username to check
     * @return True if the password is valid
     */
    private Boolean isValidUsername ( final String username ) {

        if ( username.length() < MIN_LEN || username.length() > MAX_LEN ) {
            return false;
        }

        // Check that each character is alphanumeric
        for ( int i = 0; i < username.length(); i++ ) {
            final char c = username.charAt( i );

            // Return false if any character is not in the range
            // 0-9, a-z, or A-Z
            if ( !( ( '0' <= c && c <= '9' ) || ( 'a' <= c && c <= 'z' ) || ( 'A' <= c && c <= 'Z' ) ) ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if a password is between 3 and 255 characters and has no
     * non-alphanumeric characters
     *
     * @param password
     *            the password to check
     * @return True if the password is valid
     */
    private Boolean isValidPassword ( final String password ) {
        return password.length() >= MIN_LEN && password.length() <= MAX_LEN;
    }

    /**
     * Checks if the given string is a valid email:
     * [something]@[something].[something]
     *
     * @param email
     *            the email address to check
     * @return true if the email is a valid email, or false otherwise
     */
    private Boolean isValidEmail ( final String email ) {
        final int lastAt = email.lastIndexOf( '@' );
        final int lastPeriod = email.lastIndexOf( '.' );

        // If there is no @ or period, return false
        if ( lastAt == -1 || lastPeriod == -1 ) {
            return false;
        }

        // Email string must be at least five characters long
        if ( email.length() < 5 ) {
            return false;
        }

        // Check that there is only one @ symbol
        if ( lastAt != email.indexOf( '@' ) ) {
            return false;
        }

        // @ must be in front of period
        if ( lastPeriod < lastAt ) {
            return false;
        }

        // period can not be the last character
        if ( lastPeriod == email.length() - 1 ) {
            return false;
        }

        // @ cannot be the first character
        if ( lastAt == 0 ) {
            return false;
        }

        // There must be at least one character between @ and period
        if ( lastPeriod == lastAt + 1 ) {
            return false;
        }

        // If all the above checks passed, the email is valid
        return true;
    }

    @Override
    public List<UserDto> getAllUsers () {

        // Get all users
        final List<User> users = userRepository.findAll();

        // Map to userDtos
        final List<UserDto> userDtos = new ArrayList<UserDto>();
        for ( final User u : users ) {
            userDtos.add( UserMapper.mapToUserDto( u ) );
        }

        return userDtos;
    }

    @Override
    public UserDto getUserById ( final Long id ) {
        final User user = userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "No user with id " + id ) );
        return UserMapper.mapToUserDto( user );
    }

	@Override
	public String editCustomer(String username, RegisterDto registerDto) {
		User user = null;
		
		// Get the user from their username.
		final Optional<User> userOpt = userRepository.findByUsername(username);
		
		// Check if the user is actually present in the repository and get them.
		try {
			user = userOpt.get();
		}
		
		catch (NoSuchElementException e) {
			throw new ResourceNotFoundException( "No user with name " + username );
		}
		
		String token = getToken(user.getId());
		
		// Check that the token is valid.
		if (jwtTokenProvider.validateToken(token)) {
			// Check that it corresponds with a logged-in customer.
			String username2 = jwtTokenProvider.getUsername(token);
			if (username2 == null) {
				throw new ResourceNotFoundException( "This customer does not exist." );
			}
		}
		else {
			throw new ResourceNotFoundException( "This customer does not exist." );
		}
			
		// Make sure that the user's username is not taken, but don't check
        // if we aren't actually editing the username. Otherwise, the
        // existsByUsername would return true, since the user has their own
        // username.
        if ( !user.getUsername().equals( registerDto.getUsername() )
                && userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Do the same for email
        if ( !user.getEmail().equals( registerDto.getEmail() )
                && userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        // If the password was blank, then the password should not be modified
        if ( registerDto.getPassword().equals( "" ) ) {
            // Check that the email is valid
            if ( !isValidEmail( registerDto.getEmail() ) ) {
                throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                        registerDto.getEmail() + " is not a valid email address" );
            }

            // Check that the username is valid
            if ( !isValidUsername( registerDto.getUsername() ) ) {
                throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                        registerDto.getUsername() + " is not a valid username" );
            }
        }
        
        else {
            // Make sure that the username, password, and email are valid
            validateRegisterDto( registerDto );
        }

        // Get a new set of roles to add to the user
        final Set<Role> roles = new HashSet<Role>();
        final Role userRole = roleRepository.findByName( "ROLE_" + registerDto.getRole().toUpperCase() );
        if ( userRole == null ) {
            throw new ResourceNotFoundException( registerDto.getRole() + " is not a valid role" );
        }
        
        roles.add( userRole );

        // Set the user's username, password, and email
        user.setEmail( registerDto.getEmail() );
        user.setUsername( registerDto.getUsername() );
        if ( !registerDto.getPassword().equals( "" ) ) {
            user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );
        }
        
        user.setRoles( roles );
        userRepository.save( user );

        return "Customer successfully edited.";
	}

	@Override
	public void deleteCustomer(String username) {
		User user = null;
		
		// Get the user from their username.
		final Optional<User> userOpt = userRepository.findByUsername(username);
		
		// Check if the user is actually present in the repository and get them.
		try {
			user = userOpt.get();
		}
		
		catch (NoSuchElementException e) {
			throw new ResourceNotFoundException( "No user with name " + username );
		}
		
		String token = getToken(user.getId());
		
		// Check that the token is valid.
		if (jwtTokenProvider.validateToken(token)) {
			// Check that it corresponds with a logged-in customer.
			String username2 = jwtTokenProvider.getUsername(token);
			if (username2 == null) {
				throw new ResourceNotFoundException( "This customer does not exist" );
			}
		}
		else {
			throw new ResourceNotFoundException( "This customer does not exist" );
		}
		
        // Delete the user's roles first
        user.setRoles( new HashSet<Role>() );
        userRepository.save( user );

        userRepository.deleteById( user.getId() );
	}
	
	/**
	 * Gets the token associated with the passed ID, used exclusively by the controller.
	 * 
	 * @param id id of customer to get the token of.
	 * @return the token for the customer.
	 */
	private String getToken(Long id) {
		final Optional<User> userOpt = userRepository.findById(id);
		
		if (userOpt.isEmpty()) {
			throw new ResourceNotFoundException( "This customer does not exist" );
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String tokenHold = jwtTokenProvider.generateToken(auth);
		User user = userRepository.findByUsername(jwtTokenProvider.getUsername(tokenHold)).get();
		if (user.getId() - id == 0) {
			return tokenHold;
		}
		else {
			throw new IllegalArgumentException( "The id " + user.getId() + " does not match the id " + id );
		}
	}

	@Override
	public Long getId(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isEmpty()) {
			throw new ResourceNotFoundException( "This customer does not exist" );
		}
		Long hold = user.get().getId();
		getToken(hold);
		return hold;
	}

	@Override
	public UserDto getCustomerById(Long id) {
		final User user = userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "No user with id " + id ) );
		Long hold = user.getId();
		getToken(hold);
        return UserMapper.mapToUserDto( user );
	}
    
    @Override
    public List<UserDto> getUsersByRole ( final Role role ) {
    	// Get all users
        final List<User> users = userRepository.findAll();

        // Map to userDtos
        final List<UserDto> userDtos = new ArrayList<UserDto>();
        for ( final User u : users ) {
        	if(u.getRoles().contains(role)) {
        		userDtos.add( UserMapper.mapToUserDto( u ) );
        	}
        }

        return userDtos;
    }
}
