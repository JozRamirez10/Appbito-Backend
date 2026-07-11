package com.appbito.appbito.repositories;

import com.appbito.appbito.dtos.responses.HabitProgressDtoMonthlyResponse;
import com.appbito.appbito.entities.HabitProgress;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Habit Progress Repository.
 */
public interface HabitProgressRepository extends JpaRepository<HabitProgress, Long> {

    List<HabitProgress> findByHabitId(Long id);

    List<HabitProgress> findByHabitIdAndDateBetween(
        Long habitId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT new com.appbito.appbito.dtos.responses.HabitProgressDtoMonthlyResponse("
        + "YEAR(hp.date), MONTH(hp.date), SUM(hp.timesPerformed), hp.habit.id) "
        + "FROM HabitProgress hp "
        + "WHERE hp.date >= :startDate AND hp.date <= :endDate "
        + "AND hp.habit.id IN (:habitIds) "
        + "GROUP BY YEAR(hp.date), MONTH(hp.date), hp.habit.id")
    List<HabitProgressDtoMonthlyResponse> getTotalTimesPerformedByMonths(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("habitIds") List<Long> habitIds
    );

    @Query("SELECT hp FROM HabitProgress hp WHERE hp.habit.id = :habitId "
        + "AND hp.timesPerformed >= 1  ORDER BY hp.date ASC")
    List<HabitProgress> findProgressByHabit(@Param("habitId") Long habitId);
}
