package com.appbito.appbito.utils;

import static com.appbito.appbito.auth.TokenJwtConfig.CONTENT_TYPE;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.exceptions.ForbiddenAccessException;
import com.appbito.appbito.exceptions.InvalidTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * Auth Utils.
 */
public final class AuthUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private AuthUtils() { }

    /**
     * method get user id from auth token.
     *
     * @param authentication auth token.
     *
     * @return user id.
     */
    public static Long getUserIdFromToken(Authentication authentication) {

        try {

            return Long.parseLong(authentication.getName());

        } catch (NumberFormatException e) {

            throw new InvalidTokenException();
        }
    }

    /**
     * valid user id with request user id.
     *
     * @param resourceOwnerId user id.
     * @param requestUserId request user id.
     */
    public static void validateOwnership(Long resourceOwnerId, Long requestUserId) {

        if (!resourceOwnerId.equals(requestUserId)) {

            throw new ForbiddenAccessException();
        }
    }

    /**
     * method to validate token in unit tests.
     *
     * @return invalid token.
     */
    public static TestingAuthenticationToken invalidToken() {
        return new TestingAuthenticationToken(Constants.Exceptions.INVALID_TOKEN, null);
    }

    /**
     * method convert string to secret key.
     *
     * @param jwtSecret jwt secret.
     *
     * @return secret key.
     */
    public static SecretKey stringToSecretKey(String jwtSecret) {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * method generate a cookie.
     *
     * @param name cookie name.
     * @param value cookie value.
     * @param secureCookie secure cookie to https.
     *
     * @return cookie.
     */
    public static Cookie generateCookie(String name, String value, boolean secureCookie) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath(Constants.App.ROOT_PATH);
        cookie.setMaxAge(Constants.Auth.COOKIE_MAX_AGE);
        cookie.setAttribute(Constants.Auth.SAME_SITE, Constants.Auth.STRICT);
        cookie.setSecure(secureCookie);
        return cookie;
    }

    /**
     * method to set unauthorized response.
     *
     * @param response http servlet response.
     * @param body map with error details.
     *
     * @throws IOException if an input or output exception occurs.
     */
    public static void setUnauthorizedResponse(HttpServletResponse response,
        Map<String, String> body) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(Constants.App.ENCODING);

        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(body));
    }

    /**
     * method generate an access token.
     *
     * @param userId id owner.
     * @param secretKey secret key.
     *
     * @return access token string.
     */
    public static String generateAccessToken(String userId, SecretKey secretKey) {
        return generateToken(
            userId,
            Constants.Auth.ACCESS_TOKEN_TIME,
            secretKey,
            Collections.emptyMap()
        );
    }

    /**
     * method generate an access token using email too.
     *
     * @param userId id owner.
     * @param email email owner.
     * @param secretKey secret key.
     *
     * @return access token string.
     */
    public static String generateAccessToken(String userId, String email, SecretKey secretKey) {
        return generateToken(
            userId,
            Constants.Auth.ACCESS_TOKEN_TIME,
            secretKey,
            Map.of(Constants.General.EMAIL, email)
        );
    }

    /**
     * method generate a refresh token.
     *
     * @param userId id owner.
     * @param secretKey secret key.
     *
     * @return refresh token string.
     */
    public static String generateRefreshToken(String userId, SecretKey secretKey) {
        return generateToken(
            userId,
            Constants.Auth.REFRESH_TOKEN_TIME,
            secretKey,
            Collections.emptyMap()
        );
    }

    private static String generateToken(String userId, long expirationTime, SecretKey secretKey,
        Map<String, Object> extraClaims) {

        var builder = Jwts.builder()
        .subject(userId)
        .signWith(secretKey)
        .expiration(new Date(System.currentTimeMillis() + expirationTime));

        if (!extraClaims.isEmpty()) {
            extraClaims.forEach(builder::claim);
        }

        return builder.compact();
    }
}
