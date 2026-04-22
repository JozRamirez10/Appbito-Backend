-- =============================================================================
-- Days migration: From table to Enum (@ElementCollection)
-- =============================================================================

-- 1. New format in habit_days
ALTER TABLE habit_days ADD COLUMN day_name VARCHAR(50);

-- 2. Data migration translate IDs to Enum names (Uppercase)
UPDATE habit_days hd
JOIN day d ON hd.day_id = d.id
SET hd.day_name = UPPER(d.name);

-- 3. Validation
-- SELECT habit_id, day_name FROM habit_days;

-- 4. Delete old foreign key
SET @constraint_name = (SELECT CONSTRAINT_NAME 
    FROM information_schema.KEY_COLUMN_USAGE 
    WHERE TABLE_NAME = 'habit_days' AND COLUMN_NAME = 'day_id' AND TABLE_SCHEMA = 'appbito'
    LIMIT 1);

SET @query = CONCAT('ALTER TABLE habit_days DROP FOREIGN KEY ', @constraint_name);
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5. Clean: Delete columns and tables 
ALTER TABLE habit_days DROP COLUMN day_id;
DROP TABLE day;

-- 6. Add constraints to day_name column
ALTER TABLE habit_days MODIFY COLUMN day_name VARCHAR(20) NOT NULL;