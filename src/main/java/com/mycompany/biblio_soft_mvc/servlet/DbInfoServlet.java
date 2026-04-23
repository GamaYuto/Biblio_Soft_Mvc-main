package com.mycompany.biblio_soft_mvc.servlet;

import com.google.gson.Gson;
import com.mycompany.biblio_soft_mvc.database.ConnectionDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet que expone información básica de la conexión a la base de datos.
 */
@WebServlet("/resources/dbinfo")
public class DbInfoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    /**
     * Devuelve metadatos de la conexión a la base de datos.
     * @param req Petición HTTP.
     * @param resp Respuesta HTTP con JSON de datos de la conexión.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try (Connection conn = ConnectionDB.connect()) {
            if (conn == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(Map.of("error", "No se pudo conectar a la base de datos.")));
                return;
            }
            DatabaseMetaData metaData = conn.getMetaData();
            Map<String, Object> info = new HashMap<>();
            info.put("jdbcUrl", metaData.getURL());
            info.put("user", metaData.getUserName());
            info.put("database", conn.getCatalog());
            info.put("driver", metaData.getDriverName());
            info.put("driverVersion", metaData.getDriverVersion());
            resp.getWriter().write(gson.toJson(info));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
}
