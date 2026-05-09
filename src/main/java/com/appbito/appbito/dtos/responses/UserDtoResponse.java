package com.appbito.appbito.dtos.responses;

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
public class UserDtoResponse {

    private Long id;

    private String name;

    private String lastname;

    private LocalDate birthDate;

    private String email;

    private String image;

    private boolean enabled;
}
