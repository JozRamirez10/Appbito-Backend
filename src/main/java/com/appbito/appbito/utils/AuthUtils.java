package com.appbito.appbito.utils;

import com.appbito.appbito.exceptions.ForbiddenAccessException;
import com.appbito.appbito.exceptions.InvalidTokenException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * Auth Utils.
 */
public final class AuthUtils {

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
     * valid user id with auth token.
     *
     * @param resourceUserId user id.
     * @param authentication auth token.
     */
    public static void validateOwnership(Long resourceUserId, Authentication authentication) {

        Long tokenUserId = getUserIdFromToken(authentication);

        if (!resourceUserId.equals(tokenUserId)) {
            throw new ForbiddenAccessException();
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

    public static TestingAuthenticationToken invalidToken() {
        return new TestingAuthenticationToken("Invalid token", null);
    }

}
