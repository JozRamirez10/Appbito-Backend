package com.appbito.appbito.repositories;

import com.appbito.appbito.entities.Habit;
import java.time.DayOfWeek;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Habit Repository.
 */
public interface HabitRepository extends JpaRepository<Habit, Long> {

    @Query("SELECT DISTINCT h FROM Habit h LEFT JOIN FETCH h.days")
    List<Habit> findAllWithDays();

    @Query("SELECT DISTINCT h FROM Habit h LEFT JOIN FETCH h.days WHERE h.id IN :ids")
    List<Habit> findAllWithDaysByIds(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT h FROM Habit h LEFT JOIN FETCH h.days WHERE :day"
        + " MEMBER OF h.days AND h.user.id = :userId")
    List<Habit> findByDaysContainingAndUserId(@Param("day") DayOfWeek day,
        @Param("userId") Long userId);

    @Query("SELECT DISTINCT h FROM Habit h LEFT JOIN FETCH h.days WHERE h.user.id = :userId")
    List<Habit> findByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM HabitProgress h WHERE h.id = :habitProgressId")
    void deleteHabitProgressById(@Param("habitProgressId") Long habitProgressId);

    @Modifying
    @Query("DELETE FROM HabitProgress hp WHERE hp.habit.id = :habitId")
    void deleteAllProgressByHabitId(@Param("habitId") Long habitId);
}
