package com.mycompany.biblio_soft_mvc.controller;

import com.mycompany.biblio_soft_mvc.dao.CategoryDAO;
import com.mycompany.biblio_soft_mvc.model.Category;
import java.util.List;

/**
 * Controlador para consultas de categorias.
 */
public class CategoryController {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> listCategories() {
        return categoryDAO.getAllCategories();
    }

    public Category findCategoryById(int idCategory) {
        return categoryDAO.findById(idCategory);
    }
}
