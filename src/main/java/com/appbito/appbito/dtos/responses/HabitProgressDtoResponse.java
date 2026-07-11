package com.appbito.appbito.dtos.responses;

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
public class HabitProgressDtoResponse {

    private Long id;

    private LocalDate date;

    private Integer timesPerformed;

    private String note;

    private Long habitId;

}
