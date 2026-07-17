package com.appbito.appbito.controllers;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.requests.UserDtoDeleteRequest;
import com.appbito.appbito.dtos.requests.UserDtoRequest;
import com.appbito.appbito.dtos.requests.UserDtoUpdatePasswordRequest;
import com.appbito.appbito.dtos.requests.UserDtoUpdateRequest;
import com.appbito.appbito.dtos.responses.UserDtoResponse;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import com.appbito.appbito.services.ImageService;
import com.appbito.appbito.services.UserService;
import com.appbito.appbito.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * User Controller.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints to user CRUD")
public class UserController {

    private static final String USER_NOT_FOUND = Constants.Errors.USER_NOT_FOUND;

    private final UserService service;

    private final ImageService imageService;

    /**
     * method find all.
     *
     * @return a list of UserDtoResponse.
     */
    @Operation(summary = "Get all users (Only for dev and tests)")
    @GetMapping
    public ResponseEntity<List<UserDtoResponse>> list() {
        return ResponseEntity.ok(this.service.findAll());
    }

    /**
     * method find me.
     *
     * @param authentication auth token.
     *
     * @return userDtoResponse.
     */
    @Operation(summary = "Get an user by id (Only user id authenticated)")
    @GetMapping("/me")
    public ResponseEntity<UserDtoResponse> byId(Authentication authentication) {

        Long id = AuthUtils.getUserIdFromToken(authentication);

        UserDtoResponse userDto = this.service.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        return ResponseEntity.ok(userDto);
    }

    /**
     * method get image of me.
     *
     * @param authentication auth token.
     *
     * @return user's image like resource.
     */
    @Operation(summary = "Get an user image (Only user id authenticated)")
    @GetMapping("/me/image")
    public ResponseEntity<Resource> getImage(Authentication authentication) {

        Long id = AuthUtils.getUserIdFromToken(authentication);

        UserDtoResponse userDto = this.service.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if (userDto.getImage() == null) {
            throw new ResourceNotFoundException(Constants.Files.IMAGE_NOT_FOUND);
        }

        Resource resource = this.imageService.loadAsResource(userDto.getImage());

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    /**
     * method create.
     *
     * @param user userDtoRequest.
     *
     * @return userDtoResponse.
     */
    @Operation(summary = "Create an user")
    @PostMapping
    public ResponseEntity<UserDtoResponse> create(@Valid @RequestBody UserDtoRequest user) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(this.service.save(user));
    }

    /**
     * method upload image.
     *
     * @param file image.
     * @param authentication auth token.
     *
     * @return userDtoResponse.
     */
    @Operation(summary = "Update user image (Only user authenticated)")
    @PostMapping("/me/image")
    public ResponseEntity<UserDtoResponse> uploadProfileImage(
        @RequestParam("image") MultipartFile file,
        Authentication authentication) {

        Long userId = AuthUtils.getUserIdFromToken(authentication);

        String filename = this.imageService.storeProfileImage(file, userId);

        UserDtoResponse updatedUser = this.service.saveImage(userId, filename);

        return ResponseEntity.ok(updatedUser);
    }

    /**
     * method update me.
     *
     * @param user userDtoUpdateRequest.
     * @param authentication auth token.
     *
     * @return userDtoResponse.
     */
    @Operation(summary = "Update an user (Only user authenticated")
    @PutMapping("/me")
    public ResponseEntity<UserDtoResponse> update(@Valid @RequestBody UserDtoUpdateRequest user,
        Authentication authentication) {

        Long id = AuthUtils.getUserIdFromToken(authentication);

        UserDtoResponse userDto = this.service.update(user, id)
            .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        return ResponseEntity.ok(userDto);
    }

    /**
     * method update password of me.
     *
     * @param request userDtoUpdatePasswordRequest.
     * @param authentication auth token.
     *
     * @return void.
     */
    @Operation(summary = "Update user password (Only user authenticated)")
    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
        @Valid @RequestBody UserDtoUpdatePasswordRequest request,
        Authentication authentication) {

        Long id = AuthUtils.getUserIdFromToken(authentication);

        this.service.updatePassword(id, request.getOldPassword(), request.getNewPassword());

        return ResponseEntity.ok().build();
    }

    /**
     * method delete me.
     *
     * @param authentication auth token.
     *
     * @return void.
     */
    @Operation(summary = "Delete an user (Only user authenticated)")
    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(Authentication authentication) {

        Long id = AuthUtils.getUserIdFromToken(authentication);

        if (!this.service.deleteById(id)) {
            throw new ResourceNotFoundException(USER_NOT_FOUND);
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * method delete me by password.
     *
     * @param userDto userDtoDeleteRequest.
     * @param authentication auth token.
     *
     * @return void.
     */
    @Operation(summary = "Delete an user validating your password (Only user authenticated)")
    @DeleteMapping("/me/password")
    public ResponseEntity<Void> confirmDelete(@Valid @RequestBody UserDtoDeleteRequest userDto,
        Authentication authentication) {

        Long id = AuthUtils.getUserIdFromToken(authentication);

        this.service.deleteByIdAndPassword(id, userDto.getPassword());

        return ResponseEntity.noContent().build();
    }
}
