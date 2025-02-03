package edu.ncsu.csc326.wolfcafe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Handles global errors.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Handles WolfCafeAPIExceptions and returns a ResponseEntity
     * @param exception WolfCafeAPIException to use message from
     * @param webRequest used to get short description of request leading to exception
     * @return BadRequest ResponseEntity with details surrounding the error
     */
    @ExceptionHandler(WolfCafeAPIException.class)
    public ResponseEntity<ErrorDetails> handleAPIException(WolfCafeAPIException exception, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
