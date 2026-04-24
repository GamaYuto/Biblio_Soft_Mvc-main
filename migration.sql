-- Migracion de esquema para soporte de historial de prestamos
-- y estados operativos de libros.
-- Compatible con MySQL 8.0

DROP PROCEDURE IF EXISTS migrate_library_schema;

DELIMITER $$
CREATE PROCEDURE migrate_library_schema()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'loans'
          AND COLUMN_NAME = 'returned'
    ) THEN
        ALTER TABLE loans ADD COLUMN returned BOOLEAN NOT NULL DEFAULT FALSE;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'loans'
          AND COLUMN_NAME = 'actual_return_date'
    ) THEN
        ALTER TABLE loans ADD COLUMN actual_return_date DATE NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'books'
          AND COLUMN_NAME = 'status'
    ) THEN
        ALTER TABLE books ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.TABLES
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'categories'
    ) THEN
        CREATE TABLE categories (
            id_category INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(80) NOT NULL UNIQUE,
            description VARCHAR(255)
        );
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'books'
          AND COLUMN_NAME = 'id_category'
    ) THEN
        ALTER TABLE books ADD COLUMN id_category INT NULL;
    END IF;
END$$
DELIMITER ;

CALL migrate_library_schema();
DROP PROCEDURE IF EXISTS migrate_library_schema;

INSERT INTO categories (name, description)
SELECT 'General', 'Categoria base para libros existentes'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'General'
);

INSERT INTO categories (name, description)
SELECT 'Realismo magico', 'Narrativas con elementos fantasticos integrados en la realidad cotidiana'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Realismo magico'
);

INSERT INTO categories (name, description)
SELECT 'Novela historica', 'Relatos de ficcion ambientados en contextos historicos'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Novela historica'
);

INSERT INTO categories (name, description)
SELECT 'Poesia', 'Obras poeticas y recopilaciones liricas'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Poesia'
);

INSERT INTO categories (name, description)
SELECT 'Drama', 'Obras teatrales y textos dramaticos'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Drama'
);

INSERT INTO categories (name, description)
SELECT 'Fantasia', 'Narrativas de imaginacion y mundos fantasticos'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Fantasia'
);

INSERT INTO categories (name, description)
SELECT 'Clasicos', 'Titulos canonicos de la literatura universal'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Clasicos'
);

INSERT INTO categories (name, description)
SELECT 'Distopia', 'Obras con sociedades futuras o alternativas opresivas'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Distopia'
);

INSERT INTO categories (name, description)
SELECT 'Misterio', 'Novelas de intriga, crimen o suspense'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Misterio'
);

UPDATE books
SET id_category = (
    SELECT id_category FROM categories WHERE name = 'General'
)
WHERE id_book > 0 AND id_category IS NULL;

SET @books_category_fk_exists := (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'books'
      AND CONSTRAINT_NAME = 'fk_books_categories'
);

SET @add_books_category_fk := IF(
    @books_category_fk_exists = 0,
    'ALTER TABLE books ADD CONSTRAINT fk_books_categories FOREIGN KEY (id_category) REFERENCES categories(id_category) ON UPDATE CASCADE ON DELETE RESTRICT',
    'SELECT 1'
);

PREPARE stmt_books_category_fk FROM @add_books_category_fk;
EXECUTE stmt_books_category_fk;
DEALLOCATE PREPARE stmt_books_category_fk;

CREATE TABLE IF NOT EXISTS reservations (
    id_reservation INT AUTO_INCREMENT PRIMARY KEY,
    reservation_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    id_user INT NOT NULL,
    id_book INT NOT NULL,
    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    FOREIGN KEY (id_book) REFERENCES books(id_book)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

UPDATE loans
SET returned = FALSE
WHERE id_loan > 0 AND returned IS NULL;

UPDATE books
SET status = 'DISPONIBLE'
WHERE id_book > 0 AND (status IS NULL OR TRIM(status) = '');
