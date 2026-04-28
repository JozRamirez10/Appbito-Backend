package com.appbito.appbito.exceptions;

import com.appbito.appbito.constants.Constants;

/**
 * Invalid Token Exception.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super(Constants.Exceptions.INVALID_FORMAT_TOKEN);
    }

    public InvalidTokenException(String message) {
        super(message);
    }

}
