package com.appbito.appbito.dtos.requests;

import com.appbito.appbito.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "HabitDtoRequest", description = "Request to create and update a habit")
public class HabitDtoRequest {

    @Schema(example = "Read a book", description = "Short and descriptive habit name")
    @NotBlank(message = Constants.Errors.NAME_BLANK)
    private String name;

    @Schema(example = "Read 10 pages", description = "Optional habit details")
    private String description;

    @Schema(example = "14:30:00", type = "string", description = "Hour to do the habit")
    @JsonFormat(pattern = Constants.General.HOUR_PATTERN)
    private LocalTime hour;

    @Schema(example = "[\"MONDAY\", \"WEDNESDAY\", \"FRIDAY\"]",
        description = "Days list to do the habit")
    @NotEmpty(message = Constants.Habit.DAYS_EMPTY)
    private Set<DayOfWeek> days;
}
