package edu.ncsu.csc326.wolfcafe.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Dto to represent a single user in the system.
 *
 * @author Krisjian Smith
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /** The user's username */
    private String       userName;

    /** The user's id */
    private Long         id;

    /** The user's email */
    private String       email;

    /** The user's roles */
    private List<String> roles;
}
