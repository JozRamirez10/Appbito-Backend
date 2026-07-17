package com.appbito.appbito.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Habit Progress Dto Monthly Response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "HabitProgressDtoMonthlyResponse", description = "Response to monthly progress")
public class HabitProgressDtoMonthlyResponse {

    @Schema(example = "2026", description = "Year of request")
    private Integer year;

    @Schema(example = "JULY", description = "Month of request")
    private Integer month;

    @Schema(example = "27", description = "Times the habit was formed within the month and year")
    private Long totalTimesPerformed;

    @Schema(example = "1", description = "Habit id of request")
    private Long habitId;

}
