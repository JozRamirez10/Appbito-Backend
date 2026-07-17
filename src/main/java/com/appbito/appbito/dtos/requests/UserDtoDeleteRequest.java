package com.appbito.appbito.dtos.requests;

import com.appbito.appbito.constants.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Dto Delete Request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserDtoDeleteRequest", description = "Request to delete user account")
public class UserDtoDeleteRequest {

    @Schema(example = "123456", description = "Password to delete the user account")
    @NotBlank(message = Constants.User.PASSWORD_BLANK)
    private String password;

}
