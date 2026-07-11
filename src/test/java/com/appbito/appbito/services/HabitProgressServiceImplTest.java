package com.appbito.appbito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.appbito.appbito.dtos.requests.HabitProgressDtoRequest;
import com.appbito.appbito.dtos.requests.HabitProgressDtoUpdateRequest;
import com.appbito.appbito.dtos.responses.HabitProgressDtoMonthlyResponse;
import com.appbito.appbito.dtos.responses.HabitProgressDtoResponse;
import com.appbito.appbito.entities.Habit;
import com.appbito.appbito.entities.HabitProgress;
import com.appbito.appbito.entities.User;
import com.appbito.appbito.exceptions.ForbiddenAccessException;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import com.appbito.appbito.repositories.HabitProgressRepository;
import com.appbito.appbito.repositories.HabitRepository;
import java.lang.reflect.Method;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * Habit Progress Service Test
 */
@ExtendWith(MockitoExtension.class)
class HabitProgressServiceImplTest {

    @Mock
    private HabitProgressRepository habitProgressRepository;

    @Mock
    private HabitRepository habitRepository;

    @InjectMocks
    private HabitProgressServiceImpl habitProgressService;

    private HabitProgress habitProgressEntity;

    private Habit habitEntity;

    private User userEntity;

    private static final Long HABIT_PROGRESS_ID = 1L;

    private static final LocalDate HABIT_PROGRESS_DATE = LocalDate.now();

    private static final Long HABIT_PROGRESS_TIMES_PERFORMED = 2L;

    private static final Long HABIT_ID = 1L;

    private static final String HABIT_NAME = "Habit Test";

    private static final LocalTime HABIT_HOUR = LocalTime.of(8, 0);

    private static final Set<DayOfWeek> HABIT_DAYS = Set.of(LocalDate.now().getDayOfWeek());

    private static final Long USER_ID = 1L;

    private static final Long FAKE_USER_ID = 2L;

    private static final LocalDate DAY_BEFORE = HABIT_PROGRESS_DATE.minusDays(1);

    private static final LocalDate DAY_AFTER = HABIT_PROGRESS_DATE.plusDays(1);

    private static final Integer YEAR = HABIT_PROGRESS_DATE.getYear();

    private static final List<Long> HABIT_IDS = List.of(HABIT_ID);

    private static final List<Integer> YEARS = List.of(YEAR);

    private static final LocalDate START_DATE = LocalDate.of(YEAR, Month.JANUARY, 1);

    private static final LocalDate END_DATE = LocalDate.of(YEAR, Month.DECEMBER, 31);

    private static final List<Integer> MOCKED_YEARS = mock(List.class);

    private static final Integer TIMES_PERFORMED_UPDATE = 3;

    private static final String NOTE_UPDATE = "UPDATE";

    private static final int STREAK = 1;

    @BeforeEach
    void SetUp() {

        userEntity = User.builder()
            .id(USER_ID)
            .build();

        habitEntity = Habit.builder()
            .id(HABIT_ID)
            .name(HABIT_NAME)
            .hour(HABIT_HOUR)
            .days(HABIT_DAYS)
            .user(userEntity)
            .build();

        habitProgressEntity = HabitProgress.builder()
            .id(HABIT_PROGRESS_ID)
            .date(HABIT_PROGRESS_DATE)
            .timesPerformed(HABIT_PROGRESS_TIMES_PERFORMED.intValue())
            .habit(habitEntity)
            .build();
    }

    @Test
    void findByHabitIdOk() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        when(habitProgressRepository.findByHabitId(HABIT_ID))
            .thenReturn(List.of(habitProgressEntity));

        List<HabitProgressDtoResponse> result =
            habitProgressService.findByHabitId(HABIT_ID, USER_ID);

        vaidateListHabitProgressDtoResponse(result);

        verify(habitProgressRepository).findByHabitId(HABIT_ID);
        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void findByHabitIdEmptyList() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        when(habitProgressRepository.findByHabitId(HABIT_ID)).thenReturn(List.of());

        List<HabitProgressDtoResponse> result =
            habitProgressService.findByHabitId(HABIT_ID, USER_ID);

        assertTrue(result.isEmpty());

        verify(habitProgressRepository).findByHabitId(HABIT_ID);
        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void findByHabitIdForbidden() {

        setFakeUser();

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitProgressService.findByHabitId(HABIT_ID, USER_ID);
        });

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository, never()).findByHabitId(HABIT_ID);
    }

    @Test
    void findByHabitIdNotFound() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            habitProgressService.findByHabitId(HABIT_ID, USER_ID);
        });

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository, never()).findByHabitId(HABIT_ID);
    }

    @Test
    void findByIdOk() {

        when(habitProgressRepository.findById(HABIT_PROGRESS_ID))
            .thenReturn(Optional.of(habitProgressEntity));

        HabitProgressDtoResponse result = habitProgressService.findById(HABIT_PROGRESS_ID, USER_ID);

        assertNotNull(result);
        assertEquals(HABIT_PROGRESS_ID, result.getId());
        assertEquals(HABIT_PROGRESS_DATE, result.getDate());
        assertEquals(HABIT_PROGRESS_TIMES_PERFORMED.intValue(), result.getTimesPerformed());
        assertEquals(HABIT_ID, result.getHabitId());

        verify(habitProgressRepository).findById(HABIT_PROGRESS_ID);
    }

    @Test
    void findByIdForbidden() {

        setFakeUser();

        when(habitProgressRepository.findById(HABIT_PROGRESS_ID))
        .thenReturn(Optional.of(habitProgressEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitProgressService.findById(HABIT_ID, USER_ID);
        });

        verify(habitProgressRepository).findById(HABIT_PROGRESS_ID);
    }

    @Test
    void findByIdNotFound() {

        when(habitProgressRepository.findById(HABIT_PROGRESS_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            habitProgressService.findById(HABIT_PROGRESS_ID, USER_ID);
        });

        verify(habitProgressRepository).findById(HABIT_PROGRESS_ID);
    }

    @Test
    void findByHabitIdAndDateBetweenOk() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));
        when(habitProgressRepository.findByHabitIdAndDateBetween(HABIT_ID, DAY_BEFORE, DAY_AFTER))
            .thenReturn(List.of(habitProgressEntity));

        List<HabitProgressDtoResponse> result =
            habitProgressService.findByHabitIdAndDateBetween(HABIT_ID, DAY_BEFORE, DAY_AFTER,
                USER_ID);

        vaidateListHabitProgressDtoResponse(result);

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository)
            .findByHabitIdAndDateBetween(HABIT_ID, DAY_BEFORE, DAY_AFTER);
    }

    @Test
    void findByHabitIdAndDateBetweenEmptyList() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));
        when(habitProgressRepository.findByHabitIdAndDateBetween(HABIT_ID, DAY_BEFORE, DAY_AFTER))
            .thenReturn(List.of());

        List<HabitProgressDtoResponse> result =
            habitProgressService.findByHabitIdAndDateBetween(HABIT_ID, DAY_BEFORE, DAY_AFTER,
                USER_ID);

        assertTrue(result.isEmpty());

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository)
            .findByHabitIdAndDateBetween(HABIT_ID, DAY_BEFORE, DAY_AFTER);
    }

    @Test
    void findByHabitIdAndDateBetweenForbidden() {

        setFakeUser();

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitProgressService.findByHabitIdAndDateBetween(HABIT_ID, DAY_BEFORE, DAY_AFTER,
                USER_ID);
        });

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository, never())
            .findByHabitIdAndDateBetween(HABIT_ID, DAY_BEFORE, DAY_AFTER);
    }

    @Test
    void findByHabitIdAndDateBetweenNotFound() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            habitProgressService.findByHabitIdAndDateBetween(HABIT_ID, DAY_BEFORE, DAY_AFTER,
                USER_ID);
        });

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository, never())
            .findByHabitIdAndDateBetween(HABIT_ID, DAY_BEFORE, DAY_AFTER);
    }

    @Test
    void getTotalTimesPerformedByMonthsOk() {

        HabitProgressDtoMonthlyResponse habitProgressDtoMonthlyResponse =
            habitProgressDtoMonthlyResponse();

        when(habitRepository.findAllById(HABIT_IDS)).thenReturn(List.of(habitEntity));
        when(habitProgressRepository
            .getTotalTimesPerformedByMonths(START_DATE, END_DATE, HABIT_IDS))
            .thenReturn(List.of(habitProgressDtoMonthlyResponse));

        List<HabitProgressDtoMonthlyResponse> result = habitProgressService
            .getTotalTimesPerformedByMonths(List.of(YEAR), HABIT_IDS, USER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(YEAR, result.get(0).getYear());
        assertEquals(HABIT_PROGRESS_DATE.getMonthValue(), result.get(0).getMonth());
        assertEquals(HABIT_PROGRESS_TIMES_PERFORMED, result.get(0).getTotalTimesPerformed());
        assertEquals(HABIT_ID, result.get(0).getHabitId());
    }

    @Test
    void getTotalTimesPerformedListEmpty() {

        when(habitRepository.findAllById(HABIT_IDS)).thenReturn(List.of(habitEntity));
        when(habitProgressRepository
            .getTotalTimesPerformedByMonths(START_DATE, END_DATE, HABIT_IDS))
            .thenReturn(List.of());

        List<HabitProgressDtoMonthlyResponse> result = habitProgressService
            .getTotalTimesPerformedByMonths(YEARS, HABIT_IDS, USER_ID);

        assertTrue(result.isEmpty());

        verify(habitRepository).findAllById(HABIT_IDS);
        verify(habitProgressRepository)
            .getTotalTimesPerformedByMonths(START_DATE, END_DATE, HABIT_IDS);
    }

    @Test
    void getTotalTimesPerformedYearsNull() {

        List<HabitProgressDtoMonthlyResponse> result = habitProgressService
            .getTotalTimesPerformedByMonths(null, HABIT_IDS, USER_ID);

        validateEmptyListHabitProgressDtoMonthlyResponseAndVerify(result);
    }

    @Test
    void getTotalTimesPerformedYearsEmpty() {

        List<HabitProgressDtoMonthlyResponse> result = habitProgressService
            .getTotalTimesPerformedByMonths(List.of(), HABIT_IDS, USER_ID);

        validateEmptyListHabitProgressDtoMonthlyResponseAndVerify(result);
    }

    @Test
    void getTotalTimesPerformedHabitIdsNull() {

        List<HabitProgressDtoMonthlyResponse> result = habitProgressService
            .getTotalTimesPerformedByMonths(YEARS, null, USER_ID);

        validateEmptyListHabitProgressDtoMonthlyResponseAndVerify(result);
    }

    @Test
    void getTotalTimesPerformedHabitIdsEmpty() {

        List<HabitProgressDtoMonthlyResponse> result = habitProgressService
            .getTotalTimesPerformedByMonths(YEARS, List.of(), USER_ID);

        validateEmptyListHabitProgressDtoMonthlyResponseAndVerify(result);
    }

    @Test
    void getTotalTimesPerformedMinYearException() {
        when(MOCKED_YEARS.isEmpty()).thenReturn(false);
        when(MOCKED_YEARS.stream()).thenReturn(Stream.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            habitProgressService.getTotalTimesPerformedByMonths(MOCKED_YEARS, HABIT_IDS, USER_ID);
        });
    }

    @Test
    void getTotalTimesPerformedMaxYearException() {
        when(MOCKED_YEARS.isEmpty()).thenReturn(false);
        when(MOCKED_YEARS.stream()).thenReturn(Stream.of(YEAR), Stream.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            habitProgressService.getTotalTimesPerformedByMonths(MOCKED_YEARS, HABIT_IDS, USER_ID);
        });
    }

    @Test
    void getTotalTimesPerformedSizeNotEquals() {

        when(habitRepository.findAllById(HABIT_IDS)).thenReturn(List.of());
        assertThrows(ResourceNotFoundException.class, () -> {
            habitProgressService.getTotalTimesPerformedByMonths(YEARS, HABIT_IDS, USER_ID);
        });
        verify(habitRepository).findAllById(HABIT_IDS);
        verify(habitProgressRepository, never())
            .getTotalTimesPerformedByMonths(START_DATE, END_DATE, HABIT_IDS);
    }

    @Test
    void getTotalTimesPerformedMissingIds() {

        List<Long> habitIds = List.of(HABIT_ID, 2L);

        when(habitRepository.findAllById(habitIds)).thenReturn(List.of(habitEntity));
        assertThrows(ResourceNotFoundException.class, () -> {
            habitProgressService.getTotalTimesPerformedByMonths(YEARS, habitIds, USER_ID);
        });
        verify(habitRepository).findAllById(habitIds);
        verify(habitProgressRepository, never())
            .getTotalTimesPerformedByMonths(START_DATE, END_DATE, habitIds);
    }

    @Test
    void getTotalTimesPerformedForbidden() {

        setFakeUser();

        when(habitRepository.findAllById(HABIT_IDS)).thenReturn(List.of(habitEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitProgressService.getTotalTimesPerformedByMonths(YEARS, HABIT_IDS, USER_ID);
        });

        verify(habitRepository).findAllById(HABIT_IDS);
        verify(habitProgressRepository, never())
            .getTotalTimesPerformedByMonths(START_DATE, END_DATE, HABIT_IDS);
    }

    private void vaidateListHabitProgressDtoResponse(List<HabitProgressDtoResponse> result) {
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(HABIT_PROGRESS_ID, result.get(0).getId());
        assertEquals(HABIT_PROGRESS_DATE, result.get(0).getDate());
        assertEquals(HABIT_PROGRESS_TIMES_PERFORMED.intValue(), result.get(0).getTimesPerformed());
        assertEquals(HABIT_ID, result.get(0).getHabitId());
    }

    @Test
    void saveOk() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));
        when(habitProgressRepository.save(any(HabitProgress.class)))
            .thenReturn(habitProgressEntity);

        HabitProgressDtoResponse result = habitProgressService.save(habitProgressToSave(), USER_ID);

        assertNotNull(result);
        assertEquals(HABIT_PROGRESS_ID, result.getId());
        assertEquals(HABIT_PROGRESS_DATE, result.getDate());
        assertEquals(HABIT_PROGRESS_TIMES_PERFORMED.intValue(), result.getTimesPerformed());
        assertEquals(HABIT_ID, result.getHabitId());

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository).save(any(HabitProgress.class));
    }

    @Test
    void saveForbidden() {

        setFakeUser();

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitProgressService.save(habitProgressToSave(), USER_ID);
        });

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository, never()).save(any(HabitProgress.class));
    }

    @Test
    void saveNotFound() {
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            habitProgressService.save(habitProgressToSave(), USER_ID);
        });
        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository, never()).save(any(HabitProgress.class));
    }

    @Test
    void updateOk() {
        when(habitProgressRepository.findById(HABIT_PROGRESS_ID))
            .thenReturn(Optional.of(habitProgressEntity));

        HabitProgressDtoResponse result =
            habitProgressService.update(HABIT_PROGRESS_ID, habitProgressToUpdate(), USER_ID);

        assertNotNull(result);
        assertEquals(HABIT_PROGRESS_ID, result.getId());
        assertEquals(HABIT_PROGRESS_DATE, result.getDate());
        assertEquals(TIMES_PERFORMED_UPDATE, result.getTimesPerformed());
        assertEquals(NOTE_UPDATE, result.getNote());
        assertEquals(HABIT_ID, result.getHabitId());

        verify(habitProgressRepository).findById(HABIT_PROGRESS_ID);
    }

    @Test
    void updateForbidden() {

        setFakeUser();

        when(habitProgressRepository.findById(HABIT_PROGRESS_ID))
            .thenReturn(Optional.of(habitProgressEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitProgressService.update(HABIT_PROGRESS_ID, habitProgressToUpdate(), USER_ID);
        });

        verify(habitProgressRepository).findById(HABIT_PROGRESS_ID);
    }

    @Test
    void updateNotFound() {
        when(habitProgressRepository.findById(HABIT_PROGRESS_ID)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()-> {
            habitProgressService.update(HABIT_PROGRESS_ID, habitProgressToUpdate(), USER_ID);
        });
        verify(habitProgressRepository).findById(HABIT_PROGRESS_ID);
    }

    @Test
    void deleteByIdOk() {

        when(habitProgressRepository.findById(HABIT_PROGRESS_ID))
            .thenReturn(Optional.of(habitProgressEntity));

        habitProgressService.deleteById(HABIT_PROGRESS_ID, USER_ID);

        verify(habitProgressRepository).findById(HABIT_PROGRESS_ID);
        verify(habitProgressRepository).delete(habitProgressEntity);
    }

    @Test
    void deleteByIdForbidden() {

        setFakeUser();

        when(habitProgressRepository.findById(HABIT_PROGRESS_ID))
            .thenReturn(Optional.of(habitProgressEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitProgressService.deleteById(HABIT_PROGRESS_ID, USER_ID);
        });

        verify(habitProgressRepository).findById(HABIT_PROGRESS_ID);
        verify(habitProgressRepository, never()).delete(habitProgressEntity);
    }

    @Test
    void deleteByIdNotFound() {
        when(habitProgressRepository.findById(HABIT_PROGRESS_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            habitProgressService.deleteById(HABIT_PROGRESS_ID, USER_ID);
        });

        verify(habitProgressRepository).findById(HABIT_PROGRESS_ID);
        verify(habitProgressRepository, never()).delete(habitProgressEntity);
    }

    @Test
    void getStreakOk() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));
        when(habitProgressRepository.findProgressByHabit(HABIT_ID))
            .thenReturn(List.of(habitProgressEntity));

        int result = habitProgressService.getStreak(HABIT_ID, USER_ID);

        assertEquals(STREAK, result);

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository).findProgressByHabit(HABIT_ID);
    }

    @Test
    void getStreakOkTwoProgress() {

        LocalDate date1 = LocalDate.of(YEAR, Month.JANUARY, 1);
        LocalDate date2 = LocalDate.of(YEAR, Month.JANUARY, 2);

        Set<DayOfWeek> allowedDays = Set.of(date1.getDayOfWeek(), date2.getDayOfWeek());
        habitEntity.setDays(allowedDays);

        habitProgressEntity.setDate(date1);
        HabitProgress habitProgressEntity2 = HabitProgress.builder()
            .id(2L)
            .date(date2)
            .timesPerformed(HABIT_PROGRESS_TIMES_PERFORMED.intValue())
            .habit(habitEntity)
            .build();

        List<HabitProgress> progress = List.of(habitProgressEntity, habitProgressEntity2);

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));
        when(habitProgressRepository.findProgressByHabit(HABIT_ID)).thenReturn(progress);

        int result = habitProgressService.getStreak(HABIT_ID, USER_ID);

        assertEquals(2, result);

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository).findProgressByHabit(HABIT_ID);
    }

    @Test
    void getStreakOkOneProgress() {

        LocalDate date1 = LocalDate.of(YEAR, Month.JANUARY, 1);
        LocalDate date2 = LocalDate.of(YEAR, Month.JANUARY, 2);
        LocalDate date3 = LocalDate.of(YEAR, Month.JANUARY, 3);

        Set<DayOfWeek> allowedDays = Set.of(date1.getDayOfWeek(), date2.getDayOfWeek(),
            date3.getDayOfWeek());
        habitEntity.setDays(allowedDays);

        habitProgressEntity.setDate(date1);
        HabitProgress habitProgressEntity2 = HabitProgress.builder()
            .id(3L)
            .date(date3)
            .timesPerformed(HABIT_PROGRESS_TIMES_PERFORMED.intValue())
            .habit(habitEntity)
            .build();

        List<HabitProgress> progress = List.of(habitProgressEntity, habitProgressEntity2);

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));
        when(habitProgressRepository.findProgressByHabit(HABIT_ID)).thenReturn(progress);

        int result = habitProgressService.getStreak(HABIT_ID, USER_ID);

        assertEquals(1, result);

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository).findProgressByHabit(HABIT_ID);
    }

    @Test
    void getStreakAllowedDaysNull() {

        habitEntity.setDays(null);
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        int result = habitProgressService.getStreak(HABIT_ID, USER_ID);

        assertEquals(0, result);

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository, never()).findProgressByHabit(HABIT_ID);
    }

    @Test
    void getStreakAllowedDaysEmpty() {

        habitEntity.setDays(Set.of());
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        int result = habitProgressService.getStreak(HABIT_ID, USER_ID);

        assertEquals(0, result);

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository, never()).findProgressByHabit(HABIT_ID);
    }

    @Test
    void getStreakProgressListEmpty() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));
        when(habitProgressRepository.findProgressByHabit(HABIT_ID)).thenReturn(List.of());

        int result = habitProgressService.getStreak(HABIT_ID, USER_ID);

        assertEquals(0, result);

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository).findProgressByHabit(HABIT_ID);
    }

    @Test
    void getStreakValidProgressDatesEmpty() {

        habitEntity.setDays(Set.of(LocalDate.of(YEAR, Month.JANUARY, 9)
            .getDayOfWeek()));
        habitProgressEntity.setDate(LocalDate.of(YEAR, Month.JANUARY, 10));

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));
        when(habitProgressRepository.findProgressByHabit(HABIT_ID))
            .thenReturn(List.of(habitProgressEntity));

        int result = habitProgressService.getStreak(HABIT_ID, USER_ID);

        assertEquals(0, result);

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository).findProgressByHabit(HABIT_ID);
    }

    @Test
    void getStreakForbidden() {

        setFakeUser();
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitProgressService.getStreak(HABIT_ID, USER_ID);
        });

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository, never()).findProgressByHabit(HABIT_ID);
    }

    @Test
    void getStreakNotFound() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            habitProgressService.getStreak(HABIT_ID, USER_ID);
        });

        verify(habitRepository).findById(HABIT_ID);
        verify(habitProgressRepository, never()).findProgressByHabit(HABIT_ID);
    }

    @Test
    void calculateStreak() throws Exception {

        List<LocalDate> progress = List.of(LocalDate.now());

        Method calculateStreakMethod = HabitProgressServiceImpl.class.getDeclaredMethod(
            "calculateStreak", List.class, Set.class);

        calculateStreakMethod.setAccessible(true);

        int resultProgressNull = (int) calculateStreakMethod.invoke(habitProgressService,
            null, HABIT_DAYS);
        assertEquals(0, resultProgressNull);

        int resultDaysNull = (int) calculateStreakMethod.invoke(habitProgressService, progress,
            null);
        assertEquals(0, resultDaysNull);

        int resultDaysEmpty = (int) calculateStreakMethod.invoke(habitProgressService, progress,
            Set.of());
        assertEquals(0, resultDaysEmpty);
    }

    @Test
    void findNextExpectedDate() throws Exception {

        Method findNextExpectedDateMethod = HabitProgressServiceImpl.class.getDeclaredMethod(
            "findNextExpectedDate", LocalDate.class, Set.class);

        findNextExpectedDateMethod.setAccessible(true);

        LocalDate from = LocalDate.of(YEAR, Month.JANUARY, 1);
        Set<DayOfWeek> emptyDays = Set.of();

        LocalDate result = (LocalDate) findNextExpectedDateMethod.invoke(
            habitProgressService, from, emptyDays);

        assertEquals(from.plusDays(8), result);
    }

    private void validateEmptyListHabitProgressDtoMonthlyResponseAndVerify(
        List<HabitProgressDtoMonthlyResponse> result) {

        assertTrue(result.isEmpty());

        verify(habitRepository, never()).findAllById(HABIT_IDS);
        verify(habitProgressRepository, never())
            .getTotalTimesPerformedByMonths(START_DATE, END_DATE, HABIT_IDS);
    }

    private HabitProgressDtoMonthlyResponse habitProgressDtoMonthlyResponse() {
        return HabitProgressDtoMonthlyResponse.builder()
            .year(habitProgressEntity.getDate().getYear())
            .month(habitProgressEntity.getDate().getMonthValue())
            .totalTimesPerformed(HABIT_PROGRESS_TIMES_PERFORMED)
            .habitId(HABIT_ID)
            .build();
    }

    private HabitProgressDtoRequest habitProgressToSave() {
        return HabitProgressDtoRequest.builder()
            .date(HABIT_PROGRESS_DATE)
            .timesPerformed(HABIT_PROGRESS_TIMES_PERFORMED.intValue())
            .habitId(HABIT_ID)
            .build();
    }

    private HabitProgressDtoUpdateRequest habitProgressToUpdate() {
        return HabitProgressDtoUpdateRequest.builder()
            .timesPerformed(TIMES_PERFORMED_UPDATE)
            .note(NOTE_UPDATE)
            .build();
    }

    private void setFakeUser() {
        habitEntity.setUser(fakeUser());
        habitProgressEntity.setHabit(habitEntity);
    }

    private User fakeUser() {
        return User.builder()
            .id(FAKE_USER_ID)
            .build();
    }

}
