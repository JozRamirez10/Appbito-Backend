package com.appbito.appbito.services;

import com.appbito.appbito.dtos.HabitDtoRequest;
import com.appbito.appbito.dtos.HabitDtoResponse;
import java.util.List;
import java.util.Optional;

/**
 * Habit Service.
 */
public interface HabitService {

    List<HabitDtoResponse> findAll();

    Optional<HabitDtoResponse> findById(Long id);

    HabitDtoResponse save(HabitDtoRequest habit, Long userId);

    Optional<HabitDtoResponse> update(HabitDtoRequest habit, Long id, Long userId);

    boolean deleteById(Long id, Long userId);

    List<HabitDtoResponse> findByToday(Long userId);

    List<HabitDtoResponse> findByUserId(Long id);

    List<HabitDtoResponse> updateBatch(List<HabitDtoRequest> habits, Long userId);
}
