package com.appbito.appbito.dtos;

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

    @JsonFormat(pattern = "HH:mm")
    private LocalTime hour;

    private Set<DayOfWeek> days;
    private Long userId;

}
