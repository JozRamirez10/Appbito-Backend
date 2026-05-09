package com.appbito.appbito.dtos.responses;

import com.appbito.appbito.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Habit Dto Response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitDtoResponse {

    private Long id;
    private String name;
    private String description;

    @JsonFormat(pattern = Constants.General.HOUR_PATTERN)
    private LocalTime hour;

    private Set<DayOfWeek> days;
    private Long userId;

}
