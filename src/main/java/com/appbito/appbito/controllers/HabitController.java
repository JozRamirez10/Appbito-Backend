package com.appbito.appbito.controllers;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.HabitDtoRequest;
import com.appbito.appbito.dtos.HabitDtoResponse;
import com.appbito.appbito.exceptions.ForbiddenAccessException;
import com.appbito.appbito.exceptions.InvalidTokenException;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import com.appbito.appbito.services.HabitService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Habit Controller.
 */
@CrossOrigin(originPatterns = {"http://localhost:4200"})
@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
@Validated
public class HabitController {

    private static final String HABIT_NOT_FOUND = Constants.Errors.HABIT_NOT_FOUND;

    private final HabitService service;

    /**
     * method find all.
     *
     * @return a list of habitDtoResponse.
     */
    @GetMapping
    public ResponseEntity<List<HabitDtoResponse>> list() {
        return ResponseEntity.ok(this.service.findAll());
    }

    /**
     * method find by id.
     *
     * @param id habit.
     * @param authentication auth token.
     *
     * @return habitDtoResponse.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HabitDtoResponse> byId(@PathVariable Long id,
        Authentication authentication) {

        HabitDtoResponse habitDto = this.service.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(HABIT_NOT_FOUND));

        validateOwnership(habitDto.getUserId(), authentication);

        return ResponseEntity.ok(habitDto);
    }

    /**
     * method by user id.
     *
     * @param id user.
     * @param authentication auth token.
     *
     * @return a list of habitDtoResponse.
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<List<HabitDtoResponse>> byUserId(@PathVariable Long id,
        Authentication authentication) {

        validateOwnership(id, authentication);

        return ResponseEntity.ok(this.service.findByUserId(id));
    }

    /**
     * method by today.
     *
     * @param authentication auth token.
     *
     * @return a list of habitDtoResponse.
     */
    @GetMapping("/today")
    public ResponseEntity<List<HabitDtoResponse>> byToday(Authentication authentication) {

        Long tokenUserId = getUserIdFromToken(authentication);

        return ResponseEntity.ok(this.service.findByToday(tokenUserId));
    }

    /**
     * method create.
     *
     * @param habit habitDtoRequest.
     * @param authentication auth token.
     *
     * @return habitDtoResponse.
     */
    @PostMapping
    public ResponseEntity<HabitDtoResponse> create(@Valid @RequestBody HabitDtoRequest habit,
        Authentication authentication) {

        Long tokenUserId = getUserIdFromToken(authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(this.service.save(habit, tokenUserId));
    }

    /**
     * method update.
     *
     * @param habit habitDtoRequest.
     * @param id habit.
     * @param authentication auth token.
     *
     * @return habitDtoResponse.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HabitDtoResponse> update(@Valid @RequestBody HabitDtoRequest habit,
        @PathVariable Long id, Authentication authentication) {

        Long tokenUserId = getUserIdFromToken(authentication);

        HabitDtoResponse habitDto = this.service.update(habit, id, tokenUserId)
            .orElseThrow(() -> new ResourceNotFoundException(HABIT_NOT_FOUND));

        return ResponseEntity.ok(habitDto);
    }

    /**
     * method update batch.
     *
     * @param habits list of habitDtoRequest.
     * @param authentication auth token.
     *
     * @return list of habitDtoResponse.
     */
    @PutMapping("/batch")
    public ResponseEntity<List<HabitDtoResponse>> updateBatch(
        @Valid @NotEmpty @RequestBody List<HabitDtoRequest> habits, Authentication authentication) {

        Long tokenUserId = getUserIdFromToken(authentication);

        return ResponseEntity.ok(this.service.updateBatch(habits, tokenUserId));
    }

    /**
     * method delete.
     *
     * @param id habit.
     * @param authentication auth token.
     *
     * @return void responseEntity.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {

        Long tokenUserId = getUserIdFromToken(authentication);

        if (!this.service.deleteById(id, tokenUserId)) {
            throw new ResourceNotFoundException(HABIT_NOT_FOUND);
        }

        return ResponseEntity.noContent().build();
    }

    private void validateOwnership(Long userId, Authentication authentication) {

        Long tokenUserId = getUserIdFromToken(authentication);

        if (!userId.equals(tokenUserId)) {
            throw new ForbiddenAccessException();
        }
    }

    private Long getUserIdFromToken(Authentication authentication) {
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new InvalidTokenException();
        }
    }

}
