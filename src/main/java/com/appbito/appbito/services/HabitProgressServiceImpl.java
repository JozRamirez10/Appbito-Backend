package com.appbito.appbito.services;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.requests.HabitProgressDtoRequest;
import com.appbito.appbito.dtos.requests.HabitProgressDtoUpdateRequest;
import com.appbito.appbito.dtos.responses.HabitProgressDtoMonthlyResponse;
import com.appbito.appbito.dtos.responses.HabitProgressDtoResponse;
import com.appbito.appbito.entities.Habit;
import com.appbito.appbito.entities.HabitProgress;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import com.appbito.appbito.repositories.HabitProgressRepository;
import com.appbito.appbito.repositories.HabitRepository;
import com.appbito.appbito.utils.AuthUtils;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Habit Progress Service Impl.
 */
@Service
@RequiredArgsConstructor
public class HabitProgressServiceImpl implements HabitProgressService {

    private static final String HABIT_NOT_FOUND = Constants.Errors.HABIT_NOT_FOUND;

    private static final String HABIT_PROGRESS_NOT_FOUND =
        Constants.Errors.HABIT_PROGRESS_NOT_FOUND;

    private static final String HABITS_NOT_FOUND = "Habits not found with IDs: ";

    private final HabitProgressRepository repository;

    private final HabitRepository habitRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HabitProgressDtoResponse> findByHabitId(Long habitId, Long userId) {
        validateHabitAndOwner(habitId, userId);
        List<HabitProgress> habitProgresses = this.repository.findByHabitId(habitId);
        return listHabitProgressToHabitProgressDtoResponse(habitProgresses);
    }

    @Override
    @Transactional(readOnly = true)
    public HabitProgressDtoResponse findById(Long id, Long userId) {
        HabitProgress habitProgress = getHabitProgressAndValidateOwner(id, userId);
        return habitProgressToHabitProgressDtoResponse(habitProgress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitProgressDtoResponse> findByHabitIdAndDateBetween(Long habitId,
        LocalDate startDate, LocalDate endDate, Long userId) {

        validateHabitAndOwner(habitId, userId);

        List<HabitProgress> habitProgresses =
            this.repository.findByHabitIdAndDateBetween(habitId, startDate, endDate);

        return listHabitProgressToHabitProgressDtoResponse(habitProgresses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitProgressDtoMonthlyResponse> getTotalTimesPerformedByMonths(
        List<Integer> years, List<Long> habitIds, Long userId) {

        if (years == null || years.isEmpty() || habitIds == null || habitIds.isEmpty()) {
            return List.of();
        }

        int minYear = years.stream().min(Integer::compareTo).orElseThrow(
            () -> new IllegalArgumentException(Constants.HabitProgress.YEARS_LIST_EMPTY)
        );
        int maxYear = years.stream().max(Integer::compareTo).orElseThrow(
            () -> new IllegalArgumentException(Constants.HabitProgress.YEARS_LIST_EMPTY)
        );

        List<Habit> requestedHabits = habitRepository.findAllById(habitIds);
        if (requestedHabits.size() != habitIds.size()) {
            List<Long> foundIds = requestedHabits.stream()
                .map(Habit::getId)
                .toList();

            List<Long> missingIds = habitIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

            throw new ResourceNotFoundException(HABITS_NOT_FOUND + missingIds);
        }
        requestedHabits.forEach(
            habit -> AuthUtils.validateOwnership(habit.getUser().getId(), userId)
        );

        LocalDate startDate = LocalDate.of(minYear, Month.JANUARY, 1);
        LocalDate endDate = LocalDate.of(maxYear, Month.DECEMBER, 31);

        List<HabitProgressDtoMonthlyResponse> results =
            this.repository.getTotalTimesPerformedByMonths(startDate, endDate, habitIds);

        return results.stream()
            .filter(dto -> years.contains(dto.getYear()))
            .toList();
    }

    @Override
    @Transactional
    public HabitProgressDtoResponse save(HabitProgressDtoRequest habitProgressDto, Long userId) {

        Habit habit = validateHabitAndOwner(habitProgressDto.getHabitId(), userId);

        HabitProgress habitProgress = habitProgressDtoRequestToHabitProgress(habitProgressDto);

        habitProgress.setHabit(habit);

        return habitProgressToHabitProgressDtoResponse(this.repository.save(habitProgress));
    }

    @Override
    @Transactional
    public HabitProgressDtoResponse update(Long id,
        HabitProgressDtoUpdateRequest habitProgressDto, Long userId) {

        HabitProgress habitProgressBd = getHabitProgressAndValidateOwner(id, userId);

        habitProgressBd.setTimesPerformed(habitProgressDto.getTimesPerformed());
        habitProgressBd.setNote(habitProgressDto.getNote());

        return habitProgressToHabitProgressDtoResponse(habitProgressBd);
    }

    @Override
    @Transactional
    public void deleteById(Long id, Long userId) {
        HabitProgress habitProgress = getHabitProgressAndValidateOwner(id, userId);
        this.repository.delete(habitProgress);
    }

    @Override
    @Transactional(readOnly = true)
    public int getStreak(Long habitId, Long userId) {

        Habit habit = validateHabitAndOwner(habitId, userId);

        Set<DayOfWeek> allowedDays = habit.getDays();
        if (allowedDays == null || allowedDays.isEmpty()) {
            return 0;
        }

        List<HabitProgress> progressList = repository.findProgressByHabit(habitId);
        if (progressList.isEmpty()) {
            return 0;
        }

        List<LocalDate> validProgressDates = progressList.stream()
            .map(HabitProgress::getDate)
            .filter(date -> allowedDays.contains(date.getDayOfWeek()))
            .distinct()
            .sorted()
            .toList();

        return calculateStreak(validProgressDates, allowedDays);
    }

    private int calculateStreak(List<LocalDate> progress, Set<DayOfWeek> allowedDays) {

        if (progress == null || progress.isEmpty() || allowedDays == null
            || allowedDays.isEmpty()) {
            return 0;
        }

        int maxStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < progress.size(); i++) {
            LocalDate lastSuccess = progress.get(i - 1);
            LocalDate currentSuccess = progress.get(i);
            LocalDate nextExpectedDate = findNextExpectedDate(lastSuccess, allowedDays);

            if (currentSuccess.equals(nextExpectedDate)) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }
        return maxStreak;
    }

    private LocalDate findNextExpectedDate(LocalDate from, Set<DayOfWeek> allowedDays) {
        LocalDate candidate = from.plusDays(1);
        for (int i = 0; i < 7; i++) {
            if (allowedDays.contains(candidate.getDayOfWeek())) {
                return candidate;
            }
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }

    private HabitProgressDtoResponse habitProgressToHabitProgressDtoResponse(
        HabitProgress habitProgress) {
        return HabitProgressDtoResponse.builder()
            .id(habitProgress.getId())
            .date(habitProgress.getDate())
            .timesPerformed(habitProgress.getTimesPerformed())
            .note(habitProgress.getNote())
            .habitId(habitProgress.getHabit().getId())
            .build();
    }

    private HabitProgress habitProgressDtoRequestToHabitProgress(
        HabitProgressDtoRequest habitProgressDto) {
        return HabitProgress.builder()
            .date(habitProgressDto.getDate())
            .timesPerformed(habitProgressDto.getTimesPerformed())
            .note(habitProgressDto.getNote())
            .build();
    }

    private List<HabitProgressDtoResponse> listHabitProgressToHabitProgressDtoResponse(
        List<HabitProgress> habitProgresses) {
        return habitProgresses.stream()
            .map(this::habitProgressToHabitProgressDtoResponse)
            .toList();
    }

    private Habit validateHabitAndOwner(Long habitId, Long userId) {
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new ResourceNotFoundException(HABIT_NOT_FOUND));
        AuthUtils.validateOwnership(habit.getUser().getId(), userId);
        return habit;
    }

    private HabitProgress getHabitProgressAndValidateOwner(Long habitProgressId, Long userId) {
        HabitProgress habitProgress = this.repository.findById(habitProgressId)
            .orElseThrow(() -> new ResourceNotFoundException(HABIT_PROGRESS_NOT_FOUND));
        AuthUtils.validateOwnership(habitProgress.getHabit().getUser().getId(), userId);
        return habitProgress;
    }
}
