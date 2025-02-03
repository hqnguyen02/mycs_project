package edu.ncsu.csc326.wolfcafe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when a resource is not found.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;

	/** Creates an exception for when a resource isn't found
	 * @param message message to construct exception with
	 */
	public ResourceNotFoundException(String message) {
        super(message);
    }
}
