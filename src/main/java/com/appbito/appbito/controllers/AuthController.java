package com.appbito.appbito.controllers;

import static com.appbito.appbito.auth.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.appbito.appbito.auth.TokenJwtConfig.PREFIX_TOKEN;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.responses.UserDtoResponse;
import com.appbito.appbito.exceptions.InvalidTokenException;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import com.appbito.appbito.services.UserService;
import com.appbito.appbito.utils.AuthUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auth Controller.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auths", description = "Endpoints to auth CRUDS")
public class AuthController {

    private static final String REFRESH_TOKEN = Constants.Auth.REFRESH_TOKEN;

    private final UserService userService;

    private SecretKey secretKey;

    private JwtParser jwtParser;

    @Value("${app.cookies.secure}")
    private boolean secureCookie;

    /**
     * method build a SecretKey and JwtParser.
     *
     * @param secretString secret key.
     */
    @Value("${jwt.secret}")
    public void setSecretKey(String secretString) {
        this.secretKey = AuthUtils.stringToSecretKey(secretString);

        this.jwtParser = Jwts.parser()
            .verifyWith(this.secretKey)
            .build();
    }

    /**
     * method get a new access token.
     *
     * @param refreshToken refresh token in cookie.
     * @param response httpServletResponse.
     *
     * @return new AcessToken or Unauthorized Exception.
     */
    @Operation(summary = "Refresh token to requests")
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(
        @CookieValue(value = REFRESH_TOKEN, required = false) String refreshToken,
        HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException(Constants.Exceptions.REFRESH_TOKEN_MISSING);
        }

        try {
            Claims claims = jwtParser
                .parseSignedClaims(refreshToken)
                .getPayload();

            String userId = claims.getSubject();

            String newAccessToken = AuthUtils.generateAccessToken(userId, secretKey);

            return ResponseEntity.ok()
                .header(HEADER_AUTHORIZATION, PREFIX_TOKEN + newAccessToken)
                .build();

        } catch (JwtException | IllegalArgumentException e) {
            Cookie cookieRevoked = cookieExpiration(REFRESH_TOKEN);
            response.addCookie(cookieRevoked);
            throw new InvalidTokenException(Constants.Exceptions.INVALID_REFRESH_TOKEN);
        }
    }

    /**
     * method to logout session.
     *
     * @param response httpServletResponse.
     *
     * @return success logout response.
     */
    @Operation(summary = "Logout")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        Cookie refreshCookie = cookieExpiration(REFRESH_TOKEN);
        response.addCookie(refreshCookie);
        return ResponseEntity.ok(Map.of(Constants.General.MESSAGE, Constants.Auth.LOGOUT_SUCCESS));
    }

    /**
     * method enable user using token.
     *
     * @param token send to email.
     *
     * @return userDtoResponse.
     */
    @Operation(summary = "Validate 'verificate_token' of user")
    @GetMapping("/verify")
    public ResponseEntity<UserDtoResponse> verifyAccount(@RequestParam("token") String token) {

        UserDtoResponse userDto = userService.enableUserByVerificationToken(token)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.Exceptions.TOKEN_NOT_FOUND));

        return ResponseEntity.ok(userDto);
    }

    private Cookie cookieExpiration(String nameCookie) {
        Cookie cookie = AuthUtils.generateCookie(nameCookie, Constants.General.EMPTY_STRING,
            secureCookie);
        cookie.setMaxAge(Constants.General.ZERO);
        return cookie;
    }
}
