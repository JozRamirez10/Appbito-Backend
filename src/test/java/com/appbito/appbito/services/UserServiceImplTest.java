package com.appbito.appbito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.requests.UserDtoRequest;
import com.appbito.appbito.dtos.requests.UserDtoUpdateRequest;
import com.appbito.appbito.dtos.responses.UserDtoResponse;
import com.appbito.appbito.entities.User;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import com.appbito.appbito.repositories.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * User Service Test
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User userEntity;

    private static final Long USER_ID = 1L;

    private static final String USER_NAME = "Subject";

    private static final String USER_LASTNAME = "Test";

    private static final String USER_EMAIL = "test@email.com";

    private static final LocalDate USER_BIRTHDATE =
        LocalDate.of(2000, 12, 31);

    private static final String USER_PASSWORD = "password";

    private static final String USER_IMAGE = Constants.Files.PROFILE_PREFIX + USER_ID + "."
            + Constants.Files.JPG_EXTENSION;

    private static final String USER_NAME_UPDATE = "Subject Update";

    private static final String USER_LASTNAME_UPDATE = "Test Update";

    private static final LocalDate USER_BIRTHDATE_UPDATE =
        LocalDate.of(2001, 01, 01);

    private static final String USER_PASSWORD_UPDATE = "new password";

    private static final String PASSWORD = "123456789";

    private static final String USER_TOKEN = "token";

    @BeforeEach
    void setUp() {

        userEntity = User.builder()
            .id(USER_ID)
            .name(USER_NAME)
            .lastname(USER_LASTNAME)
            .email(USER_EMAIL)
            .birthDate(USER_BIRTHDATE)
            .image(USER_IMAGE)
            .password(USER_PASSWORD)
            .verificationToken(USER_TOKEN)
            .build();
    }

    @Test
    void findAllOk() {

        when(userRepository.findAll()).thenReturn(List.of(userEntity));

        List<UserDtoResponse> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(USER_ID, result.get(0).getId());
        assertEquals(USER_NAME, result.get(0).getName());
        assertEquals(USER_LASTNAME, result.get(0).getLastname());
        assertEquals(USER_EMAIL, result.get(0).getEmail());

        verify(userRepository).findAll();
    }

    @Test
    void findAllEmptyList() {

        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDtoResponse> result = userService.findAll();

        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        verify(userRepository).findAll();
    }

    @Test
    void findByIdOk() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));

        Optional<UserDtoResponse> result = userService.findById(USER_ID);

        assertTrue(result.isPresent());
        assertEquals(USER_ID, result.get().getId());
        assertEquals(USER_NAME, result.get().getName());
        assertEquals(USER_EMAIL, result.get().getEmail());

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void findByIdNotFound() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Optional<UserDtoResponse> result = userService.findById(USER_ID);

        assertTrue(result.isEmpty());

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void saveOk() {

        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(USER_PASSWORD)).thenReturn(PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        UserDtoResponse result = userService.save(userRequest());

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(USER_NAME, result.getName());
        assertEquals(USER_EMAIL, result.getEmail());

        verify(userRepository).existsByEmail(USER_EMAIL);
        verify(passwordEncoder).encode(USER_PASSWORD);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendVerificationEmail(eq(USER_EMAIL), anyString());
    }

    @Test
    void saveEmailExists() {

        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.save(userRequest());
        });

        verify(userRepository).existsByEmail(USER_EMAIL);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void saveImageOk() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));

        UserDtoResponse result = userService.saveImage(USER_ID, USER_IMAGE);

        assertNotNull(result);
        assertEquals(USER_IMAGE, result.getImage());

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void saveImageNotFound() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.saveImage(USER_ID, USER_EMAIL);
        });

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void updateOk() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));

        Optional<UserDtoResponse> result = userService.update(userUpdate(), USER_ID);

        assertTrue(result.isPresent());
        assertEquals(USER_ID, result.get().getId());
        assertEquals(USER_NAME_UPDATE, result.get().getName());
        assertEquals(USER_LASTNAME_UPDATE, result.get().getLastname());
        assertEquals(USER_BIRTHDATE_UPDATE, result.get().getBirthDate());

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void updateNotFound() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Optional<UserDtoResponse> result = userService.update(userUpdate(), USER_ID);

        assertTrue(result.isEmpty());

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void updatePasswordOk() {

        String currentPassword = USER_PASSWORD;
        String encryptedPassword = PASSWORD;

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(USER_PASSWORD, currentPassword)).thenReturn(true);
        when(passwordEncoder.encode(USER_PASSWORD_UPDATE)).thenReturn(encryptedPassword);

        userService.updatePassword(USER_ID, USER_PASSWORD, USER_PASSWORD_UPDATE);

        assertEquals(encryptedPassword, userEntity.getPassword());

        verify(userRepository).findById(USER_ID);
        verify(passwordEncoder).matches(USER_PASSWORD, currentPassword);
        verify(passwordEncoder).encode(USER_PASSWORD_UPDATE);
    }

    @Test
    void updatePasswordNotMatches() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(USER_PASSWORD, userEntity.getPassword()))
            .thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updatePassword(USER_ID, USER_PASSWORD, PASSWORD);
        });

        verify(userRepository).findById(USER_ID);
        verify(passwordEncoder).matches(USER_PASSWORD, userEntity.getPassword());
    }

    @Test
    void updatePasswordNotFound() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updatePassword(USER_ID, USER_PASSWORD, PASSWORD);
        });

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void deleteByIdOk() {

        when(userRepository.existsById(USER_ID)).thenReturn(true);

        boolean result = userService.deleteById(USER_ID);

        assertTrue(result);

        verify(userRepository).existsById(USER_ID);
        verify(userRepository).deleteUserByIdDirectly(USER_ID);
    }

    @Test
    void deleteByIdNotFound() {

        when(userRepository.existsById(USER_ID)).thenReturn(false);

        boolean result = userService.deleteById(USER_ID);

        assertFalse(result);

        verify(userRepository).existsById(USER_ID);
        verify(userRepository, never()).deleteUserByIdDirectly(USER_ID);
    }

    @Test
    void deleteByIdAndPasswordOk() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(PASSWORD, USER_PASSWORD)).thenReturn(true);

        userService.deleteByIdAndPassword(USER_ID, PASSWORD);

        verify(userRepository).findById(USER_ID);
        verify(passwordEncoder).matches(PASSWORD, USER_PASSWORD);
        verify(userRepository).deleteUserByIdDirectly(USER_ID);
    }

    @Test
    void deleteByIdAndPasswordNotFound() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteByIdAndPassword(USER_ID, PASSWORD);
        });

        verify(userRepository).findById(USER_ID);
        verify(passwordEncoder, never()).matches(PASSWORD, USER_PASSWORD);
        verify(userRepository, never()).deleteUserByIdDirectly(USER_ID);
    }

    @Test
    void deleteByIdAndPasswordNotMatches() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(PASSWORD, USER_PASSWORD)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteByIdAndPassword(USER_ID, PASSWORD);
        });

        verify(userRepository).findById(USER_ID);
        verify(passwordEncoder).matches(PASSWORD, USER_PASSWORD);
        verify(userRepository, never()).deleteUserByIdDirectly(USER_ID);
    }

    @Test
    void enableUserOk() {

        when(userRepository.findByVerificationToken(USER_TOKEN))
            .thenReturn(Optional.of(userEntity));

        Optional<UserDtoResponse> result = userService.enableUserByVerificationToken(USER_TOKEN);

        assertTrue(result.isPresent());
        assertTrue(result.get().isEnabled());
        assertNull(userEntity.getVerificationToken());

        verify(userRepository).findByVerificationToken(USER_TOKEN);
    }

    @Test
    void enableUserNotFound() {

        when(userRepository.findByVerificationToken(USER_TOKEN)).thenReturn(Optional.empty());

        Optional<UserDtoResponse> result = userService.enableUserByVerificationToken(USER_TOKEN);

        assertTrue(result.isEmpty());

        verify(userRepository).findByVerificationToken(USER_TOKEN);
    }

    private UserDtoRequest userRequest() {
        return UserDtoRequest.builder()
            .name(USER_NAME)
            .lastname(USER_LASTNAME)
            .birthDate(USER_BIRTHDATE)
            .email(USER_EMAIL)
            .password(USER_PASSWORD)
            .build();
    }

    private UserDtoUpdateRequest userUpdate() {
        return UserDtoUpdateRequest.builder()
            .name(USER_NAME_UPDATE)
            .lastname(USER_LASTNAME_UPDATE)
            .birthDate(USER_BIRTHDATE_UPDATE)
            .build();
    }

}
