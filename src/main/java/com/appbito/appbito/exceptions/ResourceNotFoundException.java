package com.appbito.appbito.exceptions;

import com.appbito.appbito.constants.Constants;

/**
 * Resource Not Found Exception.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super(Constants.Exceptions.RESOURCE_NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(Constants.Exceptions.RESOURCE_NOT_FOUND + ": " + message);
    }

}
