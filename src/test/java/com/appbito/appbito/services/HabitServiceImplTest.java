package com.appbito.appbito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.appbito.appbito.dtos.requests.HabitDtoRequest;
import com.appbito.appbito.dtos.responses.HabitDtoResponse;
import com.appbito.appbito.entities.Habit;
import com.appbito.appbito.entities.User;
import com.appbito.appbito.exceptions.ForbiddenAccessException;
import com.appbito.appbito.repositories.HabitRepository;
import com.appbito.appbito.repositories.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Habit Service Test
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class HabitServiceImplTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HabitServiceImpl habitService;

    private Habit habitEntity;

    private User userEntity;

    private static final Long HABIT_ID = 1L;

    private static final Long USER_ID = 1L;

    private static final String HABIT_NAME = "Do Yoga";

    private static final String HABIT_NAME_UPDATE = "Habit updated";

    private static final LocalTime HOUR_UPDATE = LocalTime.of(8, 0);

    private static final Set<DayOfWeek> DAYS_UPDATE = Set.of(DayOfWeek.TUESDAY);

    private static final Long FAKE_USER_ID = 100L;

    @BeforeEach
    void SetUp() {

        userEntity = new User();
        userEntity.setId(USER_ID);

        habitEntity = Habit.builder()
            .id(HABIT_ID)
            .name(HABIT_NAME)
            .hour(LocalTime.of(7, 0))
            .days(Set.of(DayOfWeek.MONDAY))
            .user(userEntity)
            .build();
    }

    @Test
    void findAllOk() {

        when(habitRepository.findAllWithDays()).thenReturn(List.of(habitEntity));

        List<HabitDtoResponse> result = habitService.findAll();

        validateListHabitDtoResponse(result);

        verify(habitRepository).findAllWithDays();
    }

    @Test
    void findAllEmptyList(){

        when(habitRepository.findAllWithDays()).thenReturn(List.of());

        List<HabitDtoResponse> result = habitService.findAll();

        assertTrue(result.isEmpty());

        verify(habitRepository).findAllWithDays();
    }

    @Test
    void findByIdOk() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        Optional<HabitDtoResponse> result = habitService.findById(HABIT_ID);

        assertTrue(result.isPresent());
        assertEquals(HABIT_NAME, result.get().getName());

        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void findByIdNotFound() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());

        Optional<HabitDtoResponse> result = habitService.findById(HABIT_ID);

        assertTrue(result.isEmpty());

        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void findByIdAndUserIdOk() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        Optional<HabitDtoResponse> result = habitService.findByIdAndUserId(HABIT_ID, USER_ID);

        assertTrue(result.isPresent());
        assertEquals(HABIT_NAME, result.get().getName());
        assertEquals(USER_ID, result.get().getUserId());

        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void findByIdAndUserIdForbidden() {

        habitEntity.setUser(fakeUser());

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitService.findByIdAndUserId(HABIT_ID, USER_ID);
        });

        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void findByIdAndUserIdNotFound() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());

        Optional<HabitDtoResponse> result = habitService.findByIdAndUserId(HABIT_ID, USER_ID);

        assertTrue(result.isEmpty());

        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void findByUserIdOk() {

        when(habitRepository.findByUserId(USER_ID)).thenReturn(List.of(habitEntity));

        List<HabitDtoResponse> result = habitService.findByUserId(USER_ID);

        validateListHabitDtoResponse(result);

        verify(habitRepository).findByUserId(USER_ID);
    }

    @Test
    void findByUserIdEmptyList() {

        when(habitRepository.findByUserId(USER_ID)).thenReturn(List.of());

        List<HabitDtoResponse> result = habitService.findByUserId(USER_ID);

        assertTrue(result.isEmpty());

        verify(habitRepository).findByUserId(USER_ID);
    }

    @Test
    void findByTodayOk() {

        DayOfWeek today = getToday();

        habitEntity.setDays(Set.of(today));

        when(habitRepository.findByDaysContainingAndUserId(today, USER_ID))
            .thenReturn(List.of(habitEntity));

        List<HabitDtoResponse> result = habitService.findByToday(USER_ID);

        validateListHabitDtoResponse(result);
    }

    @Test
    void findByTodayEmptyList() {

        when(habitRepository.findByDaysContainingAndUserId(getToday(), USER_ID)).thenReturn(List.of());

        List<HabitDtoResponse> result = habitService.findByToday(USER_ID);

        assertTrue(result.isEmpty());

        verify(habitRepository).findByDaysContainingAndUserId(getToday(), USER_ID);

    }

    @Test
    void saveOk() {

        when(habitRepository.save(any(Habit.class))).thenReturn(habitEntity);
        when(userRepository.getReferenceById(USER_ID)).thenReturn(userEntity);

        HabitDtoResponse result = habitService.save(habitToSave(), USER_ID);

        assertNotNull(result);
        assertEquals(HABIT_ID, result.getId());
        assertEquals(HABIT_NAME, result.getName());
        assertEquals(USER_ID, result.getUserId());

        verify(habitRepository).save(any(Habit.class));
        verify(userRepository).getReferenceById(USER_ID);
    }

    @Test
    void updateOk() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        Optional<HabitDtoResponse> result = habitService.update(habitToUpdate(), HABIT_ID, USER_ID);

        assertTrue(result.isPresent());
        assertEquals(HABIT_ID, result.get().getId());
        assertEquals(HABIT_NAME_UPDATE, result.get().getName());
        assertEquals(DAYS_UPDATE, result.get().getDays());
        assertEquals(HOUR_UPDATE, result.get().getHour());
        assertEquals(USER_ID, result.get().getUserId());

        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void updateNotFound() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());

        Optional<HabitDtoResponse> result = habitService.update(habitToUpdate(), HABIT_ID, USER_ID);

        assertTrue(result.isEmpty());

        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void updateForbidden() {

        habitEntity.setUser(fakeUser());

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitService.update(habitToUpdate(), HABIT_ID, USER_ID);
        });

        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void updateBatchOk() {

        Long id1 = 10L;
        Long id2 = 11L;
        List<Long> ids = List.of(id1, id2);

        String update1 = "update1";
        String update2 = "update2";

        HabitDtoRequest request1 = HabitDtoRequest.builder()
            .id(id1)
            .name(update1)
            .build();

        HabitDtoRequest request2 = HabitDtoRequest.builder()
            .id(id2)
            .name(update2)
            .build();

        List<HabitDtoRequest> requests = List.of(request1, request2);

        Habit entity1 = Habit.builder().id(id1).name("habit1").user(userEntity).build();
        Habit entity2 = Habit.builder().id(id2).name("habit2").user(userEntity).build();
        List<Habit> entitiesFromBd = List.of(entity1, entity2);

        when(habitRepository.findAllWithDaysByIds(ids)).thenReturn(entitiesFromBd);
        when(habitRepository.saveAll(anyList())).thenReturn(entitiesFromBd);

        List<HabitDtoResponse> result = habitService.updateBatch(requests, USER_ID);

        assertEquals(2, result.size());
        assertEquals(update1, result.get(0).getName());
        assertEquals(update2, result.get(1).getName());

        verify(habitRepository).findAllWithDaysByIds(ids);
        verify(habitRepository).saveAll(anyList());
    }

    @Test
    void updateBatchEmptyList() {

        List<HabitDtoResponse> result = habitService.updateBatch(List.of(), USER_ID);

        assertTrue(result.isEmpty());

        verifyNoInteractions(habitRepository);
    }

    @Test
    void updateBatchNullList() {

        List<HabitDtoResponse> result = habitService.updateBatch(null, USER_ID);

        assertTrue(result.isEmpty());

        verifyNoInteractions(habitRepository);
    }

    @Test
    void updateBatchUpdateToFoundIgnoreNotFound() {

        Long phantom_id = 2L;

        HabitDtoRequest req1 = habitToUpdate();

        HabitDtoRequest ghost = HabitDtoRequest.builder()
            .id(phantom_id).name("Phantom")
            .build();

        List<HabitDtoRequest> requests = List.of(req1, ghost);

        when(habitRepository.findAllWithDaysByIds(List.of(HABIT_ID, phantom_id)))
            .thenReturn(List.of(habitEntity));

        when(habitRepository.saveAll(anyList())).thenReturn(List.of(habitEntity));

        List<HabitDtoResponse> result = habitService.updateBatch(requests, USER_ID);

        assertEquals(1, result.size());

        verify(habitRepository).saveAll(anyList());
    }

    @Test
    void updateBatchDtoNull() {

        String fakeName = "fakeHabit";

        HabitDtoRequest req1 = habitToUpdate();
        List<HabitDtoRequest> requests = List.of(req1);

        Habit fakeHabit = Habit.builder().id(999L).name(fakeName).user(userEntity).build();

        when(habitRepository.findAllWithDaysByIds(List.of(HABIT_ID)))
            .thenReturn(List.of(habitEntity, fakeHabit));

        when(habitRepository.saveAll(anyList())).thenReturn(List.of(habitEntity, fakeHabit));

        List<HabitDtoResponse> result = habitService.updateBatch(requests, USER_ID);

        assertEquals(2, result.size());
        assertEquals(HABIT_NAME_UPDATE, result.get(0).getName());
        assertEquals(fakeName, result.get(1).getName());

        verify(habitRepository).saveAll(anyList());
    }

    @Test
    void updateBatchForbidden() {

        Long habit_id = 100L;

        HabitDtoRequest req1 = new HabitDtoRequest();
        req1.setId(habit_id);

        habitEntity.setUser(fakeUser());

        when(habitRepository.findAllWithDaysByIds(List.of(habit_id)))
            .thenReturn(List.of(habitEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitService.updateBatch(List.of(req1), USER_ID);
        });

        verify(habitRepository, never()).saveAll(anyList());
    }

    @Test
    void deleteByIdOk() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        boolean result = habitService.deleteById(HABIT_ID, USER_ID);

        assertTrue(result);

        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void deleteByIdNotFound() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());

        boolean result = habitService.deleteById(HABIT_ID, USER_ID);

        assertFalse(result);

        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void deleteByIdForbidden() {

        habitEntity.setUser(fakeUser());

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habitEntity));

        assertThrows(ForbiddenAccessException.class, () -> {
            habitService.deleteById(HABIT_ID, USER_ID);
        });

        verify(habitRepository).findById(HABIT_ID);
    }

    private void validateListHabitDtoResponse(List<HabitDtoResponse> result) {

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(HABIT_ID, result.get(0).getId());
        assertEquals(HABIT_NAME, result.get(0).getName());
        assertEquals(USER_ID, result.get(0).getUserId());

    }

    private HabitDtoRequest habitToSave() {
        return HabitDtoRequest.builder()
            .id(HABIT_ID)
            .name(HABIT_NAME)
            .days(DAYS_UPDATE)
            .hour(HOUR_UPDATE)
            .build();
    }

    private HabitDtoRequest habitToUpdate() {
        return HabitDtoRequest.builder()
            .id(HABIT_ID)
            .name(HABIT_NAME_UPDATE)
            .days(DAYS_UPDATE)
            .hour(HOUR_UPDATE)
            .build();
    }

    private User fakeUser() {
        return User.builder()
            .id(FAKE_USER_ID)
            .build();
    }

    private DayOfWeek getToday() {
        return LocalDate.now().getDayOfWeek();
    }

}
