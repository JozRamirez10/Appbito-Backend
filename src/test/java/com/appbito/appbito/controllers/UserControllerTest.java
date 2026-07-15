package com.appbito.appbito.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.requests.UserDtoDeleteRequest;
import com.appbito.appbito.dtos.requests.UserDtoRequest;
import com.appbito.appbito.dtos.requests.UserDtoUpdatePasswordRequest;
import com.appbito.appbito.dtos.requests.UserDtoUpdateRequest;
import com.appbito.appbito.dtos.responses.UserDtoResponse;
import com.appbito.appbito.exceptions.FileStorageException;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import com.appbito.appbito.services.ImageService;
import com.appbito.appbito.services.UserService;
import com.appbito.appbito.utils.AuthUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ImageService imageService;

    private UserDtoResponse userResponseMock;

    private UserDtoRequest userRequestMock;

    private TestingAuthenticationToken authToken;

    private static final Long USER_ID = 1L;

    private static final String USER_NAME = "subject";

    private static final String USER_LASTNAME = "test";

    private static final String USER_EMAIL = "test@email.com";

    private static final LocalDate USER_BIRTHDATE =
        LocalDate.of(2000, 12, 31);

    private static final String USER_PASSWORD = "password";

    private static final String USER_IMAGE = Constants.Files.PROFILE_PREFIX + USER_ID + "."
        + Constants.Files.JPG_EXTENSION;

    private static final String OLD_PASSWORD = "Old Password";

    private static final String NEW_PASSWORD = "New Password";

    private static final String PATH_TRANSVERSAL = "../malicious.jpg";

    private static final String PATH_BASE = "/api/users";

    private static final String GET_ME = PATH_BASE + "/me";

    private static final String GET_ME_IMAGE = PATH_BASE + "/me/image";

    private static final String POST_ME_IMAGE = PATH_BASE + "/me/image";

    private static final String PUT_ME = PATH_BASE + "/me";

    private static final String PUT_ME_PASSWORD = PATH_BASE + "/me/password";

    private static final String DELETE_ME = PATH_BASE + "/me";

    private static final String DELETE_ME_PASSWORD = PATH_BASE + "/me/password";

    private static final MediaType APP_JSON = Constants.App.APP_JSON;

    @BeforeEach
    void setUp() {

        userRequestMock = new UserDtoRequest();
        userRequestMock.setName(USER_NAME);
        userRequestMock.setLastname(USER_LASTNAME);
        userRequestMock.setEmail(USER_EMAIL);
        userRequestMock.setBirthDate(USER_BIRTHDATE);
        userRequestMock.setPassword(USER_PASSWORD);

        userResponseMock = new UserDtoResponse();
        userResponseMock.setId(USER_ID);
        userResponseMock.setName(USER_NAME);
        userResponseMock.setLastname(USER_LASTNAME);
        userResponseMock.setEmail(USER_EMAIL);

        authToken = new TestingAuthenticationToken(String.valueOf(USER_ID), null);
    }

    @Test
    void findAll() throws Exception {

        when(userService.findAll()).thenReturn(List.of(userResponseMock));

        mockMvc.perform(
            get(PATH_BASE)
                .contentType(APP_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(USER_ID))
        .andExpect(jsonPath("$[0].name").value(USER_NAME))
        .andExpect(jsonPath("$[0].email").value(USER_EMAIL));
    }

    @Test
    void meOk() throws Exception {

        when(userService.findById(USER_ID)).thenReturn(Optional.of(userResponseMock));

        mockMvc.perform(
            get(GET_ME)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(USER_NAME))
        .andExpect(jsonPath("$.lastname").value(USER_LASTNAME))
        .andExpect(jsonPath("$.email").value(USER_EMAIL));
    }

    @Test
    void meUnauthorized() throws Exception {

        mockMvc.perform(
            get(GET_ME)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)

        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void meNotFound() throws Exception {

        when(userService.findById(USER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(
            get(GET_ME)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void meImageOk() throws Exception {

        userResponseMock.setImage(USER_IMAGE);

        when(userService.findById(USER_ID)).thenReturn(Optional.of(userResponseMock));

        byte[] fakeImageBytes = "dummy image content".getBytes();
        Resource resourceMock = new ByteArrayResource(fakeImageBytes) {
            @Override
            public String getFilename() {
                return USER_IMAGE;
            }
        };

        when(imageService.loadAsResource(USER_IMAGE)).thenReturn(resourceMock);

        mockMvc.perform(
            get(GET_ME_IMAGE)
                .principal(authToken)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG))
        .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=\""+USER_IMAGE+"\""))
        .andExpect(content().bytes(fakeImageBytes));
    }

    @Test
    void meImageUnauthorized() throws Exception {
        mockMvc.perform(
            get(GET_ME_IMAGE)
            .principal(AuthUtils.invalidToken())
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void meImageUserNotFound() throws Exception {

        when(userService.findById(USER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(
            get(GET_ME_IMAGE)
            .principal(authToken)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void meImageNotFound() throws Exception {

        userResponseMock.setImage(null);
        when(userService.findById(USER_ID)).thenReturn(Optional.of(userResponseMock));

        mockMvc.perform(
            get(GET_ME_IMAGE)
            .principal(authToken)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void meImageSecurityException() throws Exception {

        userResponseMock.setImage(PATH_TRANSVERSAL);

        when(userService.findById(USER_ID)).thenReturn(Optional.of(userResponseMock));
        when(imageService.loadAsResource(PATH_TRANSVERSAL)).thenThrow(new SecurityException());

        mockMvc.perform(
            get(GET_ME_IMAGE)
            .principal(authToken)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void createOk() throws Exception {

        when(userService.save(any(UserDtoRequest.class))).thenReturn(userResponseMock);

        String jsonRequest = objectMapper.writeValueAsString(userRequestMock);

        mockMvc.perform(
            post(PATH_BASE)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(USER_ID))
        .andExpect(jsonPath("$.name").value(USER_NAME))
        .andExpect(jsonPath("$.lastname").value(USER_LASTNAME))
        .andExpect(jsonPath("$.email").value(USER_EMAIL));
    }

    @Test
    void createBadRequest() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(userBadRequest());

        mockMvc.perform(
            post(PATH_BASE)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void createEmailAlreadyExists() throws Exception {

        doThrow(new IllegalArgumentException(Constants.User.EMAIL_ALREADY_EXISTS))
            .when(userService).save(any(UserDtoRequest.class));

        String jsonRequest = objectMapper.writeValueAsString(userRequestMock);

        mockMvc.perform(
            post(PATH_BASE)
            .contentType(APP_JSON)
            .content(jsonRequest)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void uploadProfileImageMeOk() throws Exception {

        MockMultipartFile fakeImage = imageRequest();

        when(imageService.storeProfileImage(any(MultipartFile.class), eq(USER_ID)))
            .thenReturn(USER_IMAGE);

        userResponseMock.setImage(USER_IMAGE);

        when(userService.saveImage(USER_ID, USER_IMAGE)).thenReturn(userResponseMock);

        mockMvc.perform(
            multipart(POST_ME_IMAGE)
                .file(fakeImage)
                .principal(authToken)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(USER_ID))
        .andExpect(jsonPath("$.image").value(USER_IMAGE));
    }

    @Test
    void uploadProfileImageUnauthorized() throws Exception {
        mockMvc.perform(
            multipart(POST_ME_IMAGE)
            .file(imageRequest())
            .principal(AuthUtils.invalidToken())
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadProfileImageFileStorageException() throws Exception {

        when(imageService.storeProfileImage(any(MultipartFile.class), eq(USER_ID)))
            .thenThrow(new FileStorageException(Constants.Files.FAILED_TO_STORE));

        mockMvc.perform(
            multipart(POST_ME_IMAGE)
            .file(imageRequest())
            .principal(authToken)
        )
        .andExpect(status().isInternalServerError());
    }

    @Test
    void updateMeOk() throws Exception {

        when(userService.update(any(UserDtoUpdateRequest.class), eq(USER_ID)))
            .thenReturn(Optional.of(userResponseMock));

        String jsonRequest = objectMapper.writeValueAsString(userDtoRequestToDtoUpdateRequest());

        mockMvc.perform(
            put(PUT_ME)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(USER_ID))
        .andExpect(jsonPath(".name").value(USER_NAME))
        .andExpect(jsonPath("$.lastname").value(USER_LASTNAME));
    }

    @Test
    void updateMeBadRequest() throws Exception {

        UserDtoUpdateRequest updateRequest = new UserDtoUpdateRequest().builder().build();

        String jsonRequest = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(
            put(PUT_ME)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void updateMeUnauthorized() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(userDtoRequestToDtoUpdateRequest());

        mockMvc.perform(
            put(PUT_ME)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void updateMeNotFound() throws Exception {

        when(userService.update(any(UserDtoUpdateRequest.class), eq(USER_ID)))
            .thenReturn(Optional.empty());

        String jsonRequest = objectMapper.writeValueAsString(userDtoRequestToDtoUpdateRequest());

        mockMvc.perform(
            put(PUT_ME)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void updateMePasswordOk() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(updatePasswordRequest());

        doNothing().when(userService).updatePassword(
            eq(USER_ID),
            eq(OLD_PASSWORD),
            eq(NEW_PASSWORD)
        );

        mockMvc.perform(
            put(PUT_ME_PASSWORD)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isOk());
    }

    @Test
    void updateMePasswordBadRequest() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(updatePasswordBadRequest());

        mockMvc.perform(
            put(PUT_ME_PASSWORD)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void updateMePasswordUnauthorized() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(updatePasswordRequest());

        mockMvc.perform(
            put(PUT_ME_PASSWORD)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteMeOk() throws Exception {

        when(userService.deleteById(USER_ID)).thenReturn(true);

        mockMvc.perform(
            delete(DELETE_ME)
                .principal(authToken)
                .contentType(APP_JSON)
        )
        .andExpect(status().isNoContent());
    }

    @Test
    void deleteMeUnauthorized() throws Exception {

        mockMvc.perform(
            delete(DELETE_ME)
            .principal(AuthUtils.invalidToken())
            .contentType(APP_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteMeNotFound() throws Exception {

        when(userService.deleteById(USER_ID)).thenReturn(false);

        mockMvc.perform(
            delete(DELETE_ME)
            .principal(authToken)
            .contentType(APP_JSON)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void deleteMePasswordOk() throws Exception {

        doNothing().when(userService).deleteByIdAndPassword(
            eq(USER_ID), eq(USER_PASSWORD));

        String jsonRequest = objectMapper.writeValueAsString(deletePasswordRequest());

        mockMvc.perform(
            delete(DELETE_ME_PASSWORD)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isNoContent());
    }

    @Test
    void deleteMePasswordBadRequest() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(deletePasswordBadRequest());

        mockMvc.perform(
            delete(DELETE_ME_PASSWORD)
            .principal(authToken)
            .contentType(APP_JSON)
            .content(jsonRequest)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMePasswordNotMatches() throws Exception {

        doThrow(new IllegalArgumentException(Constants.User.PASSWORD_DOES_NOT_MATCH))
            .when(userService).deleteByIdAndPassword(eq(USER_ID), eq(USER_PASSWORD));

        String jsonRequest = objectMapper.writeValueAsString(deletePasswordRequest());

        mockMvc.perform(
            delete(DELETE_ME_PASSWORD)
                .principal(authToken)
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMePasswordUnauthorized() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(deletePasswordRequest());

        mockMvc.perform(
            delete(DELETE_ME_PASSWORD)
                .principal(AuthUtils.invalidToken())
                .contentType(APP_JSON)
                .content(jsonRequest)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteMePasswordNotFound() throws Exception {

        doThrow(new ResourceNotFoundException()).when(userService)
            .deleteByIdAndPassword(eq(USER_ID), eq(USER_PASSWORD));

        String jsonRequest = objectMapper.writeValueAsString(deletePasswordRequest());

        mockMvc.perform(
            delete(DELETE_ME_PASSWORD)
            .principal(authToken)
            .contentType(APP_JSON)
            .content(jsonRequest)
        )
        .andExpect(status().isNotFound());
    }

    private UserDtoRequest userBadRequest() {
        return UserDtoRequest.builder().build();
    }

    private UserDtoUpdateRequest userDtoRequestToDtoUpdateRequest() {
        return UserDtoUpdateRequest.builder()
            .name(USER_NAME)
            .lastname(USER_LASTNAME)
            .birthDate(USER_BIRTHDATE)
            .build();
    }

    private UserDtoUpdatePasswordRequest updatePasswordRequest() {
        return UserDtoUpdatePasswordRequest.builder()
            .oldPassword(GET_ME)
            .newPassword(GET_ME)
            .build();
    }

    private UserDtoUpdatePasswordRequest updatePasswordBadRequest() {
        return UserDtoUpdatePasswordRequest.builder().build();
    }

    private UserDtoDeleteRequest deletePasswordRequest() {
        return UserDtoDeleteRequest.builder()
            .password(USER_PASSWORD)
            .build();
    }

    private UserDtoDeleteRequest deletePasswordBadRequest() {
        return UserDtoDeleteRequest.builder().build();
    }

    private MockMultipartFile imageRequest() {
        return new MockMultipartFile(
            "image",
            "fake-image.jpg",
            "image/jpeg",
            "fake image bytes".getBytes()
        );
    }

}
