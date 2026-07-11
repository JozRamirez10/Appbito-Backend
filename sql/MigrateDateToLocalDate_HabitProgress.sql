-- =============================================================================
-- Date to LocalDate in HabitProgress Column
-- =============================================================================

-- 1. Change to new type of data in date column
ALTER TABLE habit_progress MODIFY COLUMN date DATE NOT NULL;

-- 2. Recommended if there are many data
UPDATE habit_progress SET date = DATE(date);