package com.appbito.appbito.services;

import com.appbito.appbito.dtos.requests.UserDtoRequest;
import com.appbito.appbito.dtos.requests.UserDtoUpdateRequest;
import com.appbito.appbito.dtos.responses.UserDtoResponse;
import java.util.List;
import java.util.Optional;

/**
 * User Service.
 */
public interface UserService {

    List<UserDtoResponse> findAll();

    Optional<UserDtoResponse> findById(Long id);

    UserDtoResponse save(UserDtoRequest user);

    Optional<UserDtoResponse> update(UserDtoUpdateRequest user, Long id);

    void updatePassword(Long id, String oldPassword, String newPassword);

    boolean deleteById(Long id);

    void deleteByIdAndPassword(Long id, String password);

    UserDtoResponse saveImage(Long id, String imageName);

    Optional<UserDtoResponse> enableUserByVerificationToken(String token);
}
