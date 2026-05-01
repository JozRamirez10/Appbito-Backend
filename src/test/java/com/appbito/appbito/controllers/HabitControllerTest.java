package com.appbito.appbito.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.appbito.appbito.dtos.HabitDtoRequest;
import com.appbito.appbito.dtos.HabitDtoResponse;
import com.appbito.appbito.exceptions.ForbiddenAccessException;
import com.appbito.appbito.services.HabitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = HabitController.class)
@AutoConfigureMockMvc(addFilters = false) // Turns off jwt filters
@SuppressWarnings("null")
class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HabitService habitService;

    private HabitDtoResponse habitResponseMock;

    private HabitDtoRequest habitRequestMock;

    private TestingAuthenticationToken authToken;

    private static final String HABIT_NAME = "Do Yoga";

    private static final Long HABIT_ID = 100L;

    private static final Long USER_ID = 5L;

    private static final Long USER_ID_ERROR = 2L;

    private static final String PATH_BASE = "/api/habits";

    private static final String GET_BY_ID = PATH_BASE + "/{id}";

    private static final String GET_BY_USER_ID = PATH_BASE + "/user/{id}";

    private static final String GET_TODAY = PATH_BASE + "/today";

    private static final String PUT_BY_ID = PATH_BASE + "/{id}";

    private static final String PUT_BATCH = PATH_BASE + "/batch";

    private static final String DELETE_BY_ID = PATH_BASE + "/{id}";

    private static final MediaType APP_JSON = MediaType.APPLICATION_JSON;

    @BeforeEach
    void setUp() {

        habitRequestMock = new HabitDtoRequest();
        habitRequestMock.setName(HABIT_NAME);
        habitRequestMock.setDays(Set.of(DayOfWeek.MONDAY));

        habitResponseMock = new HabitDtoResponse();
        habitResponseMock.setId(HABIT_ID);
        habitResponseMock.setName(HABIT_NAME);
        habitResponseMock.setUserId(USER_ID);

        authToken = new TestingAuthenticationToken(String.valueOf(USER_ID), null);
    }

    @Test
    void findAllOk() throws Exception {

        when(habitService.findAll()).thenReturn(List.of(habitResponseMock));

        mockMvc.perform(
            get(PATH_BASE)
                .contentType(APP_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(HABIT_ID))
        .andExpect(jsonPath("$[0].name").value(HABIT_NAME));
    }

    @Test
    void byIdOk() throws Exception {

        when(habitService.findById(HABIT_ID)).thenReturn(Optional.of(habitResponseMock));

        mockMvc.perform(
            get(GET_BY_ID, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(HABIT_NAME));
    }

    @Test
    void byIdUnauthorized() throws Exception {

        when(habitService.findById(HABIT_ID)).thenReturn(Optional.of(habitResponseMock));

        mockMvc.perform(
            get(GET_BY_ID, HABIT_ID)
                .principal(invalidToken())
                .contentType(APP_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void byIdForbidden() throws Exception {

        when(habitService.findById(HABIT_ID)).thenReturn(Optional.of(habitForbidden()));

        mockMvc.perform(
            get(GET_BY_ID, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isForbidden());
    }


    @Test
    void byIdNotFound() throws Exception {

        when(habitService.findById(HABIT_ID)).thenReturn(Optional.empty());

        mockMvc.perform(
            get(GET_BY_ID, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void byUserIdOk() throws Exception {

        when(habitService.findByUserId(USER_ID)).thenReturn(List.of(habitResponseMock));

        mockMvc.perform(
            get(GET_BY_USER_ID, USER_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(HABIT_ID))
        .andExpect(jsonPath("$[0].name").value(HABIT_NAME));
    }

    @Test
    void byUserIdUnauthorized() throws Exception {

        mockMvc.perform(
            get(GET_BY_USER_ID, USER_ID)
                .principal(invalidToken())
                .contentType(APP_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void byUserIdForbidden() throws Exception {

        mockMvc.perform(
            get(GET_BY_USER_ID, USER_ID_ERROR)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void todayOk() throws Exception {

        when(habitService.findByToday(USER_ID)).thenReturn(List.of(habitResponseMock));

        mockMvc.perform(
            get(GET_TODAY)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(HABIT_ID))
        .andExpect(jsonPath("$[0].name").value(HABIT_NAME));

    }

    @Test
    void todayUnauthorized() throws Exception {

        mockMvc.perform(
            get(GET_TODAY)
                .principal(invalidToken())
                .contentType(APP_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void createOk() throws Exception {

        when(habitService.save(any(HabitDtoRequest.class), eq(USER_ID)))
            .thenReturn(habitResponseMock);

        String jsonRequest = objectMapper.writeValueAsString(habitRequestMock);

        mockMvc.perform(
            post(PATH_BASE)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(HABIT_ID))
        .andExpect(jsonPath("$.name").value(HABIT_NAME))
        .andExpect(jsonPath("$.userId").value(USER_ID));
    }

    @Test
    void createBadRequest() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(habitBadRequest());

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

        String jsonRequest = objectMapper.writeValueAsString(habitRequestMock);

        mockMvc.perform(
            post(PATH_BASE)
                .principal(invalidToken())
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void updateOk() throws Exception {

        when(habitService.update(any(HabitDtoRequest.class), eq(HABIT_ID), eq(USER_ID)))
            .thenReturn(Optional.of(habitResponseMock));

        String jsonRequest = objectMapper.writeValueAsString(habitRequestMock);

        mockMvc.perform(
            put(PUT_BY_ID, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(HABIT_ID))
            .andExpect(jsonPath("$.name").value(HABIT_NAME));
    }

    @Test
    void updateBadRequest() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(habitBadRequest());

        mockMvc.perform(
            put(PUT_BY_ID, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void updateUnauthorized() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(habitRequestMock);

        mockMvc.perform(
            put(PUT_BY_ID, HABIT_ID)
                .principal(invalidToken())
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void updateForbidden() throws Exception {

        when(habitService.update(any(HabitDtoRequest.class), eq(HABIT_ID), eq(USER_ID)))
            .thenThrow(new ForbiddenAccessException());

        String jsonRequest = objectMapper.writeValueAsString(habitRequestMock);

        mockMvc.perform(
            put(PUT_BY_ID, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void updateNotFound() throws Exception {

        when(habitService.update(any(HabitDtoRequest.class), eq(HABIT_ID), eq(USER_ID)))
            .thenReturn(Optional.empty());

        String jsonRequest = objectMapper.writeValueAsString(habitRequestMock);

        mockMvc.perform(
            put(PUT_BY_ID, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void updateBatchOk() throws Exception {

        when(habitService.updateBatch(anyList(), eq(USER_ID)))
            .thenReturn(List.of(habitResponseMock));

        List<HabitDtoRequest> batchRequest = List.of(habitRequestMock);
        String jsonRequest = objectMapper.writeValueAsString(batchRequest);

        mockMvc.perform(
            put(PUT_BATCH)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(HABIT_ID))
        .andExpect(jsonPath("$[0].name").value(HABIT_NAME));
    }

    @Test
    void updateBatchBadRequest() throws Exception {

        List<HabitDtoRequest> batchRequest = List.of(habitBadRequest());
        String jsonRequest = objectMapper.writeValueAsString(batchRequest);

        mockMvc.perform(
            put(PUT_BATCH)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void updateBatchBadRequestEmptyList() throws Exception {

        String jsonRequest = "[]";

        mockMvc.perform(
            put(PUT_BATCH)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void updateBatchBadRequestNullList() throws Exception {

        mockMvc.perform(
            put(PUT_BATCH)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void updateBatchUnauthorized() throws Exception {

        List<HabitDtoRequest> batchRequest = List.of(habitRequestMock);
        String jsonRequest = objectMapper.writeValueAsString(batchRequest);

        mockMvc.perform(
            put(PUT_BATCH)
                .principal(invalidToken())
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void updateBatchForbidden() throws Exception {

        when(habitService.updateBatch(anyList(), eq(USER_ID)))
            .thenThrow(new ForbiddenAccessException());

        List<HabitDtoRequest> batchRequest = List.of(habitRequestMock);
        String jsonRequest = objectMapper.writeValueAsString(batchRequest);

        mockMvc.perform(
            put(PUT_BATCH)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void deleteOk() throws Exception {

        when(habitService.deleteById(HABIT_ID, USER_ID)).thenReturn(true);

        mockMvc.perform(
            delete(DELETE_BY_ID, HABIT_ID)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isNoContent());
    }

    @Test
    void deleteUnauthorized() throws Exception {

        mockMvc.perform(
            delete(DELETE_BY_ID, HABIT_ID)
                .principal(invalidToken())
                .contentType(APP_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteForbidden() throws Exception {

        when(habitService.deleteById(eq(HABIT_ID), eq(USER_ID)))
            .thenThrow(new ForbiddenAccessException());

        mockMvc.perform(
            delete(DELETE_BY_ID, HABIT_ID)
                .principal(authToken)
        )
        .andExpect(status().isForbidden());

    }

    @Test
    void deleteNotFound() throws Exception {

        when(habitService.deleteById(HABIT_ID, USER_ID)).thenReturn(false);

        mockMvc.perform(
            delete(DELETE_BY_ID, HABIT_ID)
                .principal(authToken)
        )
        .andExpect(status().isNotFound());
    }

    private HabitDtoRequest habitBadRequest () {
        return HabitDtoRequest.builder().build();
    }

    private HabitDtoResponse habitForbidden() {

        return HabitDtoResponse.builder()
            .id(HABIT_ID)
            .userId(6L)
            .build();
    }

    private TestingAuthenticationToken invalidToken() {
        return new TestingAuthenticationToken("invalid token", null);
    }

}
