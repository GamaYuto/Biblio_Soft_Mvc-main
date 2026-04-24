package com.mycompany.biblio_soft_mvc.dao;

import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import com.mycompany.biblio_soft_mvc.model.AuthorMetric;
import com.mycompany.biblio_soft_mvc.model.DashboardMetrics;
import com.mycompany.biblio_soft_mvc.model.UserActivityMetric;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Centraliza las consultas agregadas del dashboard.
 */
public class DashboardDAO {
    private static final int DEFAULT_RECENT_WINDOW_DAYS = 30;
    private static final int DEFAULT_TOP_AUTHORS_LIMIT = 5;
    private static final int DEFAULT_ACTIVE_USERS_LIMIT = 5;

    public DashboardMetrics getDashboardMetrics() {
        int borrowedBooks = countBorrowedBooks();
        int pendingReturns = countPendingReturns();
        int overdueReturns = countOverdueReturns();
        int activeUsers = countRecentActiveUsers(DEFAULT_RECENT_WINDOW_DAYS);
        List<AuthorMetric> topAuthors = getTopAuthors(DEFAULT_TOP_AUTHORS_LIMIT);
        List<UserActivityMetric> activeUsersDetail = getRecentActiveUsers(DEFAULT_RECENT_WINDOW_DAYS, DEFAULT_ACTIVE_USERS_LIMIT);

        return new DashboardMetrics(
                borrowedBooks,
                pendingReturns,
                overdueReturns,
                activeUsers,
                DEFAULT_RECENT_WINDOW_DAYS,
                topAuthors,
                activeUsersDetail
        );
    }

    public int countBorrowedBooks() {
        return countByQuery("SELECT COUNT(*) FROM loans WHERE returned = FALSE");
    }

    public int countPendingReturns() {
        return countByQuery("SELECT COUNT(*) FROM loans WHERE returned = FALSE");
    }

    public int countOverdueReturns() {
        return countByQuery("SELECT COUNT(*) FROM loans WHERE returned = FALSE AND return_date IS NOT NULL AND return_date < CURDATE()");
    }

    public List<AuthorMetric> getTopAuthors(int limit) {
        List<AuthorMetric> metrics = new ArrayList<>();
        String sql = "SELECT a.id_author, a.name, a.nationality, COUNT(l.id_loan) AS total_loans "
                + "FROM authors a "
                + "JOIN books b ON b.id_author = a.id_author "
                + "JOIN loans l ON l.id_book = b.id_book "
                + "GROUP BY a.id_author, a.name, a.nationality "
                + "ORDER BY total_loans DESC, a.name ASC "
                + "LIMIT ?";

        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            throw new IllegalStateException("No se pudo establecer conexion con la base de datos.");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    metrics.add(new AuthorMetric(
                            rs.getInt("id_author"),
                            rs.getString("name"),
                            rs.getString("nationality"),
                            rs.getInt("total_loans")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar autores mas consultados: " + e.getMessage(), e);
        }
        return metrics;
    }

    public List<UserActivityMetric> getRecentActiveUsers(int recentWindowDays, int limit) {
        List<UserActivityMetric> metrics = new ArrayList<>();
        String sql = "SELECT u.id_user, u.name, u.email, "
                + "COALESCE(loans.recent_loans, 0) AS recent_loans, "
                + "COALESCE(reservations.recent_reservations, 0) AS recent_reservations, "
                + "GREATEST("
                + "COALESCE(loans.last_loan_date, DATE('1900-01-01')), "
                + "COALESCE(reservations.last_reservation_date, DATE('1900-01-01'))"
                + ") AS last_activity_date "
                + "FROM users u "
                + "LEFT JOIN ("
                + "    SELECT id_user, COUNT(*) AS recent_loans, MAX(loan_date) AS last_loan_date "
                + "    FROM loans "
                + "    WHERE loan_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) "
                + "    GROUP BY id_user"
                + ") loans ON loans.id_user = u.id_user "
                + "LEFT JOIN ("
                + "    SELECT id_user, COUNT(*) AS recent_reservations, MAX(reservation_date) AS last_reservation_date "
                + "    FROM reservations "
                + "    WHERE reservation_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) "
                + "    GROUP BY id_user"
                + ") reservations ON reservations.id_user = u.id_user "
                + "WHERE COALESCE(loans.recent_loans, 0) > 0 OR COALESCE(reservations.recent_reservations, 0) > 0 "
                + "ORDER BY last_activity_date DESC, u.name ASC "
                + "LIMIT ?";

        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            throw new IllegalStateException("No se pudo establecer conexion con la base de datos.");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recentWindowDays);
            ps.setInt(2, recentWindowDays);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date lastActivityDate = rs.getDate("last_activity_date");
                    metrics.add(new UserActivityMetric(
                            rs.getInt("id_user"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getInt("recent_loans"),
                            rs.getInt("recent_reservations"),
                            lastActivityDate != null ? lastActivityDate.toString() : ""
                    ));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar usuarios activos: " + e.getMessage(), e);
        }
        return metrics;
    }

    public int countRecentActiveUsers(int recentWindowDays) {
        String sql = "SELECT COUNT(*) "
                + "FROM users u "
                + "WHERE EXISTS ("
                + "    SELECT 1 FROM loans l "
                + "    WHERE l.id_user = u.id_user "
                + "      AND l.loan_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)"
                + ") OR EXISTS ("
                + "    SELECT 1 FROM reservations r "
                + "    WHERE r.id_user = u.id_user "
                + "      AND r.reservation_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)"
                + ")";

        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            throw new IllegalStateException("No se pudo establecer conexion con la base de datos.");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recentWindowDays);
            ps.setInt(2, recentWindowDays);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar usuarios activos: " + e.getMessage(), e);
        }
    }

    private int countByQuery(String sql) {
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            throw new IllegalStateException("No se pudo establecer conexion con la base de datos.");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar metricas del dashboard: " + e.getMessage(), e);
        }
    }
}
