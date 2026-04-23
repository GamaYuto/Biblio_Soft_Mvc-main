package com.mycompany.biblio_soft_mvc.dao;

import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import com.mycompany.biblio_soft_mvc.model.Author;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Maneja las operaciones de base de datos para la entidad Author.
 * Proporciona métodos CRUD para autores.
 */
public class AuthorDAO {
    /**
     * Inserta un nuevo autor en la tabla authors.
     * @param author Autor a registrar.
     * @return true si el autor se agregó correctamente.
     */
    public boolean addAuthor(Author author) {
        String sql = "INSERT INTO authors(name, nationality) VALUES(?,?)";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return false;
        }

        try (Connection c = conn; PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, author.getName());
            ps.setString(2, author.getNationality());
            int rows = ps.executeUpdate();
            boolean success = rows > 0;
            if (success) {
                System.out.println("Autor registrado: " + author.getName());
            } else {
                System.out.println("No se insertó el autor: " + author.getName());
            }
            return success;
        } catch (SQLException e) {
            System.out.println("Error al agregar autor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de un autor existente.
     * @param author Autor con los datos actualizados.
     * @return true si la actualización afectó al menos una fila.
     */
    public boolean updateAuthor(Author author) {
        String sql = "UPDATE authors SET name = ?, nationality = ? WHERE id_author = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, author.getName());
            ps.setString(2, author.getNationality());
            ps.setInt(3, author.getIdAuthor());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar autor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un autor por su identificador.
     * @param idAuthor Identificador del autor a eliminar.
     * @return true si el autor fue eliminado.
     */
    public boolean deleteAuthor(int idAuthor) {
        String sql = "DELETE FROM authors WHERE id_author = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAuthor);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar autor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera todos los autores de la base de datos.
     * @return Lista de autores existentes.
     */
    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM authors";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            throw new IllegalStateException("No se pudo establecer conexión con la base de datos.");
        }
        try (Connection c = conn; PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Author author = new Author(
                    rs.getInt("id_author"),
                    rs.getString("name"),
                    rs.getString("nationality")
                );
                authors.add(author);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar autores: " + e.getMessage());
        }
        return authors;
    }
}
