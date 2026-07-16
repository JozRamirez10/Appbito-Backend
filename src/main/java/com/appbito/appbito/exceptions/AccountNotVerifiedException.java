package com.appbito.appbito.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Account Not Verified Exception.
 */
public class AccountNotVerifiedException extends AuthenticationException {

    public AccountNotVerifiedException(String message) {
        super(message);
    }
}
