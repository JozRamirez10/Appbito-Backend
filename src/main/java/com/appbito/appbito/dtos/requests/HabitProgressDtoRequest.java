package com.appbito.appbito.dtos.requests;

import com.appbito.appbito.constants.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Habit Progress Dto Request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "HabitProgressDtoRequest", description = "Request to create a habitProgress")
public class HabitProgressDtoRequest {

    @Schema(example = "2026-12-31", type = "string", description = "Day when you did the habit")
    @NotNull(message = Constants.HabitProgress.DATE_NULL)
    private LocalDate date;

    @Schema(example = "2", description = "Number of times you did the habit")
    private Integer timesPerformed;

    @Schema(example = "Today I read only 7 pages", description = "Optional note for this progress")
    private String note;

    @Schema(example = "1", description = "Habit id")
    @NotNull(message = Constants.HabitProgress.HABIT_ID_NULL)
    private Long habitId;

}
