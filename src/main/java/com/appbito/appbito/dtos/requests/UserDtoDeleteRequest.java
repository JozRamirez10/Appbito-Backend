package com.appbito.appbito.dtos.requests;

import com.appbito.appbito.constants.Constants;
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
public class UserDtoDeleteRequest {

    @NotBlank(message = Constants.User.PASSWORD_BLANK)
    private String password;

}
