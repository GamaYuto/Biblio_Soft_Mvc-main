package com.mycompany.biblio_soft_mvc.controller;

import com.mycompany.biblio_soft_mvc.dao.DashboardDAO;
import com.mycompany.biblio_soft_mvc.model.DashboardMetrics;

/**
 * Coordina la carga de metricas para el panel principal.
 */
public class DashboardController {
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public DashboardMetrics getDashboardMetrics() {
        return dashboardDAO.getDashboardMetrics();
    }
}
