package com.appbito.appbito.dtos.requests;

import com.appbito.appbito.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Dto Update Request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserDtoUpdateRequest", description = "Request to update user account")
public class UserDtoUpdateRequest {

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

}
