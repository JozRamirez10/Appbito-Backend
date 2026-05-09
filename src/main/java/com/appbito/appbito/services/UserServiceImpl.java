package com.appbito.appbito.services;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.requests.UserDtoRequest;
import com.appbito.appbito.dtos.requests.UserDtoUpdateRequest;
import com.appbito.appbito.dtos.responses.UserDtoResponse;
import com.appbito.appbito.entities.User;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import com.appbito.appbito.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Service Impl.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDtoResponse> findAll() {
        return this.repository.findAll().stream()
        .map(this::userToUserDtoResponse)
        .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDtoResponse> findById(Long id) {
        return this.repository.findById(id)
            .map(this::userToUserDtoResponse);
    }

    @Override
    @Transactional
    public UserDtoResponse save(UserDtoRequest userDto) {

        if (this.repository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException(Constants.User.EMAIL_ALREADY_EXISTS);
        }

        User user = userDtoRequestToUser(userDto);
        String token = UUID.randomUUID().toString();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationToken(token);
        user.setEnabled(true);

        User userSaved = this.repository.save(user);
        this.emailService.sendVerificationEmail(userSaved.getEmail(), token);

        return userToUserDtoResponse(userSaved);
    }

    @Override
    @Transactional
    public UserDtoResponse saveImage(Long id, String imageName) {

        User userBd = getUserOrThrow(id);

        userBd.setImage(imageName);

        return userToUserDtoResponse(userBd);
    }

    @Override
    @Transactional
    public Optional<UserDtoResponse> update(UserDtoUpdateRequest userDto, Long id) {

        return this.repository.findById(id)
            .map(userBd -> {
                userBd.setName(userDto.getName());
                userBd.setLastname(userDto.getLastname());
                userBd.setBirthDate(userDto.getBirthDate());
                return userToUserDtoResponse(userBd); // Save automatically
            });
    }

    @Override
    @Transactional
    public void updatePassword(Long id, String oldPassword,
        String newPassword) {

        User userBd = getUserOrThrow(id);

        if (!this.passwordEncoder.matches(oldPassword, userBd.getPassword())) {
            throw new IllegalArgumentException(Constants.User.INCORRECT_PASSWORD);
        }

        userBd.setPassword(this.passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {

        if (this.repository.existsById(id)) {
            this.repository.deleteUserByIdDirectly(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void deleteByIdAndPassword(Long id, String password) {

        User userBd = getUserOrThrow(id);

        if (!this.passwordEncoder.matches(password, userBd.getPassword())) {
            throw new IllegalArgumentException(Constants.User.PASSWORD_DOES_NOT_MATCH);
        }

        this.repository.deleteUserByIdDirectly(id);
    }

    @Override
    @Transactional
    public Optional<UserDtoResponse> enableUserByVerificationToken(String token) {

        return this.repository.findByVerificationToken(token)
            .map(userBd -> {
                userBd.setEnabled(true);
                userBd.setVerificationToken(null);
                return userToUserDtoResponse(userBd);
            });
    }

    private User getUserOrThrow(Long id) {
        return this.repository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(Constants.Errors.USER_NOT_FOUND)
            );
    }

    private User userDtoRequestToUser(UserDtoRequest userDto) {
        return User.builder()
            .name(userDto.getName())
            .lastname(userDto.getLastname())
            .birthDate(userDto.getBirthDate())
            .email(userDto.getEmail())
            .password(userDto.getPassword())
            .build();
    }

    private UserDtoResponse userToUserDtoResponse(User user) {
        return UserDtoResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .lastname(user.getLastname())
            .birthDate(user.getBirthDate())
            .email(user.getEmail())
            .image(user.getImage())
            .enabled(user.isEnabled())
            .build();
    }

}
