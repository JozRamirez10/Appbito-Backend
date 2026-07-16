package com.appbito.appbito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.appbito.appbito.auth.models.CustomUserDetails;
import com.appbito.appbito.dtos.UserDtoCredentials;
import com.appbito.appbito.exceptions.AccountNotVerifiedException;
import com.appbito.appbito.repositories.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * User Jpa Details Service Test.
 */
@ExtendWith(MockitoExtension.class)
class UserJpaDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserJpaDetailsService userJpaDetailsService;

    private static final Long USER_ID = 1L;

    private static final String EMAIL = "test@email.com";

    private static final String PASSWORD = "password";

    private static final boolean ENABLED = true;

    private static final boolean NOT_ENABLED = false;

    @Test
    void loadUserByUsernameOk() {

        UserDtoCredentials userDtoCredentials = new UserDtoCredentials(
            USER_ID, EMAIL, PASSWORD, ENABLED
        );

        UserDetails userDetailsResponse = new CustomUserDetails(USER_ID, EMAIL, PASSWORD);

        when(userRepository.findCredentialsByEmail(EMAIL))
            .thenReturn(Optional.of(userDtoCredentials));

        UserDetails result = userJpaDetailsService.loadUserByUsername(EMAIL);

        assertNotNull(result);
        assertEquals(userDetailsResponse.getUsername(), result.getUsername());
        assertEquals(userDetailsResponse.getPassword(), result.getPassword());

        verify(userRepository).findCredentialsByEmail(EMAIL);
    }

    @Test
    void loadUserByUsernameNotFound() {

        when(userRepository.findCredentialsByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userJpaDetailsService.loadUserByUsername(EMAIL);
        });

        verify(userRepository).findCredentialsByEmail(EMAIL);
    }

    @Test
    void loadUserByUsernameNotEnabled() {

        UserDtoCredentials userDtoCredentialsNotEnabled = new UserDtoCredentials(
            USER_ID, EMAIL, PASSWORD, NOT_ENABLED
        );

        when(userRepository.findCredentialsByEmail(EMAIL))
            .thenReturn(Optional.of(userDtoCredentialsNotEnabled));

        assertThrows(AccountNotVerifiedException.class, () -> {
            userJpaDetailsService.loadUserByUsername(EMAIL);
        });

        verify(userRepository).findCredentialsByEmail(EMAIL);
    }

}
