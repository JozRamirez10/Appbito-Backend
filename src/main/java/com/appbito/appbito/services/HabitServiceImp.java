package com.appbito.appbito.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appbito.appbito.dtos.HabitDTO;
import com.appbito.appbito.entities.Habit;
import com.appbito.appbito.entities.User;
import com.appbito.appbito.repositories.HabitRepository;
import com.appbito.appbito.repositories.UserRepository;


@Service
public class HabitServiceImp implements HabitService{

    @Autowired
    private HabitRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HabitDTO> findAll() {
        List<Habit> habits = this.repository.findAllWithDays();
        return listHabitToListHabitDTO(habits);
    }   

    @Override
    @Transactional(readOnly = true)
    public Optional<HabitDTO> findById(Long id) {
        return this.repository.findById(id)
            .map(this::habitToHabitDTO);
    }

    @Override
    @Transactional
    public HabitDTO save(HabitDTO habitDTO) {
        Habit habit = habitDtoToHabit(habitDTO);
        
        User user = getUserFromHabitDto(habitDTO);
        habit.setUser(user); 
        return habitToHabitDTO(this.repository.save(habit));
    }

    @Override
    @Transactional
    public Optional<HabitDTO> update(HabitDTO habitDTO, Long id) {
        Optional<Habit> habitOptional = this.repository.findById(id);
        if(habitOptional.isPresent()){

            Habit habit = habitDtoToHabit(habitDTO);
            
            Habit habitBD = habitOptional.orElseThrow();

            habitBD.setName(habit.getName());
            habitBD.setDescription(habit.getDescription());
            habitBD.setHour(habit.getHour());
            habitBD.setDays(habit.getDays());

            return Optional.of(this.repository.save(habitBD))
                .map(this::habitToHabitDTO);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        this.repository.deleteById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<HabitDTO> findByUserId(Long id) {
        List<Habit> habits = new ArrayList<>();
        this.repository.findByUserId(id).forEach(habits::add);
        return listHabitToListHabitDTO(habits);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitDTO> findByToday() {
        DayOfWeek today = DayOfWeek.valueOf(LocalDate.now().getDayOfWeek().name());
        List<Habit> habits = new ArrayList<>();
        this.repository.findByDaysContaining(today).forEach(habits::add);
        return listHabitToListHabitDTO(habits);
    }

    @Override
    @Transactional
    public List<HabitDTO> updateBatch(List<HabitDTO> habits) {
        List<Habit> entities = habits.stream()
            .map(habitDTO -> {
                Optional<Habit> habitOptional = this.repository.findById(habitDTO.getId());
                if(habitOptional.isPresent()){
                    Habit habitTransform = habitDtoToHabit(habitDTO);
                    Habit habit = habitOptional.orElseThrow();
                    return habit;
                }
                return null;
            }).collect(Collectors.toList());
        List<Habit> habitsSave = new ArrayList<>();
        this.repository.saveAll(entities).forEach(habitsSave::add);
        return listHabitToListHabitDTO(habitsSave);
    }
    
    private Habit habitDtoToHabit(HabitDTO habitDTO){
        Habit habit = new Habit();
        habit.setName(habitDTO.getName());
        habit.setDescription(habitDTO.getDescription());
        habit.setHour(habitDTO.getHour());
        
        habit.setDays(habitDTO.getDays());

        if(habitDTO.getUserId() != null){
            User user = getUserFromHabitDto(habitDTO);
            habit.setUser(user);
        }

        return habit;
    }

    private HabitDTO habitToHabitDTO(Habit habit){

        HabitDTO habitDTO = HabitDTO.builder()
            .id(habit.getId())
            .name(habit.getName())
            .description(habit.getDescription())
            .hour(habit.getHour())
            .userId(habit.getUser().getId())
            .days(habit.getDays())
            .build();

        return habitDTO;
    }

    private List<HabitDTO> listHabitToListHabitDTO(List<Habit> habits){
        return habits.stream()
            .map(this::habitToHabitDTO)
            .collect(Collectors.toList());
    }

    private User getUserFromHabitDto(HabitDTO habitDTO){
        return this.userRepository.findById(habitDTO.getUserId()).orElseThrow(
            () -> new RuntimeException("User not found with id: " + habitDTO.getUserId())
        );
    }
}
