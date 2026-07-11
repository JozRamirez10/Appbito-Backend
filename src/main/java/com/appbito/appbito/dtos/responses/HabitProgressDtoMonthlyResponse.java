package com.appbito.appbito.dtos.responses;

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
public class HabitProgressDtoMonthlyResponse {

    private Integer year;

    private Integer month;

    private Long totalTimesPerformed;

    private Long habitId;

}
