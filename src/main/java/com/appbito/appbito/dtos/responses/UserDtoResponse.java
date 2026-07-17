package com.appbito.appbito.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Dto Response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserDtoResponse", description = "Response to user methods")
public class UserDtoResponse {

    @Schema(example = "10", description = "User id")
    private Long id;

    @Schema(example = "Black", description = "User name")
    private String name;

    @Schema(example = "Widow", description = "User lastname")
    private String lastname;

    @Schema(example = "1999-12-31", type = "string", description = "User birthdate")
    private LocalDate birthDate;

    @Schema(example = "user@email.com", description = "User email")
    private String email;

    @Schema(example = "profile_10.jpg", description = "User image")
    private String image;

    @Schema(example = "true", description = "User enabled")
    private boolean enabled;
}
