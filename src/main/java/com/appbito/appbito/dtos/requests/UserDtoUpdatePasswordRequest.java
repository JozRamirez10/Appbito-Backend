package com.appbito.appbito.dtos.requests;

import com.appbito.appbito.constants.Constants;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Dto Update Password Request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoUpdatePasswordRequest {

    @NotBlank(message = Constants.Errors.MUST_NOT_BE_EMPTY)
    private String oldPassword;

    @NotBlank(message = Constants.Errors.MUST_NOT_BE_EMPTY)
    private String newPassword;

}
