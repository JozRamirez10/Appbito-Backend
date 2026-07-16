package com.appbito.appbito.controllers;

import static com.appbito.appbito.auth.TokenJwtConfig.HEADER_AUTHORIZATION;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.responses.UserDtoResponse;
import com.appbito.appbito.services.UserService;
import com.appbito.appbito.utils.AuthUtils;
import jakarta.servlet.http.Cookie;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Auth Controller Test.
 */
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Value("${jwt.secret}")
    private String secretString;

    private static final String USER_ID_STRING = "1";

    private static final Long USER_ID = 1L;

    private static final String USER_NAME = "username";

    private static final String USER_EMAIL = "user@email.com";

    private static final boolean USER_ENABLED = true;

    private static final String TOKEN = "token";

    private static final String FAKE_TOKEN = "fake-token";

    private static final String PATH_BASE = "/auth";

    private static final String POST_REFRESH_TOKEN = PATH_BASE + "/refresh";

    private static final String POST_LOGOUT = PATH_BASE + "/logout";

    private static final String GET_VERIFY_ACCOUNT = PATH_BASE + "/verify";

    @Test
    void refreshTokenOk() throws Exception {

        SecretKey secretKey = AuthUtils.stringToSecretKey(secretString);

        String validRefreshToken = AuthUtils.generateRefreshToken(USER_ID_STRING, secretKey);

        Cookie refreshCookie = new Cookie(Constants.Auth.REFRESH_TOKEN, validRefreshToken);

        mockMvc.perform(
            post(POST_REFRESH_TOKEN)
                .cookie(refreshCookie)
        )
        .andExpect(status().isOk())
        .andExpect(header().exists(HEADER_AUTHORIZATION));
    }

    @Test
    void refreshTokenNull() throws Exception {

        mockMvc.perform(
            post(POST_REFRESH_TOKEN)
        )
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error")
            .value(Constants.Exceptions.REFRESH_TOKEN_MISSING));
    }

    @Test
    void refreshTokenBlank() throws Exception {

        Cookie refreshCookie = new Cookie(Constants.Auth.REFRESH_TOKEN,
            Constants.General.EMPTY_STRING);

        mockMvc.perform(
            post(POST_REFRESH_TOKEN)
                .cookie(refreshCookie)
        )
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error")
            .value(Constants.Exceptions.REFRESH_TOKEN_MISSING));
    }

    @Test
    void refreshTokenInvalid() throws Exception {

        Cookie badCookie = new Cookie(Constants.Auth.REFRESH_TOKEN, FAKE_TOKEN);

        mockMvc.perform(
            post(POST_REFRESH_TOKEN)
                .cookie(badCookie)
        )
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error")
            .value(Constants.Exceptions.INVALID_REFRESH_TOKEN));
    }

    @Test
    void logoutOk() throws Exception {

        mockMvc.perform(
            post(POST_LOGOUT)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(Constants.Auth.LOGOUT_SUCCESS));
    }

    @Test
    void verifyAccountOk() throws Exception {

        UserDtoResponse userDtoResponse = UserDtoResponse.builder()
            .id(USER_ID)
            .name(USER_NAME)
            .email(USER_EMAIL)
            .enabled(USER_ENABLED)
            .build();

        when(userService.enableUserByVerificationToken(TOKEN))
            .thenReturn(Optional.of(userDtoResponse));

        mockMvc.perform(
            get(GET_VERIFY_ACCOUNT)
            .param(TOKEN, TOKEN)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(USER_ID))
        .andExpect(jsonPath("$.name").value(USER_NAME))
        .andExpect(jsonPath("$.email").value(USER_EMAIL))
        .andExpect(jsonPath("$.enabled").value(USER_ENABLED));
    }

    @Test
    void verifyAccountNotFound() throws Exception {

        when(userService.enableUserByVerificationToken(TOKEN)).thenReturn(Optional.empty());

        mockMvc.perform(
            get(GET_VERIFY_ACCOUNT)
                .param(TOKEN, TOKEN)
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error")
            .value(Constants.Exceptions.RESOURCE_NOT_FOUND + ": "
                + Constants.Exceptions.TOKEN_NOT_FOUND));
    }
}
