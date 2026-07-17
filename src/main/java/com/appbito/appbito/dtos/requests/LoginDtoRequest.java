package com.appbito.appbito.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Login Dto Request.
 */
@Getter
@Setter
@Schema(name = "LoginDtoRequest", description = "Request to login")
public class LoginDtoRequest {

    @Schema(example = "user@email.com", description = "Email to login")
    private String email;

    @Schema(example = "123456", description = "Password to login")
    private String password;
}
