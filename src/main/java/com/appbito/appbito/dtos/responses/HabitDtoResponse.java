package com.appbito.appbito.dtos.responses;

import com.appbito.appbito.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "HabitDtoResponse", description = "Response to habit methods")
public class HabitDtoResponse {

    @Schema(example = "24", description = "Habit id")
    private Long id;

    @Schema(example = "Read a book", description = "Short and descriptive habit name")
    private String name;

    @Schema(example = "Read 10 pages", description = "Optional habit details")
    private String description;

    @Schema(example = "14:30:00", type = "string", description = "Hour to do the habit")
    @JsonFormat(pattern = Constants.General.HOUR_PATTERN)
    private LocalTime hour;

    @Schema(example = "[\"MONDAY\", \"WEDNESDAY\", \"FRIDAY\"]",
        description = "Days list to do the habit")
    private Set<DayOfWeek> days;

    @Schema(example = "10", description = "User id of habit owner")
    private Long userId;

}
