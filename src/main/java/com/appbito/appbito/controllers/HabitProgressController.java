package com.appbito.appbito.controllers;

import com.appbito.appbito.dtos.requests.HabitProgressDtoRequest;
import com.appbito.appbito.dtos.requests.HabitProgressDtoUpdateRequest;
import com.appbito.appbito.dtos.responses.HabitProgressDtoMonthlyResponse;
import com.appbito.appbito.dtos.responses.HabitProgressDtoResponse;
import com.appbito.appbito.services.HabitProgressService;
import com.appbito.appbito.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Habit Progress Controller.
 */
@RestController
@RequestMapping("/api/habitsProgress")
@RequiredArgsConstructor
@Validated
@Tag(name = "HabitProgress", description = "Endpoints to habit progress CRUD")
public class HabitProgressController {

    private final HabitProgressService service;

    /**
     * method by id habit.
     *
     * @param id habit.
     * @param authentication auth token.
     *
     * @return a list of habitProgressDtoResponse.
     */
    @Operation(summary = "Get habit progresses by habit id")
    @GetMapping("/habit/{id}")
    public ResponseEntity<List<HabitProgressDtoResponse>> byIdHabit(@PathVariable Long id,
        Authentication authentication) {

        Long userId = AuthUtils.getUserIdFromToken(authentication);
        return ResponseEntity.ok(this.service.findByHabitId(id, userId));
    }

    /**
     * method by id.
     *
     * @param id habitProgress
     * @param authentication auth token
     *
     * @return habitProgressDtoResponse
     */
    @Operation(summary = "Get a habit progress by id")
    @GetMapping("/{id}")
    public ResponseEntity<HabitProgressDtoResponse> byId(@PathVariable Long id,
        Authentication authentication) {

        Long userId = AuthUtils.getUserIdFromToken(authentication);
        return ResponseEntity.ok(this.service.findById(id, userId));
    }

    /**
     * method by id habit and date range.
     *
     * @param id habit.
     * @param startDate localDate.
     * @param endDate localDate.
     * @param authentication auth token.
     *
     * @return a list of habitProgressDtoResponse.
     */
    @Operation(summary = "Get habit progresses within a period time")
    @GetMapping("/habit/{id}/range")
    public ResponseEntity<List<HabitProgressDtoResponse>> byIdHabitAndDateRange(
        @PathVariable Long id,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
        Authentication authentication) {

        Long userId = AuthUtils.getUserIdFromToken(authentication);
        return ResponseEntity.ok(
            this.service.findByHabitIdAndDateBetween(id, startDate, endDate, userId)
        );
    }

    /**
     * method get total times performed by months.
     *
     * @param years a list of integer.
     * @param habitIds a list of longs.
     * @param authentication auth token.
     *
     * @return a list of habitProgressDtoMonthlyResponse.
     */
    @Operation(summary = "Get total times performed in a specific month of a habit ids list")
    @GetMapping("/monthly")
    public ResponseEntity<List<HabitProgressDtoMonthlyResponse>> getTotalTimesPerformedByMonths(
        @RequestParam List<Integer> years, @RequestParam List<Long> habitIds,
        Authentication authentication) {

        Long userId = AuthUtils.getUserIdFromToken(authentication);
        return ResponseEntity.ok(
            this.service.getTotalTimesPerformedByMonths(years, habitIds, userId)
        );
    }

    /**
     * method create.
     *
     * @param habitProgressDto habitProgressDtoRequest.
     * @param authentication auth token.
     *
     * @return habitProgressDtoResponse.
     */
    @Operation(summary = "Create a habit progress")
    @PostMapping
    public ResponseEntity<HabitProgressDtoResponse> create(
        @Valid @RequestBody HabitProgressDtoRequest habitProgressDto,
        Authentication authentication) {

        Long userId = AuthUtils.getUserIdFromToken(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(this.service.save(habitProgressDto, userId));
    }

    /**
     * method update.
     *
     * @param habitProgressDto habitProgressDtoUpdateRequest.
     * @param id habitProgress.
     * @param authentication auth token.
     *
     * @return habitProgressDtoResponse.
     */
    @Operation(summary = "Update a habit progress")
    @PutMapping("/{id}")
    public ResponseEntity<HabitProgressDtoResponse> update(
        @Valid @RequestBody HabitProgressDtoUpdateRequest habitProgressDto, @PathVariable Long id,
        Authentication authentication) {

        Long userId = AuthUtils.getUserIdFromToken(authentication);
        return ResponseEntity.ok(this.service.update(id, habitProgressDto, userId));
    }

    /**
     * method delete.
     *
     * @param id habitProgress.
     * @param authentication auth token.
     *
     * @return void.
     */
    @Operation(summary = "Delete a habit progress by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {

        Long userId = AuthUtils.getUserIdFromToken(authentication);
        this.service.deleteById(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * method get habit streak.
     *
     * @param id habit.
     * @param authentication auth token.
     *
     * @return integer.
     */
    @Operation(summary = "Get a streak of a habit by id")
    @GetMapping("/habit/{id}/streak")
    public ResponseEntity<Integer> getHabitStreak(@PathVariable Long id,
        Authentication authentication) {

        Long userId = AuthUtils.getUserIdFromToken(authentication);
        return ResponseEntity.ok(this.service.getStreak(id, userId));
    }
}
