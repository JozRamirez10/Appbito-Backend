package com.appbito.appbito.controllers;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.requests.HabitDtoRequest;
import com.appbito.appbito.dtos.responses.HabitDtoResponse;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import com.appbito.appbito.services.HabitService;
import com.appbito.appbito.utils.AuthUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
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

        Long userId = AuthUtils.getUserIdFromToken(authentication);

        HabitDtoResponse habitDto = this.service.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException(HABIT_NOT_FOUND));

        return ResponseEntity.ok(habitDto);
    }

    /**
     * method by user id.
     *
     * @param authentication auth token.
     *
     * @return a list of habitDtoResponse.
     */
    @GetMapping("/me")
    public ResponseEntity<List<HabitDtoResponse>> byUserId(Authentication authentication) {

        Long userId = AuthUtils.getUserIdFromToken(authentication);

        return ResponseEntity.ok(this.service.findByUserId(userId));
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

        Long tokenUserId = AuthUtils.getUserIdFromToken(authentication);

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

        Long tokenUserId = AuthUtils.getUserIdFromToken(authentication);

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

        Long tokenUserId = AuthUtils.getUserIdFromToken(authentication);

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

        Long tokenUserId = AuthUtils.getUserIdFromToken(authentication);

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

        Long tokenUserId = AuthUtils.getUserIdFromToken(authentication);

        if (!this.service.deleteById(id, tokenUserId)) {
            throw new ResourceNotFoundException(HABIT_NOT_FOUND);
        }

        return ResponseEntity.noContent().build();
    }
}
