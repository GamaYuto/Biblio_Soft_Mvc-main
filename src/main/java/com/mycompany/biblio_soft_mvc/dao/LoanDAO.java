package com.mycompany.biblio_soft_mvc.dao;

import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import com.mycompany.biblio_soft_mvc.model.Author;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.Category;
import com.mycompany.biblio_soft_mvc.model.Loan;
import com.mycompany.biblio_soft_mvc.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Maneja las operaciones de base de datos para la entidad Loan.
 */
public class LoanDAO {

    public boolean addLoan(Loan loan) {
        return addLoanWithError(loan) == null;
    }

    public String addLoanWithError(Loan loan) {
        String sql = "INSERT INTO loans(loan_date, return_date, id_user, id_book, returned) VALUES(?,?,?,?,?)";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            return "No se pudo establecer conexion con la base de datos. Verifique las credenciales en ConnectionDB.";
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
            ps.setBoolean(5, loan.isReturned());
            int rows = ps.executeUpdate();
            return rows > 0 ? null : "La insercion no afecto ninguna fila.";
        } catch (SQLException e) {
            System.out.println("Error al registrar prestamo: " + e.getMessage());
            return "Error SQL: " + e.getMessage();
        }
    }

    public List<Loan> getAllLoans() {
        return getLoansByCondition("WHERE l.returned = FALSE");
    }

    public List<Loan> getLoanHistory() {
        return getLoansByCondition("");
    }

    public List<Loan> getLoansByUser(int userId) {
        return getLoansByCondition("WHERE l.id_user = ?", userId);
    }

    public List<Loan> getLoansByBook(int bookId) {
        return getLoansByCondition("WHERE l.id_book = ?", bookId);
    }

    public Loan findById(int idLoan) {
        List<Loan> loans = getLoansByCondition("WHERE l.id_loan = ?", idLoan);
        return loans.isEmpty() ? null : loans.get(0);
    }

    public boolean hasActiveLoanForBook(int bookId) {
        String sql = "SELECT 1 FROM loans WHERE id_book = ? AND returned = FALSE LIMIT 1";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar prestamos activos por libro: " + e.getMessage());
            return false;
        }
    }

    public boolean returnBook(int idLoan) {
        String sql = "UPDATE loans SET returned = TRUE, actual_return_date = ? WHERE id_loan = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(new Date().getTime()));
            ps.setInt(2, idLoan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al devolver libro: " + e.getMessage());
            return false;
        }
    }

    public boolean updateLoan(Loan loan) {
        String sql = "UPDATE loans SET loan_date = ?, return_date = ?, id_user = ?, id_book = ? WHERE id_loan = ? AND returned = FALSE";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
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
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar prestamo: " + e.getMessage());
            return false;
        }
    }

    private List<Loan> getLoansByCondition(String condition, Object... params) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, u.name AS user_name, u.email, u.phone, "
                + "b.title, b.isbn, b.year, b.status, "
                + "a.id_author, a.name AS author_name, a.nationality, "
                + "c.id_category, c.name AS category_name, c.description AS category_description "
                + "FROM loans l "
                + "JOIN users u ON l.id_user = u.id_user "
                + "JOIN books b ON l.id_book = b.id_book "
                + "JOIN authors a ON b.id_author = a.id_author "
                + "LEFT JOIN categories c ON b.id_category = c.id_category "
                + condition;
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return loans;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Integer) {
                    ps.setInt(i + 1, (Integer) params[i]);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User(rs.getInt("id_user"), rs.getString("user_name"), rs.getString("email"), rs.getString("phone"));
                    Author author = new Author(rs.getInt("id_author"), rs.getString("author_name"), rs.getString("nationality"));
                    Category category = null;
                    int categoryId = rs.getInt("id_category");
                    if (!rs.wasNull()) {
                        category = new Category(categoryId, rs.getString("category_name"), rs.getString("category_description"));
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
            System.out.println("Error al listar prestamos: " + e.getMessage());
        }
        return loans;
    }
}
