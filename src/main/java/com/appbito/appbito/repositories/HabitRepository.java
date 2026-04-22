package com.appbito.appbito.repositories;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.appbito.appbito.entities.Habit;

public interface HabitRepository extends CrudRepository<Habit, Long>{

    @Query("SELECT h FROM Habit h LEFT JOIN FETCH h.days")
    List<Habit> findAllWithDays();

    @Query("SELECT h FROM Habit h LEFT JOIN FETCH h.days WHERE :day MEMBER OF h.days")
    List<Habit> findByDaysContaining(@Param("day") DayOfWeek day);

    @Query("SELECT h FROM Habit h WHERE h.user.id = :userId")
    List<Habit> findByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM HabitProgress h WHERE h.id = :habitProgressId")
    void deleteHabitProgressById(@Param("habitProgressId") Long habitProgressId);

}
