package com.appbito.appbito.services;

import com.appbito.appbito.dtos.requests.HabitProgressDtoRequest;
import com.appbito.appbito.dtos.requests.HabitProgressDtoUpdateRequest;
import com.appbito.appbito.dtos.responses.HabitProgressDtoMonthlyResponse;
import com.appbito.appbito.dtos.responses.HabitProgressDtoResponse;
import java.time.LocalDate;
import java.util.List;

/**
 * Habit Progress Service.
 */
public interface HabitProgressService {

    List<HabitProgressDtoResponse> findByHabitId(Long habitId, Long userId);

    HabitProgressDtoResponse findById(Long id, Long userId);

    List<HabitProgressDtoResponse> findByHabitIdAndDateBetween(Long habitId, LocalDate startDate,
        LocalDate endDate, Long userId);

    List<HabitProgressDtoMonthlyResponse> getTotalTimesPerformedByMonths(List<Integer> years,
        List<Long> habitIds, Long userId);

    HabitProgressDtoResponse save(HabitProgressDtoRequest habitProgressDto, Long userId);

    HabitProgressDtoResponse update(Long id, HabitProgressDtoUpdateRequest habitProgressDto,
        Long userId);

    void deleteById(Long id, Long userId);

    int getStreak(Long habitId, Long userId);
}
