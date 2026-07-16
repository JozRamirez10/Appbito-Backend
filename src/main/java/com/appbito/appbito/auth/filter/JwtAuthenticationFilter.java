package com.appbito.appbito.auth.filter;

import static com.appbito.appbito.auth.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.appbito.appbito.auth.TokenJwtConfig.PREFIX_TOKEN;

import com.appbito.appbito.auth.models.CustomUserDetails;
import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.requests.LoginDtoRequest;
import com.appbito.appbito.exceptions.AccountNotVerifiedException;
import com.appbito.appbito.utils.AuthUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Jwt Authentication Filter.
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SecretKey secretKey;

    private final boolean secureCookie;

    /**
     * method constructor.
     *
     * @param authenticationManager authentication manager.
     * @param secretKey secret key.
     * @param secureCookie secure cookite to https.
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
        SecretKey secretKey, boolean secureCookie) {

        this.setAuthenticationManager(authenticationManager);
        this.secretKey = secretKey;
        this.secureCookie = secureCookie;

        setUsernameParameter(Constants.General.EMAIL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {

        try {
            LoginDtoRequest loginDtoRequest = OBJECT_MAPPER.readValue(request.getInputStream(),
                LoginDtoRequest.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    loginDtoRequest.getEmail(),
                    loginDtoRequest.getPassword()
                );

            return this.getAuthenticationManager().authenticate(authenticationToken);

        } catch (IOException e) {
            throw new AuthenticationServiceException(Constants.Exceptions.INVALID_LOGIN);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {

        CustomUserDetails user = (CustomUserDetails) authResult.getPrincipal();

        String userId = user.getId().toString();
        String userEmail = user.getUsername();

        String accessToken = AuthUtils.generateAccessToken(userId, userEmail, secretKey);
        String refreshToken = AuthUtils.generateRefreshToken(userId, secretKey);

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + accessToken);

        Cookie refreshCookie = AuthUtils.generateCookie(Constants.Auth.REFRESH_TOKEN, refreshToken,
            secureCookie);
        response.addCookie(refreshCookie);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {

        String message = failed instanceof AccountNotVerifiedException
            ? Constants.Exceptions.USER_NOT_ENABLED
            : Constants.Errors.LOGIN_ERROR;

        Map<String, String> body = Map.of(
            Constants.Errors.ERROR, Constants.Errors.UNAUTHORIZED,
            Constants.General.MESSAGE, message
        );

        AuthUtils.setUnauthorizedResponse(response, body);
    }
}