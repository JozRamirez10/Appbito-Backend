package com.appbito.appbito.dtos;

/**
 * User Dto Credentials.
 */
public record UserDtoCredentials(
    Long id,
    String email,
    String password,
    boolean enabled
) {}
