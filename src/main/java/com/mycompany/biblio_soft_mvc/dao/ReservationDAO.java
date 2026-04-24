package com.mycompany.biblio_soft_mvc.dao;

import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import com.mycompany.biblio_soft_mvc.model.Author;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.Category;
import com.mycompany.biblio_soft_mvc.model.Reservation;
import com.mycompany.biblio_soft_mvc.model.ReservationStatus;
import com.mycompany.biblio_soft_mvc.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar reservas de libros.
 */
public class ReservationDAO {

    public boolean addReservation(Reservation reservation) {
        return addReservationWithError(reservation) == null;
    }

    public String addReservationWithError(Reservation reservation) {
        String sql = "INSERT INTO reservations(reservation_date, status, id_user, id_book) VALUES(?,?,?,?)";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            return "No se pudo establecer conexion con la base de datos.";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(reservation.getReservationDate().getTime()));
            ps.setString(2, ReservationStatus.normalize(reservation.getStatus()));
            ps.setInt(3, reservation.getUser().getIdUser());
            ps.setInt(4, reservation.getBook().getIdBook());
            int rows = ps.executeUpdate();
            return rows > 0 ? null : "La insercion no afecto ninguna fila.";
        } catch (SQLException e) {
            System.out.println("Error al registrar reserva: " + e.getMessage());
            return "Error SQL: " + e.getMessage();
        }
    }

    public List<Reservation> getActiveReservations() {
        return getReservationsByCondition("WHERE r.status = ?", ReservationStatus.ACTIVA.name());
    }

    public List<Reservation> getReservationHistory() {
        return getReservationsByCondition("");
    }

    public List<Reservation> getReservationsByUser(int userId) {
        return getReservationsByCondition("WHERE r.id_user = ?", userId);
    }

    public List<Reservation> getReservationsByBook(int bookId) {
        return getReservationsByCondition("WHERE r.id_book = ?", bookId);
    }

    public Reservation findById(int idReservation) {
        List<Reservation> reservations = getReservationsByCondition("WHERE r.id_reservation = ?", idReservation);
        return reservations.isEmpty() ? null : reservations.get(0);
    }

    public boolean hasActiveReservation(int userId, int bookId) {
        String sql = "SELECT 1 FROM reservations WHERE id_user = ? AND id_book = ? AND status = ? LIMIT 1";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.setString(3, ReservationStatus.ACTIVA.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al validar reserva activa: " + e.getMessage());
            return false;
        }
    }

    public boolean hasActiveReservationsForBook(int bookId) {
        String sql = "SELECT 1 FROM reservations WHERE id_book = ? AND status = ? LIMIT 1";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setString(2, ReservationStatus.ACTIVA.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar reservas activas por libro: " + e.getMessage());
            return false;
        }
    }

    public Reservation findFirstActiveReservationForUserAndBook(int userId, int bookId) {
        String condition = "WHERE r.id_user = ? AND r.id_book = ? AND r.status = ?";
        List<Reservation> reservations = getReservationsByCondition(condition, userId, bookId, ReservationStatus.ACTIVA.name());
        return reservations.isEmpty() ? null : reservations.get(0);
    }

    public boolean updateReservationStatus(int idReservation, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE id_reservation = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ReservationStatus.normalize(status));
            ps.setInt(2, idReservation);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de reserva: " + e.getMessage());
            return false;
        }
    }

    private List<Reservation> getReservationsByCondition(String condition, Object... params) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, u.name AS user_name, u.email, u.phone, "
                + "b.title, b.isbn, b.year, b.status AS book_status, "
                + "a.id_author, a.name AS author_name, a.nationality, "
                + "c.id_category, c.name AS category_name, c.description AS category_description "
                + "FROM reservations r "
                + "JOIN users u ON r.id_user = u.id_user "
                + "JOIN books b ON r.id_book = b.id_book "
                + "JOIN authors a ON b.id_author = a.id_author "
                + "LEFT JOIN categories c ON b.id_category = c.id_category "
                + condition
                + " ORDER BY r.reservation_date ASC, r.id_reservation ASC";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return reservations;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id_user"),
                            rs.getString("user_name"),
                            rs.getString("email"),
                            rs.getString("phone")
                    );
                    Author author = new Author(
                            rs.getInt("id_author"),
                            rs.getString("author_name"),
                            rs.getString("nationality")
                    );
                    Category category = null;
                    int categoryId = rs.getInt("id_category");
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
                            rs.getString("book_status"),
                            author,
                            category
                    );
                    Reservation reservation = new Reservation(
                            rs.getInt("id_reservation"),
                            rs.getDate("reservation_date"),
                            rs.getString("status"),
                            user,
                            book
                    );
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar reservas: " + e.getMessage());
        }
        return reservations;
    }
}
