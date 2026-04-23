package com.mycompany.biblio_soft_mvc.dao;

import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.Author;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Maneja las operaciones de base de datos para la entidad Book.
 * Ofrece funciones de búsqueda, inserción, actualización y eliminación.
 */
public class BookDAO {
    /**
     * Inserta un nuevo libro en la base de datos.
     * @param book Libro a agregar.
     * @return true si el libro se insertó correctamente.
     */
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books(title, isbn, year, id_author) VALUES(?,?,?,?)";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getIsbn());
            ps.setInt(3, book.getYear());
            ps.setInt(4, book.getAuthor().getIdAuthor());
            int rows = ps.executeUpdate();
            boolean success = rows > 0;
            if (success) {
                System.out.println("Libro registrado: " + book.getTitle());
            } else {
                System.out.println("No se insertó el libro: " + book.getTitle());
            }
            return success;
        } catch (SQLException e) {
            System.out.println("Error al agregar libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de un libro existente.
     * @param book Libro con los valores actualizados.
     * @return true si la actualización se aplicó.
     */
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, isbn = ?, year = ?, id_author = ? WHERE id_book = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getIsbn());
            ps.setInt(3, book.getYear());
            ps.setInt(4, book.getAuthor().getIdAuthor());
            ps.setInt(5, book.getIdBook());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un libro por su identificador.
     * @param idBook Identificador del libro a eliminar.
     * @return true si el libro se eliminó.
     */
    public boolean deleteBook(int idBook) {
        String sql = "DELETE FROM books WHERE id_book = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBook);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los libros.
     * @return Lista completa de libros.
     */
    public List<Book> getAllBooks() {
        return getBooksByCondition("");
    }

    /**
     * Busca libros por título parcial.
     * @param title Texto a buscar en el título.
     * @return Lista de libros que coinciden.
     */
    public List<Book> searchBooks(String title) {
        return getBooksByCondition("WHERE b.title LIKE ?", "%" + title + "%");
    }

    /**
     * Busca libros por nombre de autor.
     * @param authorName Texto a buscar en el nombre del autor.
     * @return Lista de libros que coinciden.
     */
    public List<Book> searchBooksByAuthor(String authorName) {
        return getBooksByCondition("WHERE a.name LIKE ?", "%" + authorName + "%");
    }

    /**
     * Busca libros por ISBN parcial.
     * @param isbn Texto a buscar en el ISBN.
     * @return Lista de libros que coinciden.
     */
    public List<Book> searchBooksByIsbn(String isbn) {
        return getBooksByCondition("WHERE b.isbn LIKE ?", "%" + isbn + "%");
    }

    /**
     * Método auxiliar que construye y ejecuta la consulta para obtener libros.
     * @param condition Cláusula WHERE opcional.
     * @param params Parámetros para la cláusula WHERE.
     * @return Lista de libros que cumplen la condición.
     */
    private List<Book> getBooksByCondition(String condition, String... params) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, a.name AS author_name, a.nationality FROM books b " +
                     "JOIN authors a ON b.id_author = a.id_author " + condition;
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            throw new IllegalStateException("No se pudo establecer conexión con la base de datos.");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Asigna parámetros dinámicos según la cláusula WHERE.
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Author author = new Author(rs.getInt("id_author"), rs.getString("author_name"), rs.getString("nationality"));
                    Book book = new Book(
                        rs.getInt("id_book"),
                        rs.getString("title"),
                        rs.getString("isbn"),
                        rs.getInt("year"),
                        author
                    );
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar libros: " + e.getMessage());
        }
        return books;
    }
}
