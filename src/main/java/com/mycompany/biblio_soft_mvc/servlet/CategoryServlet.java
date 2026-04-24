package com.mycompany.biblio_soft_mvc.servlet;

import com.google.gson.Gson;
import com.mycompany.biblio_soft_mvc.controller.CategoryController;
import com.mycompany.biblio_soft_mvc.model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet que expone la consulta de categorias.
 */
@WebServlet({"/resources/categories", "/categories"})
public class CategoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final CategoryController categoryController = new CategoryController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            List<Category> categories = categoryController.listCategories();
            resp.getWriter().write(gson.toJson(categories));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error al cargar categorias: " + e.getMessage())));
        }
    }

    private static class ErrorResponse {
        private final String error;

        ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
