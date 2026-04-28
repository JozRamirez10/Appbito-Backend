package com.appbito.appbito.services;

import com.appbito.appbito.dtos.HabitDtoRequest;
import com.appbito.appbito.dtos.HabitDtoResponse;
import com.appbito.appbito.entities.Habit;
import com.appbito.appbito.entities.User;
import com.appbito.appbito.exceptions.ForbiddenAccessException;
import com.appbito.appbito.repositories.HabitRepository;
import com.appbito.appbito.repositories.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Habit Service Impl.
 */
@Service
@RequiredArgsConstructor
public class HabitServiceImp implements HabitService {

    private final HabitRepository repository;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HabitDtoResponse> findAll() {
        List<Habit> habits = this.repository.findAllWithDays();
        return listHabitToListHabitDto(habits);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HabitDtoResponse> findById(Long id) {
        return this.repository.findById(id)
            .map(this::habitToHabitDtoResponse);
    }

    @Override
    @Transactional
    public HabitDtoResponse save(HabitDtoRequest habitDto, Long userId) {

        User userProxy = this.userRepository.getReferenceById(userId);

        Habit habit = habitDtoRequestToHabit(habitDto);

        habit.setUser(userProxy);

        return habitToHabitDtoResponse(this.repository.save(habit));
    }

    @Override
    @Transactional
    public Optional<HabitDtoResponse> update(HabitDtoRequest habitDto, Long id, Long userId) {

        Optional<Habit> habitOptional = getValidHabitForUser(id, userId);

        if (habitOptional.isPresent()) {

            Habit habitBd = habitOptional.get();

            habitBd.setName(habitDto.getName());
            habitBd.setDescription(habitDto.getDescription());
            habitBd.setHour(habitDto.getHour());
            habitBd.setDays(habitDto.getDays());

            return Optional.of(habitToHabitDtoResponse(habitBd)); // Save automatically
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteById(Long id, Long userId) {

        Optional<Habit> habitOptional = getValidHabitForUser(id, userId);

        if (habitOptional.isPresent()) {

            this.repository.deleteAllProgressByHabitId(id);
            this.repository.delete(habitOptional.get());

            return true;
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitDtoResponse> findByUserId(Long id) {
        return listHabitToListHabitDto(
            this.repository.findByUserId(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitDtoResponse> findByToday(Long userId) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<Habit> habits = this.repository.findByDaysContainingAndUserId(today, userId);
        return listHabitToListHabitDto(habits);
    }

    @Override
    @Transactional
    public List<HabitDtoResponse> updateBatch(List<HabitDtoRequest> habits, Long userId) {

        if (habits == null || habits.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> habitIds = habits.stream()
            .map(HabitDtoRequest::getId)
            .collect(Collectors.toList());

        List<Habit> habitsBd = this.repository.findAllWithDaysByIds(habitIds);

        Map<Long, HabitDtoRequest> dtoMap = habits.stream()
            .collect(Collectors.toMap(HabitDtoRequest::getId, dto -> dto));

        for (Habit habit : habitsBd) {

            validateOwnership(habit, userId);

            HabitDtoRequest dto = dtoMap.get(habit.getId());
            if (dto != null) {
                habit.setName(dto.getName());
                habit.setDescription(dto.getDescription());
                habit.setHour(dto.getHour());
                habit.setDays(dto.getDays());
            }
        }

        return listHabitToListHabitDto(this.repository.saveAll(habitsBd));
    }

    private Habit habitDtoRequestToHabit(HabitDtoRequest habitDto) {

        return Habit.builder()
            .name(habitDto.getName())
            .description(habitDto.getDescription())
            .hour(habitDto.getHour())
            .days(habitDto.getDays())
            .build();

    }

    private HabitDtoResponse habitToHabitDtoResponse(Habit habit) {

        return HabitDtoResponse.builder()
            .id(habit.getId())
            .name(habit.getName())
            .description(habit.getDescription())
            .hour(habit.getHour())
            .userId(habit.getUser().getId())
            .days(habit.getDays())
            .build();

    }

    private List<HabitDtoResponse> listHabitToListHabitDto(List<Habit> habits) {
        return habits.stream()
            .map(this::habitToHabitDtoResponse)
            .collect(Collectors.toList());
    }

    private Optional<Habit> getValidHabitForUser(Long habitId, Long userId) {
        return this.repository.findById(habitId).map(habit -> {
            validateOwnership(habit, userId);
            return habit;
        });
    }

    private void validateOwnership(Habit habit, Long userId) {
        if (!habit.getUser().getId().equals(userId)) {
            throw new ForbiddenAccessException();
        }
    }
}
