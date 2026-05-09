-- ==========================================
-- 1. Relation Habit - User
-- ==========================================
SET @fk_habit = (SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_NAME = 'habit' AND COLUMN_NAME = 'user_id' AND TABLE_SCHEMA = DATABASE()
    LIMIT 1);

SET @query_habit = CONCAT('ALTER TABLE habit DROP FOREIGN KEY ', @fk_habit);
PREPARE stmt_habit FROM @query_habit;
EXECUTE stmt_habit;
DEALLOCATE PREPARE stmt_habit;

-- Add On Delete Cascade
ALTER TABLE habit ADD CONSTRAINT fk_habit_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE;


-- ==========================================
-- 2. Relation Habit_days - Habit
-- ==========================================
SET @fk_days = (SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_NAME = 'habit_days' AND COLUMN_NAME = 'habit_id' AND TABLE_SCHEMA = DATABASE()
    LIMIT 1);

SET @query_days = CONCAT('ALTER TABLE habit_days DROP FOREIGN KEY ', @fk_days);
PREPARE stmt_days FROM @query_days;
EXECUTE stmt_days;
DEALLOCATE PREPARE stmt_days;

-- Add CASCADE
ALTER TABLE habit_days ADD CONSTRAINT fk_habit_days_habit FOREIGN KEY (habit_id) REFERENCES habit(id) ON DELETE CASCADE;


-- ==========================================
-- 3. Relation HabitProgress-Habit
-- ==========================================
SET @fk_progress = (SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_NAME = 'habit_progress' AND COLUMN_NAME = 'habit_id' AND TABLE_SCHEMA = DATABASE()
    LIMIT 1);

SET @query_progress = CONCAT('ALTER TABLE habit_progress DROP FOREIGN KEY ', @fk_progress);
PREPARE stmt_progress FROM @query_progress;
EXECUTE stmt_progress;
DEALLOCATE PREPARE stmt_progress;

-- Add Cascade
ALTER TABLE habit_progress ADD CONSTRAINT fk_habit_progress_habit FOREIGN KEY (habit_id) REFERENCES habit(id) ON DELETE CASCADE;