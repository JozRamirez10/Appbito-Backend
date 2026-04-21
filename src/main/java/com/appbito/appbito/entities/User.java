package com.appbito.appbito.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Lastname is required")
    private String lastname;

    @NotNull(message = "Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotEmpty(message = "Email is required")
    @Column(unique = true)
    @Email(message = "It doesn't have an email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "The password must be 6 characters long")
    private String password;

    private String image;

    private boolean enabled = false;

    @Column(unique = true)
    private String verificationToken;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "user", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true
    )
    @JsonIgnore
    private List<Habit> habits;

    public User() {
        this.habits = new ArrayList<>();
    }

}
