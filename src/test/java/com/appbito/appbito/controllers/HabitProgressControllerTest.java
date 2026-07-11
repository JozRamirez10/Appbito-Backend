package com.appbito.appbito.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.requests.HabitProgressDtoRequest;
import com.appbito.appbito.dtos.requests.HabitProgressDtoUpdateRequest;
import com.appbito.appbito.dtos.responses.HabitProgressDtoMonthlyResponse;
import com.appbito.appbito.dtos.responses.HabitProgressDtoResponse;
import com.appbito.appbito.exceptions.ForbiddenAccessException;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import com.appbito.appbito.services.HabitProgressService;
import com.appbito.appbito.utils.AuthUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = HabitProgressController.class)
@AutoConfigureMockMvc(addFilters = false)
class HabitProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HabitProgressService habitProgressService;

    private HabitProgressDtoRequest habitProgressRequestMock;

    private HabitProgressDtoResponse habitProgressResponseMock;

    private TestingAuthenticationToken authToken;

    private static final LocalDate DATE = LocalDate.now();

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String DATE_FORMATTED = DATE.format(FORMATTER);

    private static final List<Integer> YEARS = List.of(DATE.getYear());

    private static final String[] YEARS_PARAM =
        YEARS.stream().map(String::valueOf).toArray(String[]::new);

    private static final String YEARS_S = "years";

    private static final Integer TIMES_PERFORMED = 1;

    private static final Integer TIMES_PERFORMED_UPDATE = 2;

    private static final Long HABIT_ID = 1L;

    private static final List<Long> HABIT_IDS = List.of(HABIT_ID);

    private static final String[] HABIT_IDS_PARAM =
        HABIT_IDS.stream().map(String::valueOf).toArray(String[]::new);

    private static final String HABITS_IDS_S = "habitIds";

    private static final String NOTE_UPDATE = "Note";

    private static final Long HABIT_PROGRESS_ID = 1L;

    private static final Long USER_ID = 1L;

    private static final String START_DATE = "startDate";

    private static final String END_DATE = "endDate";

    private static final String PATH_BASE = "/api/habitsProgress";

    private static final String GET_BY_ID_HABIT = PATH_BASE + "/habit/{id}";

    private static final String GET_BY_ID = PATH_BASE + "/{id}";

    private static final String GET_BY_ID_HABIT_AND_DATE_RANGE = PATH_BASE + "/habit/{id}/range";

    private static final String GET_TOTAL_TIMES_PERFORMED_BY_MONTHS = PATH_BASE + "/monthly";

    private static final String PUT_BY_ID = PATH_BASE + "/{id}";

    private static final String DELETE_BY_ID = PATH_BASE + "/{id}";

    private static final String GET_HABIT_STREAK = PATH_BASE + "/habit/{id}/streak";

    private static final MediaType APP_JSON = Constants.App.APP_JSON;

    @BeforeEach
    void setUp() {

        habitProgressRequestMock = new HabitProgressDtoRequest();
        habitProgressRequestMock.setDate(DATE);
        habitProgressRequestMock.setTimesPerformed(TIMES_PERFORMED);
        habitProgressRequestMock.setHabitId(HABIT_ID);

        habitProgressResponseMock = new HabitProgressDtoResponse();
        habitProgressResponseMock.setId(HABIT_PROGRESS_ID);
        habitProgressResponseMock.setDate(DATE);
        habitProgressResponseMock.setTimesPerformed(TIMES_PERFORMED);
        habitProgressResponseMock.setHabitId(HABIT_ID);

        authToken = new TestingAuthenticationToken(String.valueOf(USER_ID), null);
    }

    @Test
    void byIdHabitOk() throws Exception {

        when(habitProgressService.findByHabitId(HABIT_ID, USER_ID))
            .thenReturn(List.of(habitProgressResponseMock));

        mockMvc.perform(
            get(GET_BY_ID_HABIT, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(HABIT_PROGRESS_ID))
        .andExpect(jsonPath("$[0].date").value(DATE.toString()))
        .andExpect(jsonPath("$[0].timesPerformed").value(TIMES_PERFORMED))
        .andExpect(jsonPath("$[0].habitId").value(HABIT_ID));
    }

    @Test
    void byIdHabitUnauthorized() throws Exception {

        mockMvc.perform(
            get(GET_BY_ID_HABIT, HABIT_ID)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void byIdHabitForbidden() throws Exception {

        when(habitProgressService.findByHabitId(HABIT_ID, USER_ID))
            .thenThrow(new ForbiddenAccessException());

        mockMvc.perform(
            get(GET_BY_ID_HABIT, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void byHabitIdNotFound() throws Exception {

        when(habitProgressService.findByHabitId(HABIT_ID, USER_ID))
            .thenThrow(new ResourceNotFoundException());

        mockMvc.perform(
            get(GET_BY_ID_HABIT, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void byIdOk() throws Exception {

        when(habitProgressService.findById(HABIT_ID, USER_ID))
            .thenReturn(habitProgressResponseMock);

        mockMvc.perform(
            get(GET_BY_ID, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(HABIT_PROGRESS_ID))
        .andExpect(jsonPath("$.date").value(DATE.toString()))
        .andExpect(jsonPath("$.timesPerformed").value(TIMES_PERFORMED))
        .andExpect(jsonPath("$.habitId").value(HABIT_ID));
    }

    @Test
    void byIdUnauthorized() throws Exception {

        mockMvc.perform(
            get(GET_BY_ID, HABIT_ID)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void byIdForbidden() throws Exception {

        when(habitProgressService.findById(HABIT_ID, USER_ID))
            .thenThrow(new ForbiddenAccessException());

        mockMvc.perform(
            get(GET_BY_ID, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void byIdNotFound() throws Exception {

        when(habitProgressService.findById(HABIT_ID, USER_ID))
            .thenThrow(new ResourceNotFoundException());

        mockMvc.perform(
            get(GET_BY_ID, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void byIdHabitAndDateRangeOk() throws Exception {

        when(habitProgressService.findByHabitIdAndDateBetween(HABIT_ID, DATE, DATE, USER_ID))
            .thenReturn(List.of(habitProgressResponseMock));

        mockMvc.perform(
            get(GET_BY_ID_HABIT_AND_DATE_RANGE, HABIT_ID)
                .param(START_DATE, DATE_FORMATTED)
                .param(END_DATE, DATE_FORMATTED)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(HABIT_PROGRESS_ID))
        .andExpect(jsonPath("$[0].date").value(DATE.toString()))
        .andExpect(jsonPath("$[0].timesPerformed").value(TIMES_PERFORMED))
        .andExpect(jsonPath("$[0].habitId").value(HABIT_ID));
    }

    @Test
    void byIdHabitAndDateRangeUnauthorized() throws Exception {
        mockMvc.perform(
            get(GET_BY_ID_HABIT_AND_DATE_RANGE, HABIT_ID)
                .param(START_DATE, DATE_FORMATTED)
                .param(END_DATE, DATE_FORMATTED)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void byIdHabitAndDateRangeForbidden() throws Exception {

        when(habitProgressService.findByHabitIdAndDateBetween(HABIT_ID, DATE, DATE, USER_ID))
            .thenThrow(new ForbiddenAccessException());

        mockMvc.perform(
            get(GET_BY_ID_HABIT_AND_DATE_RANGE, HABIT_ID)
                .param(START_DATE, DATE_FORMATTED)
                .param(END_DATE, DATE_FORMATTED)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void byIdHabitAndDateRangeNotFound() throws Exception {

        when(habitProgressService.findByHabitIdAndDateBetween(HABIT_ID, DATE, DATE, USER_ID))
            .thenThrow(new ResourceNotFoundException());

        mockMvc.perform(
            get(GET_BY_ID_HABIT_AND_DATE_RANGE, HABIT_ID)
                .param(START_DATE, DATE_FORMATTED)
                .param(END_DATE, DATE_FORMATTED)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void getTotalTimesPerformedByMonthsOk() throws Exception {

        when(habitProgressService.getTotalTimesPerformedByMonths(YEARS, HABIT_IDS, USER_ID))
            .thenReturn(List.of(getMonthlyResponse()));

        mockMvc.perform(
            get(GET_TOTAL_TIMES_PERFORMED_BY_MONTHS)
                .param(YEARS_S, YEARS_PARAM)
                .param(HABITS_IDS_S, HABIT_IDS_PARAM)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].year").value(DATE.getYear()))
        .andExpect(jsonPath("$[0].month").value(DATE.getMonthValue()))
        .andExpect(jsonPath("$[0].totalTimesPerformed")
        .value(TIMES_PERFORMED.longValue()))
        .andExpect(jsonPath("$[0].habitId").value(HABIT_ID));
    }

    @Test
    void getTotalTimesPerformedByMonthsUnauthorized() throws Exception {
        mockMvc.perform(
            get(GET_TOTAL_TIMES_PERFORMED_BY_MONTHS)
                .param(YEARS_S, YEARS_PARAM)
                .param(HABITS_IDS_S, HABIT_IDS_PARAM)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void getTotalTimesPerformedByMonthsForbidden() throws Exception {

        when(habitProgressService.getTotalTimesPerformedByMonths(YEARS, HABIT_IDS, USER_ID))
            .thenThrow(new ForbiddenAccessException());

        mockMvc.perform(
            get(GET_TOTAL_TIMES_PERFORMED_BY_MONTHS)
                .param(YEARS_S, YEARS_PARAM)
                .param(HABITS_IDS_S, HABIT_IDS_PARAM)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void getTotalTimesPerformedByMonthsNotFound() throws Exception {

        when(habitProgressService.getTotalTimesPerformedByMonths(YEARS, HABIT_IDS, USER_ID))
            .thenThrow(new ResourceNotFoundException());

        mockMvc.perform(
            get(GET_TOTAL_TIMES_PERFORMED_BY_MONTHS)
            .param(YEARS_S, YEARS_PARAM)
            .param(HABITS_IDS_S, HABIT_IDS_PARAM)
            .principal(authToken)
            .contentType(APP_JSON)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void createOk() throws Exception {

        when(habitProgressService.save(habitProgressRequestMock, USER_ID))
            .thenReturn(habitProgressResponseMock);

        String jsonRequest = objectMapper.writeValueAsString(habitProgressRequestMock);

        mockMvc.perform(
            post(PATH_BASE)
            .principal(authToken)
            .contentType(APP_JSON)
            .content(jsonRequest)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(HABIT_PROGRESS_ID))
        .andExpect(jsonPath("$.date").value(DATE.toString()))
        .andExpect(jsonPath("$.timesPerformed").value(TIMES_PERFORMED))
        .andExpect(jsonPath("$.habitId").value(HABIT_ID));
    }

    @Test
    void createBadRequest() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(habitProgressBadRequest());

        mockMvc.perform(
            post(PATH_BASE)
            .principal(authToken)
            .contentType(APP_JSON)
            .content(jsonRequest)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void createUnauthorized() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(habitProgressRequestMock);

        mockMvc.perform(
            post(PATH_BASE)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void createForbidden() throws Exception {

        when(habitProgressService.save(habitProgressRequestMock, USER_ID))
            .thenThrow(new ForbiddenAccessException());

        String jsonRequest = objectMapper.writeValueAsString(habitProgressRequestMock);

        mockMvc.perform(
            post(PATH_BASE)
            .principal(authToken)
            .contentType(APP_JSON)
            .content(jsonRequest)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void createNotFound() throws Exception {

        when(habitProgressService.save(habitProgressRequestMock, USER_ID))
            .thenThrow(new ResourceNotFoundException());

        String jsonRequest = objectMapper.writeValueAsString(habitProgressRequestMock);

        mockMvc.perform(
            post(PATH_BASE)
            .principal(authToken)
            .contentType(APP_JSON)
            .content(jsonRequest)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void updateOk() throws Exception {

        habitProgressResponseMock.setTimesPerformed(TIMES_PERFORMED_UPDATE);
        habitProgressResponseMock.setNote(NOTE_UPDATE);

        when(habitProgressService.update(HABIT_PROGRESS_ID, getUpdateRequest(), USER_ID))
            .thenReturn(habitProgressResponseMock);

        String jsonRequest = objectMapper.writeValueAsString(getUpdateRequest());

        mockMvc.perform(
            put(PUT_BY_ID, HABIT_PROGRESS_ID)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.timesPerformed").value(TIMES_PERFORMED_UPDATE))
        .andExpect(jsonPath("$.note").value(NOTE_UPDATE));
    }

    @Test
    void updateBadRequest() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString("");

        mockMvc.perform(
            put(PUT_BY_ID, HABIT_PROGRESS_ID)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void updateUnauthorized() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(getUpdateRequest());

        mockMvc.perform(
            put(PUT_BY_ID, HABIT_PROGRESS_ID)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void updateForbidden() throws Exception {

        when(habitProgressService.update(HABIT_ID, getUpdateRequest(), USER_ID))
            .thenThrow(new ForbiddenAccessException());

        String jsonRequest = objectMapper.writeValueAsString(getUpdateRequest());

        mockMvc.perform(
            put(PUT_BY_ID, HABIT_PROGRESS_ID)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void updateNotFound() throws Exception {

        when(habitProgressService.update(HABIT_ID, getUpdateRequest(), USER_ID))
            .thenThrow(new ResourceNotFoundException());

        String jsonRequest = objectMapper.writeValueAsString(getUpdateRequest());

        mockMvc.perform(
            put(PUT_BY_ID, HABIT_PROGRESS_ID)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void deleteOk() throws Exception {

        doNothing().when(habitProgressService).deleteById(HABIT_PROGRESS_ID, USER_ID);

        mockMvc.perform(
            delete(DELETE_BY_ID, HABIT_PROGRESS_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isNoContent());

        verify(habitProgressService).deleteById(HABIT_PROGRESS_ID, USER_ID);
    }

    @Test
    void deleteUnauthorized() throws Exception {

        mockMvc.perform(
            delete(DELETE_BY_ID, HABIT_PROGRESS_ID)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteForbidden() throws Exception {

        doThrow(new ForbiddenAccessException()).when(habitProgressService)
            .deleteById(HABIT_PROGRESS_ID, USER_ID);

        mockMvc.perform(
            delete(DELETE_BY_ID, HABIT_PROGRESS_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void deleteNotFound() throws Exception {

        doThrow(new ResourceNotFoundException()).when(habitProgressService)
            .deleteById(HABIT_PROGRESS_ID, USER_ID);

        mockMvc.perform(
            delete(DELETE_BY_ID, HABIT_PROGRESS_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void getHabitStreakOk() throws Exception {

        int habitStreak = 1;

        when(habitProgressService.getStreak(HABIT_ID, USER_ID)).thenReturn(habitStreak);

        mockMvc.perform(
            get(GET_HABIT_STREAK, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(habitStreak));
    }

    @Test
    void getStreakUnauthorized() throws Exception {
        mockMvc.perform(
            get(GET_HABIT_STREAK, HABIT_ID)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void getStreakForbidden() throws Exception {

        when(habitProgressService.getStreak(HABIT_ID, USER_ID))
            .thenThrow(new ForbiddenAccessException());

        mockMvc.perform(
            get(GET_HABIT_STREAK, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void getStreakNotFound() throws Exception {

        when(habitProgressService.getStreak(HABIT_ID, USER_ID))
            .thenThrow(new ResourceNotFoundException());

        mockMvc.perform(
            get(GET_HABIT_STREAK, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isNotFound());
    }

    private HabitProgressDtoMonthlyResponse getMonthlyResponse() {
        return HabitProgressDtoMonthlyResponse.builder()
            .year(DATE.getYear())
            .month(DATE.getMonthValue())
            .totalTimesPerformed(TIMES_PERFORMED.longValue())
            .habitId(HABIT_ID)
            .build();
    }

    private HabitProgressDtoUpdateRequest getUpdateRequest() {
        return HabitProgressDtoUpdateRequest.builder()
            .timesPerformed(TIMES_PERFORMED_UPDATE)
            .note(NOTE_UPDATE)
            .build();
    }

    private HabitProgressDtoRequest habitProgressBadRequest() {
        return HabitProgressDtoRequest.builder().build();
    }
}
