package com.appbito.appbito.auth.filter;

import static com.appbito.appbito.auth.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.appbito.appbito.auth.TokenJwtConfig.PREFIX_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.appbito.appbito.constants.Constants;
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
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Jwt Validation Filter Test.
 */
class JwtValidationFilterTest {

    private JwtValidationFilter filter;

    private SecretKey secretKey;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private MockFilterChain filterChain;

    private static final String SECRET_KEY_STRING =
        "un-secreto-muy-largo-para-pruebas-unitarias-1234567890";

    private static final String USER_ID_STRING = "1";

    private static final String INVALID_TOKEN = "invalid-token";

    @BeforeEach
    void setUp() {

        AuthenticationManager authManager = mock(AuthenticationManager.class);

        secretKey = AuthUtils.stringToSecretKey(SECRET_KEY_STRING);

        filter = new JwtValidationFilter(authManager, secretKey);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();

        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternalOk() throws IOException, ServletException {

        String validToken = AuthUtils.generateAccessToken(USER_ID_STRING, secretKey);

        request.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + validToken);

        filter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(USER_ID_STRING, authentication.getPrincipal());
    }

    @Test
    void doFilterInternalTokenNull() throws IOException, ServletException {

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalTokenWithoutPrefix() throws IOException, ServletException {

        String validToken = AuthUtils.generateAccessToken(USER_ID_STRING, secretKey);

        request.addHeader(HEADER_AUTHORIZATION, validToken);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalTokenInvalid() throws IOException, ServletException {

        request.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + INVALID_TOKEN);

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        String responseBody = response.getContentAsString();
        assertTrue(responseBody.contains(Constants.Exceptions.INVALID_TOKEN));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
