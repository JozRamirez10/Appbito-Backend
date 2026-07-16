package com.appbito.appbito.dtos.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * Login Dto Request.
 */
@Getter
@Setter
public class LoginDtoRequest {

    private String email;

    private String password;
}
