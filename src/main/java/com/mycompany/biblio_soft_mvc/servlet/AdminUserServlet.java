package com.mycompany.biblio_soft_mvc.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mycompany.biblio_soft_mvc.controller.AdminUserController;
import com.mycompany.biblio_soft_mvc.model.AdminUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * API para la administracion de usuarios de acceso al sistema.
 */
@WebServlet({"/resources/admin-users", "/admin-users"})
public class AdminUserServlet extends HttpServlet {

    private final AdminUserController adminUserController = new AdminUserController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            List<AdminUser> adminUsers = adminUserController.listAdminUsers();
            resp.getWriter().write(gson.toJson(adminUsers));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error al cargar administradores: " + e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            AdminUserRequest request = gson.fromJson(req.getReader(), AdminUserRequest.class);
            if (request == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("JSON invalido.")));
                return;
            }

            if ("delete".equalsIgnoreCase(request.action)) {
                if (request.idAdmin <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del administrador es obligatorio para eliminar.")));
                    return;
                }

                AdminUser currentAdmin = (AdminUser) req.getSession(false).getAttribute(LoginServlet.SESSION_ADMIN_USER);
                if (currentAdmin != null && currentAdmin.getIdAdmin() == request.idAdmin) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No puedes eliminar tu propia cuenta mientras estas autenticado.")));
                    return;
                }

                boolean deleted = adminUserController.deleteAdminUser(request.idAdmin);
                if (deleted) {
                    resp.getWriter().write(gson.toJson(Map.of("deleted", true)));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo eliminar el administrador.")));
                }
                return;
            }

            if ("update".equalsIgnoreCase(request.action)) {
                if (request.idAdmin <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del administrador es obligatorio para actualizar.")));
                    return;
                }
                if (!validateRequest(request, resp, false)) {
                    return;
                }

                boolean updated = adminUserController.updateAdminUser(
                        request.idAdmin,
                        request.username.trim(),
                        normalizeOptional(request.fullName),
                        request.password
                );
                if (updated) {
                    resp.getWriter().write(gson.toJson(Map.of("updated", true)));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo actualizar el administrador.")));
                }
                return;
            }

            if (!validateRequest(request, resp, true)) {
                return;
            }

            boolean created = adminUserController.createAdminUser(
                    request.username.trim(),
                    normalizeOptional(request.fullName),
                    request.password
            );
            if (created) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(Map.of("created", true)));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo crear el administrador. Verifica que el username no este duplicado.")));
            }
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("JSON invalido: " + e.getMessage())));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error del servidor: " + e.getMessage())));
        }
    }

    private boolean validateRequest(AdminUserRequest request, HttpServletResponse resp, boolean passwordRequired) throws IOException {
        if (request.username == null || request.username.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El username es obligatorio.")));
            return false;
        }
        if (!request.username.trim().matches("[A-Za-z0-9._-]{3,30}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El username debe tener entre 3 y 30 caracteres alfanumericos y puede incluir . _ -")));
            return false;
        }
        if (request.fullName != null && !request.fullName.isBlank() && !request.fullName.trim().matches("[\\p{L}\\s]{3,100}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El nombre completo solo puede contener letras y espacios.")));
            return false;
        }
        if (passwordRequired && (request.password == null || request.password.isBlank())) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("La contrasena es obligatoria.")));
            return false;
        }
        if (request.password != null && !request.password.isBlank() && request.password.length() < 8) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("La contrasena debe tener al menos 8 caracteres.")));
            return false;
        }
        return true;
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static class AdminUserRequest {
        private String action;
        private int idAdmin;
        private String username;
        private String fullName;
        private String password;
    }

    private static class ErrorResponse {
        private final String error;

        private ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
