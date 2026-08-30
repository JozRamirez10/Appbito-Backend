package com.appbito.appbito.dtos.requests;

import com.appbito.appbito.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * User Dto Request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserDtoRequest", description = "Request to create an user")
public class UserDtoRequest {

    @Schema(example = "Black", description = "User name")
    @NotBlank(message = Constants.Errors.NAME_BLANK)
    private String name;

    @Schema(example = "Widow", description = "User lastname")
    @NotBlank(message = Constants.User.LASTNAME_BLANK)
    private String lastname;

    @Schema(example = "1999-12-31", type = "string", description = "User birthdate")
    @NotNull(message = Constants.User.BIRTHDATE_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.General.DATE_PATTERN)
    private LocalDate birthDate;

    @Schema(example = "user@email.com", description = "User email")
    @NotBlank(message = Constants.User.EMAIL_BLANK)
    @Email(message = Constants.User.EMAIL_BAD_FORMAT)
    private String email;

    @Schema(example = "123456", description = "User password")
    @NotBlank(message = Constants.User.PASSWORD_BLANK)
    private String password;
}
