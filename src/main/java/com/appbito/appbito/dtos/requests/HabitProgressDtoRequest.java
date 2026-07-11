package com.appbito.appbito.dtos.requests;

import com.appbito.appbito.constants.Constants;
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
public class HabitProgressDtoRequest {

    @NotNull(message = Constants.HabitProgress.DATE_NULL)
    private LocalDate date;

    private Integer timesPerformed;

    private String note;

   @NotNull(message = Constants.HabitProgress.HABIT_ID_NULL)
   private Long habitId;

}
