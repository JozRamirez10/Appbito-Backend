package com.appbito.appbito.controllers;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import com.appbito.appbito.repositories.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test Auth Controller.
 */
@RestController
@RequestMapping("/test/auth")
@Profile({"dev", "test"})
@RequiredArgsConstructor
public class TestAuthController {

    private final UserRepository userRepository;

    /**
     * method get verification token by email.
     * only for tests.
     *
     * @param email email owner.
     *
     * @return string token.
     */
    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getVerificationToken(
        @RequestParam("email") String email) {

        String token = userRepository.findVerificationTokenByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.Errors.USER_NOT_FOUND));

        return ResponseEntity.ok(Map.of(Constants.Auth.VERIFICATION_TOKEN, token));
    }
}
