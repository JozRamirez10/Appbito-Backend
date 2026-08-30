package com.appbito.appbito.dtos.requests;

import com.appbito.appbito.constants.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "UserDtoUpdatePasswordRequest", description = "Request to update user password")
public class UserDtoUpdatePasswordRequest {

    @Schema(example = "123456", description = "Old user password")
    @NotBlank(message = Constants.Errors.MUST_NOT_BE_EMPTY)
    private String oldPassword;

    @Schema(example = "ABC123", description = "New user password")
    @NotBlank(message = Constants.Errors.MUST_NOT_BE_EMPTY)
    private String newPassword;

}
