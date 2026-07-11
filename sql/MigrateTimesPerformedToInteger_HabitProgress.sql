-- =============================================================================
-- Times Performed from Long to Integer type
-- =============================================================================

ALTER TABLE habit_progress MODIFY COLUMN times_performed INT NOT NULL DEFAULT 0;