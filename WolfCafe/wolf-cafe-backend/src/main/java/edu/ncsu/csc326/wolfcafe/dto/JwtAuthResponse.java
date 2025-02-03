package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response for authenticated and authorized user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {
	/** Token of authenticated user */
    private String accessToken;
    /** Token type */
    private String tokenType = "Bearer";
    /** User's role this token provides access to */
    private String role;
}
