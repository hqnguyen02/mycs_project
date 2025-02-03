package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Information to login a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
	/** User's username or email */
    private String usernameOrEmail;
    /** User's password */
    private String password;

}
