package com.appbito.appbito.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitProgressMonthlyDTO {
    
    private int year;

    private int month;
    
    private Long habitId;
    
    private Long totalTimesPerformed;

}
