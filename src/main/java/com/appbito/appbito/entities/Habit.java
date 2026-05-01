package com.appbito.appbito.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Habit Entity.
 */
@Entity
@Table(name = "habit")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private LocalTime hour;

    @ElementCollection(
        targetClass = DayOfWeek.class,
        fetch = FetchType.EAGER
    )
    @CollectionTable(
        name = "habit_days",
        joinColumns = @JoinColumn(name = "habit_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "day_name", nullable = false)
    private Set<DayOfWeek> days;

    @Builder.Default
    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "habit",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<HabitProgress> habitProgress = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
