package com.appbito.appbito.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitProgressDTO {

    private Long id;

    private String date;

    private Long timesPerformed;

    private String note;

    private Long habitId;
    
}
