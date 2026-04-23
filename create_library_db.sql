-- Script de creación de la base de datos local para Biblio_Soft_Mvc
-- Ejecutar en un servidor MySQL local.

DROP DATABASE IF EXISTS library;
CREATE DATABASE library
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE library;

CREATE TABLE IF NOT EXISTS authors (
  id_author INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  nationality VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS users (
  id_user INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100),
  phone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS books (
  id_book INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(150) NOT NULL,
  isbn VARCHAR(20),
  year INT,
  id_author INT NOT NULL,
  FOREIGN KEY (id_author) REFERENCES authors(id_author)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS loans (
  id_loan INT AUTO_INCREMENT PRIMARY KEY,
  loan_date DATE NOT NULL,
  return_date DATE,
  id_user INT NOT NULL,
  id_book INT NOT NULL,
  returned BOOLEAN NOT NULL DEFAULT FALSE,
  actual_return_date DATE NULL,
  FOREIGN KEY (id_user) REFERENCES users(id_user)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  FOREIGN KEY (id_book) REFERENCES books(id_book)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

-- Datos de prueba: autores y libros
INSERT INTO authors (name, nationality) VALUES
  ('Gabriel García Márquez', 'Colombiano'),
  ('Isabel Allende', 'Chilena'),
  ('Mario Vargas Llosa', 'Peruano'),
  ('Jorge Luis Borges', 'Argentino'),
  ('Octavio Paz', 'Mexicano'),
  ('Pablo Neruda', 'Chileno'),
  ('Carlos Fuentes', 'Mexicano'),
  ('Laura Esquivel', 'Mexicana'),
  ('Miguel de Cervantes', 'Español'),
  ('Miguel Ángel Asturias', 'Guatemalteco'),
  ('Julio Cortázar', 'Argentino'),
  ('Rosario Castellanos', 'Mexicana'),
  ('Federico García Lorca', 'Español'),
  ('J. K. Rowling', 'Británica'),
  ('George Orwell', 'Británico'),
  ('Jane Austen', 'Británica'),
  ('Mark Twain', 'Estadounidense'),
  ('Herman Melville', 'Estadounidense'),
  ('Agatha Christie', 'Británica'),
  ('Ernest Hemingway', 'Estadounidense');

INSERT INTO books (title, isbn, year, id_author) VALUES
  ('Cien años de soledad', '978-0439023528', 1967, 1),
  ('La casa de los espíritus', '978-0061122415', 1982, 2),
  ('La ciudad y los perros', '978-0141184930', 1962, 3),
  ('El Aleph', '978-8432205248', 1949, 4),
  ('El laberinto de la soledad', '978-6071606981', 1950, 5),
  ('Veinte poemas de amor y una canción desesperada', '978-0143106267', 1924, 6),
  ('La muerte de Artemio Cruz', '978-0307387044', 1962, 7),
  ('Como agua para chocolate', '978-0553272137', 1989, 8),
  ('Don Quijote de la Mancha', '978-8491050212', 1605, 9),
  ('El Señor Presidente', '978-1400030400', 1946, 10),
  ('Rayuela', '978-8499890947', 1963, 11),
  ('Balún Canán', '978-6070707450', 1957, 12),
  ('Bodas de sangre', '978-0142437463', 1933, 13),
  ('Harry Potter y la piedra filosofal', '978-8498380771', 1997, 14),
  ('1984', '978-0451524935', 1949, 15),
  ('Orgullo y prejuicio', '978-0141439518', 1813, 16),
  ('Las aventuras de Tom Sawyer', '978-0143039563', 1876, 17),
  ('Moby Dick', '978-0142437241', 1851, 18),
  ('Diez negritos', '978-8497595715', 1939, 19),
  ('El viejo y el mar', '978-0684801223', 1952, 20);
