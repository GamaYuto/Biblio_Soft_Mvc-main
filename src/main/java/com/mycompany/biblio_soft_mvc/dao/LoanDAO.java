package com.mycompany.biblio_soft_mvc.dao;

import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import com.mycompany.biblio_soft_mvc.model.Loan;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.User;
import com.mycompany.biblio_soft_mvc.model.Author;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Maneja las operaciones de base de datos para la entidad Loan.
 * Soporta registro de préstamos, devoluciones y consultas históricas.
 */
public class LoanDAO {
    /**
     * Registra un préstamo usando el método de error opcional.
     * @param loan Objeto Loan a insertar.
     * @return true si el préstamo se agregó correctamente.
     */
    public boolean addLoan(Loan loan) {
        return addLoanWithError(loan) == null;
    }

    /**
     * Intenta insertar el préstamo y devuelve null si tuvo éxito,
     * o el mensaje de error SQL si falló.
     * @param loan Préstamo a registrar.
     * @return null si el registro fue exitoso, de lo contrario el mensaje de error.
     */
    public String addLoanWithError(Loan loan) {
        String sql = "INSERT INTO loans(loan_date, return_date, id_user, id_book, returned) VALUES(?,?,?,?,?)";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            return "No se pudo establecer conexión con la base de datos. Verifique las credenciales en ConnectionDB.";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(loan.getLoanDate().getTime()));
            if (loan.getReturnDate() != null) {
                ps.setDate(2, new java.sql.Date(loan.getReturnDate().getTime()));
            } else {
                // Si no hay fecha estimada de devolución, se inserta NULL.
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setInt(3, loan.getUser().getIdUser());
            ps.setInt(4, loan.getBook().getIdBook());
            ps.setBoolean(5, loan.isReturned());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Préstamo registrado correctamente.");
                return null;
            }
            return "La inserción no afectó ninguna fila.";
        } catch (SQLException e) {
            System.out.println("Error al registrar préstamo: " + e.getMessage());
            return "Error SQL: " + e.getMessage();
        }
    }

    /**
     * Recupera los préstamos activos (no devueltos).
     * @return Lista de préstamos activos.
     */
    public List<Loan> getAllLoans() {
        return getLoansByCondition("WHERE returned = FALSE");
    }

    /**
     * Recupera el historial completo de los préstamos.
     * @return Lista de todos los préstamos.
     */
    public List<Loan> getLoanHistory() {
        return getLoansByCondition("");
    }

    /**
     * Obtiene los préstamos de un usuario específico.
     * @param userId Identificador del usuario.
     * @return Lista de préstamos de ese usuario.
     */
    public List<Loan> getLoansByUser(int userId) {
        return getLoansByCondition("WHERE l.id_user = ?", userId);
    }

    /**
     * Obtiene los préstamos de un libro específico.
     * @param bookId Identificador del libro.
     * @return Lista de préstamos asociados al libro.
     */
    public List<Loan> getLoansByBook(int bookId) {
        return getLoansByCondition("WHERE l.id_book = ?", bookId);
    }

    /**
     * Marca un préstamo como devuelto y registra la fecha real de devolución.
     * @param idLoan Identificador del préstamo.
     * @return true si el registro de devolución se realizó correctamente.
     */
    public boolean returnBook(int idLoan) {
        String sql = "UPDATE loans SET returned = TRUE, actual_return_date = ? WHERE id_loan = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(new Date().getTime()));
            ps.setInt(2, idLoan);
            int rows = ps.executeUpdate();
            boolean success = rows > 0;
            if (success) {
                System.out.println("Libro devuelto (préstamo marcado como retornado) con ID: " + idLoan);
            }
            return success;
        } catch (SQLException e) {
            System.out.println("Error al devolver libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de un préstamo que aún no ha sido devuelto.
     * @param loan Objeto Loan con los datos actualizados.
     * @return true si la actualización se aplicó correctamente.
     */
    public boolean updateLoan(Loan loan) {
        String sql = "UPDATE loans SET loan_date = ?, return_date = ?, id_user = ?, id_book = ? WHERE id_loan = ? AND returned = FALSE";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(loan.getLoanDate().getTime()));
            if (loan.getReturnDate() != null) {
                ps.setDate(2, new java.sql.Date(loan.getReturnDate().getTime()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setInt(3, loan.getUser().getIdUser());
            ps.setInt(4, loan.getBook().getIdBook());
            ps.setInt(5, loan.getIdLoan());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar préstamo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método auxiliar que devuelve préstamos según una condición dinámica.
     * Construye la consulta con JOINs para incluir datos de usuario, libro y autor.
     * @param condition Cláusula WHERE adicional.
     * @param params Parámetros para la cláusula WHERE.
     * @return Lista de préstamos que cumplen la condición.
     */
    private List<Loan> getLoansByCondition(String condition, Object... params) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, u.name AS user_name, u.email, u.phone, b.title, b.isbn, b.year, a.id_author, a.name AS author_name, a.nationality " +
                     "FROM loans l " +
                     "JOIN users u ON l.id_user = u.id_user " +
                     "JOIN books b ON l.id_book = b.id_book " +
                     "JOIN authors a ON b.id_author = a.id_author " +
                     condition;
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return loans;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Asignar los parámetros numéricos de la consulta dinámica.
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Integer) {
                    ps.setInt(i + 1, (Integer) params[i]);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User(rs.getInt("id_user"), rs.getString("user_name"), rs.getString("email"), rs.getString("phone"));
                    Author author = new Author(rs.getInt("id_author"), rs.getString("author_name"), rs.getString("nationality"));
                    Book book = new Book(rs.getInt("id_book"), rs.getString("title"), rs.getString("isbn"), rs.getInt("year"), author);
                    Loan loan = new Loan(
                        rs.getInt("id_loan"),
                        rs.getDate("loan_date"),
                        rs.getDate("return_date"),
                        rs.getBoolean("returned"),
                        rs.getDate("actual_return_date"),
                        user,
                        book
                    );
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar préstamos: " + e.getMessage());
        }
        return loans;
    }
}
