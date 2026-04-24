package com.mycompany.biblio_soft_mvc.dao;

import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import com.mycompany.biblio_soft_mvc.model.Author;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.BookStatus;
import com.mycompany.biblio_soft_mvc.model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Maneja las operaciones de base de datos para la entidad Book.
 * Ofrece funciones de busqueda, insercion, actualizacion y eliminacion.
 */
public class BookDAO {

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books(title, isbn, year, status, id_author, id_category) VALUES(?,?,?,?,?,?)";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getIsbn());
            ps.setInt(3, book.getYear());
            ps.setString(4, BookStatus.normalize(book.getStatus()));
            ps.setInt(5, book.getAuthor().getIdAuthor());
            ps.setInt(6, book.getCategory().getIdCategory());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error al agregar libro: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, isbn = ?, year = ?, status = ?, id_author = ?, id_category = ? WHERE id_book = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getIsbn());
            ps.setInt(3, book.getYear());
            ps.setString(4, BookStatus.normalize(book.getStatus()));
            ps.setInt(5, book.getAuthor().getIdAuthor());
            ps.setInt(6, book.getCategory().getIdCategory());
            ps.setInt(7, book.getIdBook());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar libro: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int idBook) {
        String sql = "DELETE FROM books WHERE id_book = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBook);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }

    public List<Book> getAllBooks() {
        return getBooksByCondition("");
    }

    public List<Book> getAvailableBooks() {
        return getBooksByCondition("WHERE b.status = ?", BookStatus.DISPONIBLE.name());
    }

    public List<Book> getReservableBooks() {
        return getBooksByCondition("WHERE b.status IN (?, ?)", BookStatus.PRESTADO.name(), BookStatus.RESERVADO.name());
    }

    public Book findById(int idBook) {
        List<Book> books = getBooksByCondition("WHERE b.id_book = ?", String.valueOf(idBook));
        return books.isEmpty() ? null : books.get(0);
    }

    public boolean updateBookStatus(int idBook, String status) {
        String sql = "UPDATE books SET status = ? WHERE id_book = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, BookStatus.normalize(status));
            ps.setInt(2, idBook);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del libro: " + e.getMessage());
            return false;
        }
    }

    public List<Book> searchBooks(String title) {
        return getBooksByCondition("WHERE b.title LIKE ?", "%" + title + "%");
    }

    public List<Book> searchBooksByAuthor(String authorName) {
        return getBooksByCondition("WHERE a.name LIKE ?", "%" + authorName + "%");
    }

    public List<Book> searchBooksByIsbn(String isbn) {
        return getBooksByCondition("WHERE b.isbn LIKE ?", "%" + isbn + "%");
    }

    public List<Book> searchBooksAdvanced(String title, String authorName, String isbn, Integer categoryId, Integer yearFrom, Integer yearTo) {
        List<Object> params = new ArrayList<>();
        StringBuilder condition = new StringBuilder("WHERE 1 = 1");

        if (title != null && !title.isBlank()) {
            condition.append(" AND b.title LIKE ?");
            params.add("%" + title.trim() + "%");
        }
        if (authorName != null && !authorName.isBlank()) {
            condition.append(" AND a.name LIKE ?");
            params.add("%" + authorName.trim() + "%");
        }
        if (isbn != null && !isbn.isBlank()) {
            condition.append(" AND b.isbn LIKE ?");
            params.add("%" + isbn.trim() + "%");
        }
        if (categoryId != null && categoryId > 0) {
            condition.append(" AND c.id_category = ?");
            params.add(categoryId);
        }
        if (yearFrom != null) {
            condition.append(" AND b.year >= ?");
            params.add(yearFrom);
        }
        if (yearTo != null) {
            condition.append(" AND b.year <= ?");
            params.add(yearTo);
        }

        return getBooksByCondition(condition.toString(), params.toArray());
    }

    private List<Book> getBooksByCondition(String condition, Object... params) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, a.name AS author_name, a.nationality, "
                + "c.id_category AS category_id, c.name AS category_name, c.description AS category_description "
                + "FROM books b "
                + "JOIN authors a ON b.id_author = a.id_author "
                + "LEFT JOIN categories c ON b.id_category = c.id_category "
                + condition + " ORDER BY b.title";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            throw new IllegalStateException("No se pudo establecer conexion con la base de datos.");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Author author = new Author(
                            rs.getInt("id_author"),
                            rs.getString("author_name"),
                            rs.getString("nationality")
                    );
                    Category category = null;
                    int categoryId = rs.getInt("category_id");
                    if (!rs.wasNull()) {
                        category = new Category(
                                categoryId,
                                rs.getString("category_name"),
                                rs.getString("category_description")
                        );
                    }
                    Book book = new Book(
                            rs.getInt("id_book"),
                            rs.getString("title"),
                            rs.getString("isbn"),
                            rs.getInt("year"),
                            rs.getString("status"),
                            author,
                            category
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
