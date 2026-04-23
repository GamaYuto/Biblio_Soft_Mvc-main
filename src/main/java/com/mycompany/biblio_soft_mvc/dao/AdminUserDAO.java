package com.mycompany.biblio_soft_mvc.dao;

import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import com.mycompany.biblio_soft_mvc.model.AdminUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO para consultar credenciales de administradores.
 */
public class AdminUserDAO {

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
}
