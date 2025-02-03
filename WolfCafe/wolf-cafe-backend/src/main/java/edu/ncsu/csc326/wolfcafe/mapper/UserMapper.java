package edu.ncsu.csc326.wolfcafe.mapper;

import java.util.ArrayList;
import java.util.Collection;

import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.entity.User;

/**
 * Converts between UserDto and User entity
 */
public class UserMapper {

    /**
     * Converts a User entity to UserDto
     *
     * @param user
     *            User to convert
     * @return UserDto object
     */
    public static UserDto mapToUserDto ( final User user ) {

        // Make Dto with empty list of roles
        final UserDto userDto = new UserDto( user.getUsername(), user.getId(), user.getEmail(),
                new ArrayList<String>() );

        // Add the name of each role
        final Collection<Role> roles = user.getRoles();
        for ( final Role role : roles ) {

            // Set it so only the first letter is capitalized
            String roleName = role.getName().toLowerCase().substring( 5 );
            roleName = Character.toUpperCase( roleName.charAt( 0 ) ) + roleName.substring( 1 );
            userDto.getRoles().add( roleName );
        }

        return userDto;
    }

}
