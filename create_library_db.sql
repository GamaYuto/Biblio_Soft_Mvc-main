-- Script de creacion de la base de datos local para Biblio_Soft_Mvc
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

CREATE TABLE IF NOT EXISTS categories (
  id_category INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80) NOT NULL UNIQUE,
  description VARCHAR(255)
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
  status VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
  id_author INT NOT NULL,
  id_category INT NOT NULL,
  FOREIGN KEY (id_author) REFERENCES authors(id_author)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  FOREIGN KEY (id_category) REFERENCES categories(id_category)
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

CREATE TABLE IF NOT EXISTS admin_users (
  id_admin INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(150),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO categories (name, description) VALUES
  ('Realismo magico', 'Narrativas con elementos fantasticos integrados en la realidad cotidiana'),
  ('Novela historica', 'Relatos de ficcion ambientados en contextos historicos'),
  ('Poesia', 'Obras poeticas y recopilaciones liricas'),
  ('Drama', 'Obras teatrales y textos dramaticos'),
  ('Fantasia', 'Narrativas de imaginacion y mundos fantasticos'),
  ('Clasicos', 'Titulos canonicos de la literatura universal'),
  ('Distopia', 'Obras con sociedades futuras o alternativas opresivas'),
  ('Misterio', 'Novelas de intriga, crimen o suspense');

-- Datos de prueba: autores y libros
INSERT INTO authors (name, nationality) VALUES
  ('Gabriel Garcia Marquez', 'Colombiano'),
  ('Isabel Allende', 'Chilena'),
  ('Mario Vargas Llosa', 'Peruano'),
  ('Jorge Luis Borges', 'Argentino'),
  ('Octavio Paz', 'Mexicano'),
  ('Pablo Neruda', 'Chileno'),
  ('Carlos Fuentes', 'Mexicano'),
  ('Laura Esquivel', 'Mexicana'),
  ('Miguel de Cervantes', 'Espanol'),
  ('Miguel Angel Asturias', 'Guatemalteco'),
  ('Julio Cortazar', 'Argentino'),
  ('Rosario Castellanos', 'Mexicana'),
  ('Federico Garcia Lorca', 'Espanol'),
  ('J. K. Rowling', 'Britanica'),
  ('George Orwell', 'Britanico'),
  ('Jane Austen', 'Britanica'),
  ('Mark Twain', 'Estadounidense'),
  ('Herman Melville', 'Estadounidense'),
  ('Agatha Christie', 'Britanica'),
  ('Ernest Hemingway', 'Estadounidense');

INSERT INTO books (title, isbn, year, status, id_author, id_category) VALUES
  ('Cien anos de soledad', '978-0439023528', 1967, 'DISPONIBLE', 1, 1),
  ('La casa de los espiritus', '978-0061122415', 1982, 'DISPONIBLE', 2, 1),
  ('La ciudad y los perros', '978-0141184930', 1962, 'DISPONIBLE', 3, 6),
  ('El Aleph', '978-8432205248', 1949, 'DISPONIBLE', 4, 6),
  ('El laberinto de la soledad', '978-6071606981', 1950, 'DISPONIBLE', 5, 6),
  ('Veinte poemas de amor y una cancion desesperada', '978-0143106267', 1924, 'DISPONIBLE', 6, 3),
  ('La muerte de Artemio Cruz', '978-0307387044', 1962, 'DISPONIBLE', 7, 2),
  ('Como agua para chocolate', '978-0553272137', 1989, 'DISPONIBLE', 8, 1),
  ('Don Quijote de la Mancha', '978-8491050212', 1605, 'DISPONIBLE', 9, 6),
  ('El Senor Presidente', '978-1400030400', 1946, 'DISPONIBLE', 10, 2),
  ('Rayuela', '978-8499890947', 1963, 'DISPONIBLE', 11, 6),
  ('Balun Canan', '978-6070707450', 1957, 'DISPONIBLE', 12, 6),
  ('Bodas de sangre', '978-0142437463', 1933, 'DISPONIBLE', 13, 4),
  ('Harry Potter y la piedra filosofal', '978-8498380771', 1997, 'DISPONIBLE', 14, 5),
  ('1984', '978-0451524935', 1949, 'DISPONIBLE', 15, 7),
  ('Orgullo y prejuicio', '978-0141439518', 1813, 'DISPONIBLE', 16, 6),
  ('Las aventuras de Tom Sawyer', '978-0143039563', 1876, 'DISPONIBLE', 17, 6),
  ('Moby Dick', '978-0142437241', 1851, 'DISPONIBLE', 18, 6),
  ('Diez negritos', '978-8497595715', 1939, 'DISPONIBLE', 19, 8),
  ('El viejo y el mar', '978-0684801223', 1952, 'DISPONIBLE', 20, 6);

-- Usuario administrador inicial
-- username: admin
-- password: Admin12345!
INSERT INTO admin_users (username, password_hash, full_name) VALUES
  ('admin', '$2a$12$Nbvkhxua7VpVY7owxdBeY.D9X43Gypci4gbajQo11YefVMKccN3Je', 'Administrador General');
