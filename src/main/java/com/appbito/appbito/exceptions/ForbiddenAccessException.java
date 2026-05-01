package com.appbito.appbito.exceptions;

import com.appbito.appbito.constants.Constants;

/**
 * Forbidden Acess Exception.
 */
public class ForbiddenAccessException extends RuntimeException {

    public ForbiddenAccessException() {
        super(Constants.Exceptions.ACCESS_DENIED);
    }

    public ForbiddenAccessException(String message) {
        super(message);
    }

}
