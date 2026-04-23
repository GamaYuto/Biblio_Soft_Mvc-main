package com.mycompany.biblio_soft_mvc.dao;

import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import com.mycompany.biblio_soft_mvc.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Maneja las operaciones de base de datos para la entidad User.
 * Soporta creación, lectura, actualización y eliminación de usuarios.
 */
public class UserDAO {
    /**
     * Agrega un nuevo usuario a la tabla users.
     * @param user Usuario a registrar.
     * @return true si el usuario se insertó correctamente.
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users(name, email, phone) VALUES(?,?,?)";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            int rows = ps.executeUpdate();
            boolean success = rows > 0;
            if (success) {
                System.out.println("Usuario registrado: " + user.getName());
            }
            return success;
        } catch (SQLException e) {
            System.out.println("Error al agregar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera todos los usuarios registrados.
     * @return Lista de usuarios.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return users;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id_user"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return users;
    }

    /**
     * Elimina un usuario por su ID.
     * @param idUser Identificador del usuario.
     * @return true si el usuario fue eliminado.
     */
    public boolean deleteUser(int idUser) {
        String sql = "DELETE FROM users WHERE id_user = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            int rows = ps.executeUpdate();
            boolean success = rows > 0;
            if (success) {
                System.out.println("Usuario eliminado con ID: " + idUser);
            }
            return success;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     * @param user Usuario con los valores actualizados.
     * @return true si la actualización se aplicó.
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ? WHERE id_user = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setInt(4, user.getIdUser());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
}
