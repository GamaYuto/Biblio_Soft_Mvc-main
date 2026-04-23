-- Migración de esquema para soporte de historial de préstamos
-- Compatible con MySQL 8.0

DROP PROCEDURE IF EXISTS migrate_loans;

DELIMITER $$
CREATE PROCEDURE migrate_loans()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME   = 'loans'
          AND COLUMN_NAME  = 'returned'
    ) THEN
        ALTER TABLE loans ADD COLUMN returned BOOLEAN NOT NULL DEFAULT FALSE;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME   = 'loans'
          AND COLUMN_NAME  = 'actual_return_date'
    ) THEN
        ALTER TABLE loans ADD COLUMN actual_return_date DATE NULL;
    END IF;
END$$
DELIMITER ;

CALL migrate_loans();
DROP PROCEDURE IF EXISTS migrate_loans;

-- Asegurar que filas existentes queden como no devueltas
UPDATE loans SET returned = FALSE WHERE returned IS NULL;
