package com.mycompany.biblio_soft_mvc.dao;

import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import com.mycompany.biblio_soft_mvc.model.AdminUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para consultar credenciales de administradores.
 */
public class AdminUserDAO {

    public List<AdminUser> findAll() {
        List<AdminUser> adminUsers = new ArrayList<>();
        String sql = "SELECT id_admin, username, full_name, created_at FROM admin_users ORDER BY id_admin";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return adminUsers;
        }
        try (conn; PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AdminUser adminUser = new AdminUser();
                adminUser.setIdAdmin(rs.getInt("id_admin"));
                adminUser.setUsername(rs.getString("username"));
                adminUser.setFullName(rs.getString("full_name"));
                adminUser.setCreatedAt(rs.getTimestamp("created_at"));
                adminUsers.add(adminUser);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar administradores: " + e.getMessage());
        }
        return adminUsers;
    }

    public AdminUser findByUsername(String username) {
        String sql = "SELECT id_admin, username, password_hash, full_name, created_at "
                + "FROM admin_users WHERE username = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return null;
        }
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AdminUser(
                            rs.getInt("id_admin"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("full_name"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar administrador por username: " + e.getMessage());
        }
        return null;
    }

    public boolean add(AdminUser adminUser) {
        String sql = "INSERT INTO admin_users(username, password_hash, full_name) VALUES(?,?,?)";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return false;
        }
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, adminUser.getUsername());
            ps.setString(2, adminUser.getPasswordHash());
            ps.setString(3, adminUser.getFullName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al crear administrador: " + e.getMessage());
            return false;
        }
    }

    public boolean update(AdminUser adminUser) {
        String sql = "UPDATE admin_users SET username = ?, full_name = ? WHERE id_admin = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return false;
        }
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, adminUser.getUsername());
            ps.setString(2, adminUser.getFullName());
            ps.setInt(3, adminUser.getIdAdmin());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar administrador: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(int idAdmin, String passwordHash) {
        String sql = "UPDATE admin_users SET password_hash = ? WHERE id_admin = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return false;
        }
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, idAdmin);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar clave de administrador: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int idAdmin) {
        String sql = "DELETE FROM admin_users WHERE id_admin = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexion con la base de datos.");
            return false;
        }
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAdmin);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar administrador: " + e.getMessage());
            return false;
        }
    }
}
