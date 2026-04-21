package com.appbito.appbito.dtos;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitDTO {
    
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime hour;

    @NotEmpty(message = "Days are required")
    private List<String> days;

    @JsonIgnore
    private List<String> progress;

    @JsonIgnore
    private List<HabitProgressDTO> habitProgress;

    private Long userId;
    
}
