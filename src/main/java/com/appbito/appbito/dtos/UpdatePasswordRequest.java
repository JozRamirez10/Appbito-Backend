package com.appbito.appbito.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {

    @NotBlank(message = "Must not be empty")
    private String oldPassword;

    @NotBlank(message = "Must not be empty")
    private String newPassword;

}
