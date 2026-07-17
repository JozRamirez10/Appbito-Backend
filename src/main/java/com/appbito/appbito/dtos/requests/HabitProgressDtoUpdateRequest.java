package com.appbito.appbito.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Habit Progress Dto Update Request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "HabitProgressDtoUpdateRequest", description = "Request to update a habitProgress")
public class HabitProgressDtoUpdateRequest {

    @Schema(example = "2", description = "Number of times you did the habit")
    private Integer timesPerformed;

    @Schema(example = "Today I read only 7 pages", description = "Optional note for this progress")
    private String note;
}
