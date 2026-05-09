package com.appbito.appbito.dtos.requests;

import com.appbito.appbito.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Habit Dto Request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitDtoRequest {

    private Long id;

    @NotBlank(message = Constants.Errors.NAME_BLANK)
    private String name;

    private String description;

    @JsonFormat(pattern = Constants.General.HOUR_PATTERN)
    private LocalTime hour;

    @NotEmpty(message = Constants.Habit.DAYS_EMPTY)
    private Set<DayOfWeek> days;
}
