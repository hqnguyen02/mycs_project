package edu.ncsu.csc326.wolfcafe.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Provides details on errors.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
	/** Time the error occurred */
    private LocalDateTime timeStamp;
    /** Message displayed with the error */
    private String message;
    /** Details surrounding the error */
    private String details;
}
