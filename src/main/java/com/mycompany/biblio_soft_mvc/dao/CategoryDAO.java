package com.mycompany.biblio_soft_mvc.dao;

import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import com.mycompany.biblio_soft_mvc.model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Maneja el acceso a datos de categorias.
 */
public class CategoryDAO {

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id_category, name, description FROM categories ORDER BY name";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            throw new IllegalStateException("No se pudo establecer conexion con la base de datos.");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("id_category"),
                        rs.getString("name"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar categorias: " + e.getMessage(), e);
        }
        return categories;
    }

    public Category findById(int idCategory) {
        String sql = "SELECT id_category, name, description FROM categories WHERE id_category = ?";
        Connection conn = ConnectionDB.connect();
        if (conn == null) {
            throw new IllegalStateException("No se pudo establecer conexion con la base de datos.");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCategory);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                            rs.getInt("id_category"),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al buscar categoria: " + e.getMessage(), e);
        }
        return null;
    }
}
