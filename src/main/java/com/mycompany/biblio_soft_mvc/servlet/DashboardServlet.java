package com.mycompany.biblio_soft_mvc.servlet;

import com.google.gson.Gson;
import com.mycompany.biblio_soft_mvc.controller.DashboardController;
import com.mycompany.biblio_soft_mvc.model.DashboardMetrics;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Expone las metricas agregadas del dashboard en formato JSON.
 */
@WebServlet({"/resources/dashboard-metrics", "/dashboard-metrics"})
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final DashboardController dashboardController = new DashboardController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            DashboardMetrics metrics = dashboardController.getDashboardMetrics();
            resp.getWriter().write(gson.toJson(metrics));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error al cargar metricas del dashboard: " + e.getMessage())));
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
