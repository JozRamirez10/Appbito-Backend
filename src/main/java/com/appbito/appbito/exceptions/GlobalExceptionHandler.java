package com.appbito.appbito.exceptions;

import com.appbito.appbito.constants.Constants;
import jakarta.validation.ConstraintViolationException;
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

    private static final String ERROR = Constants.Errors.ERROR;

    // TODO: Add exception to json parse LocalDate and LocalTime

    /**
     * error 400 - Bad request to Illegal Arguments.
     *
     * @param ex exception.
     *
     * @return response entity exception.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Collections.singletonMap(ERROR, ex.getMessage()));
    }

    /**
     * error 400 - Bad request to Dtos Request.
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
     * error 400 - Bad request to List.
     *
     * @param ex exception.
     *
     * @return response entity exception.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraint(ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            if (propertyPath.contains(".")) {
                propertyPath = propertyPath.substring(propertyPath.indexOf(".") + 1);
            }
            errors.put(propertyPath, violation.getMessage());
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
            .body(Collections.singletonMap(ERROR, ex.getMessage()));
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
            .body(Collections.singletonMap(ERROR, ex.getMessage()));
    }

    /**
     * error 403 - Security Exception (Forbidden).
     *
     * @param ex exception.
     *
     * @return response entity exception.
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurityException(SecurityException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(Collections.singletonMap(ERROR, ex.getMessage()));
    }

    /**
     * error 404 - Not Found.
     *
     * @param ex exception.
     *
     * @return response entity exception.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Collections.singletonMap(ERROR, ex.getMessage()));
    }

    /**
     * error 500 - Internal Server Error.
     *
     * @param ex exception.
     *
     * @return response entity exception
     */
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<?> handleFileStorageException(FileStorageException ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Collections.singletonMap(ERROR, ex.getMessage()));
    }

}
