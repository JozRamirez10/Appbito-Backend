package com.appbito.appbito.exceptions;

import com.appbito.appbito.constants.Constants;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * error 400 - Bad request.
     *
     * @param ex exception.
     *
     * @return response entity exception.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * error 401 - Unauthorized.
     *
     * @param ex exception.
     *
     * @return response entity exception.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(Collections.singletonMap(Constants.Errors.ERROR, ex.getMessage()));
    }

    /**
     * error 403 - Forbidden.
     *
     * @param ex exception.
     *
     * @return response entity exception.
     */
    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<?> handleForbiddenAccess(ForbiddenAccessException ex) {

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(Collections.singletonMap(Constants.Errors.ERROR, ex.getMessage()));
    }

    /**
     * error 404 - Not Found.
     *
     * @param ex exception.
     *
     * @return response entity exception.
     */
    @ExceptionHandler()
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Collections.singletonMap(Constants.Errors.ERROR, ex.getMessage()));
    }

}
