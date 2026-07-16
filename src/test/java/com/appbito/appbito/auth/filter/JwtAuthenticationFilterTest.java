package com.appbito.appbito.auth.filter;

import static com.appbito.appbito.auth.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.appbito.appbito.auth.TokenJwtConfig.PREFIX_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.appbito.appbito.auth.models.CustomUserDetails;
import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.exceptions.AccountNotVerifiedException;
import com.appbito.appbito.utils.AuthUtils;
import jakarta.servlet.ServletException;
import java.io.IOException;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Jwt Authentication Filter Test.
 */
class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;

    private SecretKey secretKey;

    private AuthenticationManager authManager;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private MockFilterChain filterChain;

    private static final String SECRET_KEY_STRING =
        "un-secreto-muy-largo-para-pruebas-unitarias-1234567890";

    private static final boolean SECURE_COOKIE = false;

    private static final String LOGIN_JSON =
        "{\"email\":\"test@email.com\", \"password\":\"123456\"}";

    private static final String FAKE_JSON = "fake-json";

    private static final Long USER_ID = 1L;

    private static final String EMAIL = "test@email.com";

    private static final String PASSWORD = "password";

    private static final String BAD_PASSWORD = "Bad password";

    private static final String ACCOUNT_NOT_VERIFIED = "Account not verified";

    @BeforeEach
    void setUp() {

        authManager = mock(AuthenticationManager.class);

        secretKey = AuthUtils.stringToSecretKey(SECRET_KEY_STRING);

        filter = new JwtAuthenticationFilter(authManager, secretKey, SECURE_COOKIE);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();

        SecurityContextHolder.clearContext();
    }

    @Test
    void attemptAuthenticationOk() {

        request.setContent(LOGIN_JSON.getBytes());

        Authentication mockAuth = mock(Authentication.class);
        when(authManager.authenticate(any())).thenReturn(mockAuth);

        Authentication result = filter.attemptAuthentication(request, response);

        assertNotNull(result);
        assertEquals(mockAuth, result);
    }

    @Test
    void attemptAuthenticationException() {

        request.setContent(FAKE_JSON.getBytes());

        assertThrows(AuthenticationServiceException.class, () -> {
            filter.attemptAuthentication(request, response);
        });
    }

    @Test
    void successfulAuthenticationOk() throws IOException, ServletException {

        Authentication authResult = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(USER_ID, EMAIL, PASSWORD);

        when(authResult.getPrincipal()).thenReturn(userDetails);

        filter.successfulAuthentication(request, response, filterChain, authResult);

        assertTrue(response.containsHeader(HEADER_AUTHORIZATION));
        assertTrue(response.getHeader(HEADER_AUTHORIZATION).startsWith(PREFIX_TOKEN));

        assertNotNull(response.getCookie(Constants.Auth.REFRESH_TOKEN));
    }

    @Test
    void unsuccessfulAuthentication() throws IOException, ServletException {

        BadCredentialsException exception = new BadCredentialsException(BAD_PASSWORD);

        filter.unsuccessfulAuthentication(request, response, exception);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        String responseBody = response.getContentAsString();
        assertTrue(responseBody.contains(Constants.Errors.LOGIN_ERROR));
    }

    @Test
    void unsuccessfulAuthenticationAccountNotEnabled() throws IOException, ServletException {

        AccountNotVerifiedException exception =
            new AccountNotVerifiedException(ACCOUNT_NOT_VERIFIED);

        filter.unsuccessfulAuthentication(request, response, exception);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        String responseBody = response.getContentAsString();
        assertTrue(responseBody.contains(Constants.Exceptions.USER_NOT_ENABLED));
    }
}
