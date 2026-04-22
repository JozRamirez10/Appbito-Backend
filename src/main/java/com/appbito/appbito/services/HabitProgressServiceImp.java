package com.appbito.appbito.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appbito.appbito.dtos.HabitProgressDTO;
import com.appbito.appbito.dtos.HabitProgressMonthlyDTO;
import com.appbito.appbito.entities.Habit;
import com.appbito.appbito.entities.HabitProgress;
import com.appbito.appbito.repositories.HabitProgressRepository;
import com.appbito.appbito.repositories.HabitRepository;

@Service
public class HabitProgressServiceImp implements HabitProgressService{

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Autowired
    private HabitProgressRepository repository;

    @Autowired
    private HabitRepository habitRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HabitProgressDTO> findByHabitId(Long idHabit) {
        List<HabitProgress> habitProgressDTOs = new ArrayList<>(); 
        this.repository.findByHabitId(idHabit).forEach(habitProgressDTOs::add);
        return listHabitProgresstoListHabitProgressDTO(habitProgressDTOs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitProgressDTO> findByHabitIdAndDateBetween(Long habitId, Date startDate, Date endDate) {
        List<HabitProgress> habitProgresses = this.repository.findByHabitIdAndDateBetween(habitId, startDate, endDate);
        return listHabitProgresstoListHabitProgressDTO(habitProgresses);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HabitProgressDTO> findById(Long id) {
        return this.repository.findById(id)
            .map(this::habitProgressToHabitProgressDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitProgressMonthlyDTO> getTotalTimesPerformedByMonths(List<Integer> years, List<Long> habitIds){
        List<Object[]> results = this.repository.getTotalTimesPerformedByMonths(years, habitIds);
        return results.stream()
            .map(r -> new HabitProgressMonthlyDTO((Integer) r[0], (Integer) r[1], (Long) r[2], (Long) r[3]))
            .collect(Collectors.toList());
    }

    @Override
    public HabitProgressDTO save(HabitProgressDTO habitProgressDTO) {
        HabitProgress habitProgress = habitProgressDTOtoHabitProgress(habitProgressDTO);

        Habit habit = getHabitFromHabitProgressDTO(habitProgressDTO);
        habitProgress.setHabit(habit);

        return habitProgressToHabitProgressDTO(this.repository.save(habitProgress));
    }
    
    @Override
    @Transactional
    public Optional<HabitProgressDTO> update(Long id, HabitProgressDTO habitProgressDTO) {
        Optional<HabitProgress> habitProgressOptional = this.repository.findById(id);
        if(habitProgressOptional.isPresent()){
            HabitProgress habitProgress = habitProgressDTOtoHabitProgress(habitProgressDTO);
            HabitProgress habitProgressBD = habitProgressOptional.orElseThrow();

            habitProgressBD.setTimesPerformed(habitProgress.getTimesPerformed());
            habitProgressBD.setNote(habitProgress.getNote());

            return Optional.of(this.repository.save(habitProgressBD))
                .map(this::habitProgressToHabitProgressDTO);
            
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        this.repository.deleteById(id);
    }

    @Override
    @Transactional
    public int getStreak(Long habitId){
        Habit habit = habitRepository.findById(habitId).orElseThrow(() -> new RuntimeException("Habit not found"));

        Set<DayOfWeek> allowedDays = habit.getDays();

        List<HabitProgress> progressList = repository.findProgressByHabit(habitId);
        if(progressList.isEmpty()) return 0;

        List<LocalDate> progressDates = new ArrayList<>();
        for(HabitProgress progress : progressList){
            
            LocalDate localDate = progress.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

            if(allowedDays.contains(localDate.getDayOfWeek())){
                progressDates.add(localDate);
            }
        }
        
        if(progressDates.isEmpty()) return 0;
        
        return calculateStreak(progressDates, allowedDays);
    }

    private int calculateStreak(List<LocalDate> progress, Set<DayOfWeek> allowedDays){
        if(progress != null || progress.isEmpty() || allowedDays != null || allowedDays.isEmpty()) return 0;

        List<LocalDate> sortedProgress = progress.stream()
            .distinct()
            .sorted()
            .toList();

        int maxStreak = 0;
        int currentStreak = 1;
        
        for(int i = 1; i < sortedProgress.size(); i++){
            LocalDate lastSuccess = sortedProgress.get(i - 1);
            LocalDate currentSuccess = sortedProgress.get(i);

            LocalDate nextExpectedDate = findNextExpectedDate(lastSuccess, allowedDays);

            if (currentSuccess.equals(nextExpectedDate)) {
                currentStreak++;
            }else{
                maxStreak = Math.max(maxStreak, currentStreak);
                currentStreak = 1;
            }
        }

        return Math.max(maxStreak, currentStreak);
    }

    private LocalDate findNextExpectedDate(LocalDate from, Set<DayOfWeek> allowedDays){

        LocalDate candidate = from.plusDays(1);

        for(int i = 0; i < 7; i++){
            DayOfWeek candidateDay = DayOfWeek.valueOf(candidate.getDayOfWeek().name());
            if (allowedDays.contains(candidateDay)) {
                return candidate;
            }
            candidate = candidate.plusDays(1);
        }

        return candidate;
    }

    private HabitProgress habitProgressDTOtoHabitProgress(HabitProgressDTO habitProgressDTO){
        HabitProgress habitProgress = new HabitProgress();
        
        try{
            Date progress = DATE_FORMAT.parse(habitProgressDTO.getDate());

            Calendar cal = Calendar.getInstance();
            cal.setTime(progress);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            habitProgress.setDate(cal.getTime());
            habitProgress.setTimesPerformed(habitProgressDTO.getTimesPerformed());
            if(habitProgressDTO.getNote() != null){
                habitProgress.setNote(habitProgressDTO.getNote());
            }
            return habitProgress;

        } catch(ParseException e) {
            throw new RuntimeException("Invalid date format: " + e);
        }
    }

    private HabitProgressDTO habitProgressToHabitProgressDTO(HabitProgress habitProgress){

        HabitProgressDTO habitProgressDTO = HabitProgressDTO.builder()
            .id(habitProgress.getId())
            .date(habitProgress.getDate().toString())
            .timesPerformed(habitProgress.getTimesPerformed())
            .note(habitProgress.getNote())
            .habitId(habitProgress.getHabit().getId())
            .build();

        return habitProgressDTO;
    }

    private List<HabitProgressDTO> listHabitProgresstoListHabitProgressDTO(List<HabitProgress> habitProgresses){
        return habitProgresses.stream()
            .map(this::habitProgressToHabitProgressDTO)
            .collect(Collectors.toList());
    }

    private List<HabitProgress> listHabitProgressesDTOtoListHabitProgresses(List<HabitProgressDTO> habitProgressDTO){
        return habitProgressDTO.stream()
            .map(this::habitProgressDTOtoHabitProgress)
            .collect(Collectors.toList());
    }

    private Habit getHabitFromHabitProgressDTO(HabitProgressDTO habitProgressDTO){
        return this.habitRepository.findById(habitProgressDTO.getHabitId()).orElseThrow(
            () -> new RuntimeException("Habit not found width id: " + habitProgressDTO.getHabitId())
        );
    }
}
