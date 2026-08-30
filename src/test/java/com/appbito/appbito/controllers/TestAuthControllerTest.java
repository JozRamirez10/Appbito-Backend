package com.appbito.appbito.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.repositories.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test Auth Controller Test.
 */
@WebMvcTest(controllers = TestAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TestAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    private static final String EMAIL = "test@test.com";

    private static final String TOKEN = "87400049-6546-4e14-acc7-c2095ee0646d";

    private static final String PATH_BASE = "/test/auth";

    private static final String GET_VERIFICATION_TOKEN = PATH_BASE + "/token";

    @Test
    void getVerificationTokenOk() throws Exception {

        when(userRepository.findVerificationTokenByEmail(EMAIL))
            .thenReturn(Optional.of(TOKEN));

        mockMvc.perform(
            get(GET_VERIFICATION_TOKEN)
                .param(Constants.General.EMAIL, EMAIL)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.verificationToken").value(TOKEN));
    }

    @Test
    void getVerificationTokenNotFound() throws Exception {

        when(userRepository.findVerificationTokenByEmail(EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(
            get(GET_VERIFICATION_TOKEN)
            .param(Constants.General.EMAIL, EMAIL)
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error")
            .value(Constants.Exceptions.RESOURCE_NOT_FOUND + ": "
                + Constants.Errors.USER_NOT_FOUND));
    }

}
