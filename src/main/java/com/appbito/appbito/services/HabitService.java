package com.appbito.appbito.services;

import com.appbito.appbito.dtos.requests.HabitDtoRequest;
import com.appbito.appbito.dtos.responses.HabitDtoResponse;
import java.util.List;
import java.util.Optional;

/**
 * Habit Service.
 */
public interface HabitService {

    List<HabitDtoResponse> findAll();

    Optional<HabitDtoResponse> findById(Long id);

    Optional<HabitDtoResponse> findByIdAndUserId(Long id, Long userId);

    List<HabitDtoResponse> findByToday(Long userId);

    List<HabitDtoResponse> findByUserId(Long id);

    HabitDtoResponse save(HabitDtoRequest habit, Long userId);

    Optional<HabitDtoResponse> update(HabitDtoRequest habit, Long id, Long userId);

    List<HabitDtoResponse> updateBatch(List<HabitDtoRequest> habits, Long userId);

    boolean deleteById(Long id, Long userId);

}
