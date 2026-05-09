package com.appbito.appbito.dtos.requests;

import com.appbito.appbito.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UserDtoRequest {

    @NotBlank(message = Constants.Errors.NAME_BLANK)
    private String name;

    @NotBlank(message = Constants.User.LASTNAME_BLANK)
    private String lastname;

    @NotNull(message = Constants.User.BIRTHDATE_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.General.DATE_PATTERN)
    private LocalDate birthDate;

    @NotBlank(message = Constants.User.EMAIL_BLANK)
    @Email(message = Constants.User.EMAIL_BAD_FORMAT)
    private String email;

    @NotBlank(message = Constants.User.PASSWORD_BLANK)
    private String password;
}
