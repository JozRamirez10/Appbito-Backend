package com.appbito.appbito.dtos.requests;

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
public class HabitProgressDtoUpdateRequest {

    private Integer timesPerformed;

    private String note;

}
