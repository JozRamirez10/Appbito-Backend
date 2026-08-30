package com.appbito.appbito.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Habit Progress Dto Response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "HabitProgressDtoResponse", description = "Response to habitProgress methods")
public class HabitProgressDtoResponse {

    @Schema(example = "72", description = "HabitProgress id")
    private Long id;

    @Schema(example = "2026-12-31", type = "string", description = "Day when you did the habit")
    private LocalDate date;

    @Schema(example = "2", description = "Number of times you did the habit")
    private Integer timesPerformed;

    @Schema(example = "Today I read 7 pages", description = "Optional note for this progress")
    private String note;

    @Schema(example = "1", description = "Habit id")
    private Long habitId;

}
