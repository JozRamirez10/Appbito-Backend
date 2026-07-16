package com.appbito.appbito.auth.models;

import java.util.Collections;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Custom User Details.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    private final Long id;

    public CustomUserDetails(Long id, String email, String password) {
        super(email, password, Collections.emptyList());
        this.id = id;
    }
}