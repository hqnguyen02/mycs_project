package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Information needed to register a new customer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
	/** Registering user's username */
    private String username;
    /** Registering user's email */
    private String email;
    /** Registering user's password */
    private String password;
    /** Registering user's role */
    private String role;
}
