package com.mycompany.biblio_soft_mvc.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mycompany.biblio_soft_mvc.controller.UserController;
import com.mycompany.biblio_soft_mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Servlet que expone la API REST para la entidad User.
 * Permite listar, crear, actualizar y eliminar usuarios.
 */
@WebServlet({"/resources/users", "/users"})
public class UserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserController userController = new UserController();
    private final Gson gson = new Gson();

    /**
     * Devuelve la lista de usuarios en JSON.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            resp.getWriter().write(gson.toJson(userController.listUsers()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error al cargar usuarios: " + e.getMessage())));
        }
    }

    /**
     * Procesa la creación, actualización o eliminación de usuarios.
     * La acción se decide por el campo action en el JSON.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            UserRequest request = gson.fromJson(req.getReader(), UserRequest.class);
            if (request == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("JSON inválido.")));
                return;
            }

            if ("delete".equalsIgnoreCase(request.action)) {
                if (request.idUser <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del usuario es obligatorio para eliminar.")));
                    return;
                }
                boolean deleted = userController.deleteUser(request.idUser);
                if (deleted) {
                    resp.getWriter().write(gson.toJson(Map.of("deleted", true)));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo eliminar el usuario.")));
                }
                return;
            }

            if ("update".equalsIgnoreCase(request.action)) {
                if (request.idUser <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del usuario es obligatorio para actualizar.")));
                    return;
                }
                if (!validateUserFields(request, resp)) {
                    return;
                }
                boolean updated = userController.updateUser(request.idUser, request.name.trim(), request.email.trim(), request.phone.trim());
                if (updated) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(request));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo actualizar el usuario.")));
                }
                return;
            }

            if (!validateUserFields(request, resp)) {
                return;
            }
            boolean created = userController.registerUser(request.name.trim(), request.email.trim(), request.phone.trim());
            if (created) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(request));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo registrar el usuario.")));
            }
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("JSON inválido: " + e.getMessage())));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error del servidor: " + e.getMessage())));
        }
    }

    /**
     * Valida los campos del usuario con reglas básicas de formato.
     * @param request Datos del usuario.
     * @param resp Respuesta HTTP para escribir errores.
     * @return true si la validación es satisfactoria.
     * @throws IOException Si falla la escritura en la respuesta.
     */
    private boolean validateUserFields(UserRequest request, HttpServletResponse resp) throws IOException {
        if (request.name == null || request.name.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El nombre del usuario es obligatorio.")));
            return false;
        }
        if (!request.name.trim().matches("[\\p{L}\\s]+")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El nombre solo puede contener letras y espacios.")));
            return false;
        }
        if (request.email == null || request.email.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El email del usuario es obligatorio.")));
            return false;
        }
        if (!request.email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El email no tiene un formato válido.")));
            return false;
        }
        if (request.phone == null || request.phone.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El teléfono del usuario es obligatorio.")));
            return false;
        }
        if (!request.phone.trim().matches("\\d{1,15}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El teléfono debe contener solo dígitos (máximo 15).")));
            return false;
        }
        return true;
    }

    /**
     * Elimina un usuario usando el ID en el query string.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        int idUser = parseIdFromQuery(req);
        if (idUser <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del usuario es obligatorio.")));
            return;
        }
        boolean deleted = userController.deleteUser(idUser);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(Map.of("deleted", true)));
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo eliminar el usuario.")));
        }
    }

    /**
     * Obtiene el ID del usuario desde el query string.
     * @param req Petición HTTP.
     * @return El ID parseado o -1 si no es válido.
     */
    private int parseIdFromQuery(HttpServletRequest req) {
        String query = req.getQueryString();
        if (query == null) return -1;
        for (String part : query.split("&")) {
            String[] pair = part.split("=", 2);
            if (pair.length == 2 && "id".equals(pair[0])) {
                try {
                    return Integer.parseInt(pair[1]);
                } catch (NumberFormatException ex) {
                    return -1;
                }
            }
        }
        return -1;
    }

    private static class UserRequest {
        private String action;
        private int idUser;
        private String name;
        private String email;
        private String phone;
    }

    private static class ErrorResponse {
        private final String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }
}
