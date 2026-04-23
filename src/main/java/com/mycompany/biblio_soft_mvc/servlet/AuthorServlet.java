package com.mycompany.biblio_soft_mvc.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mycompany.biblio_soft_mvc.controller.AuthorController;
import com.mycompany.biblio_soft_mvc.model.Author;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servlet que brinda la API REST para la entidad Author.
 * Gestiona listar, crear, actualizar y eliminar autores.
 */
@WebServlet({"/resources/authors", "/authors"})
public class AuthorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final AuthorController authorController = new AuthorController();
    private final Gson gson = new Gson();

    /**
     * Devuelve la lista de autores en formato JSON.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            List<Author> authors = authorController.listAuthors();
            resp.getWriter().write(gson.toJson(authors));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error al cargar autores: " + e.getMessage())));
        }
    }

    /**
     * Procesa las operaciones POST para crear, actualizar o eliminar autores.
     * La acción se determina por el campo request.action.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            AuthorRequest request = gson.fromJson(req.getReader(), AuthorRequest.class);
            if (request == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("JSON inválido.")));
                return;
            }

            if ("delete".equalsIgnoreCase(request.action)) {
                if (request.idAuthor <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del autor es obligatorio para eliminar.")));
                    return;
                }
                boolean deleted = authorController.deleteAuthor(request.idAuthor);
                if (deleted) {
                    resp.getWriter().write(gson.toJson(Map.of("deleted", true)));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo eliminar el autor.")));
                }
                return;
            }

            if ("update".equalsIgnoreCase(request.action)) {
                if (request.idAuthor <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del autor es obligatorio para actualizar.")));
                    return;
                }
                if (!validateAuthorFields(request, resp)) {
                    return;
                }
                boolean updated = authorController.updateAuthor(request.idAuthor, request.name.trim(), request.nationality != null ? request.nationality.trim() : null);
                if (updated) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(request));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo actualizar el autor.")));
                }
                return;
            }

            if (!validateAuthorFields(request, resp)) {
                return;
            }
            boolean created = authorController.registerAuthor(request.name.trim(), request.nationality != null ? request.nationality.trim() : null);
            if (created) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(request));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo registrar el autor en la base de datos.")));
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
     * Valida los campos del autor antes de crear o actualizar.
     * @param request Datos del autor.
     * @param resp Respuesta HTTP para devolver errores.
     * @return true si los datos son válidos.
     * @throws IOException Si ocurre error al escribir la respuesta.
     */
    private boolean validateAuthorFields(AuthorRequest request, HttpServletResponse resp) throws IOException {
        if (request.name == null || request.name.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El nombre del autor es obligatorio.")));
            return false;
        }
        if (!request.name.trim().matches("[\\p{L}\\s]+")) {
            // Verifica que el nombre solo contenga letras y espacios.
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El nombre del autor solo puede contener letras y espacios.")));
            return false;
        }
        if (request.nationality != null && !request.nationality.isBlank()
                && !request.nationality.trim().matches("[\\p{L}\\s]+")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("La nacionalidad solo puede contener letras y espacios.")));
            return false;
        }
        return true;
    }

    /**
     * Elimina un autor usando el parámetro query id.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del autor es obligatorio.")));
            return;
        }
        try {
            int idAuthor = Integer.parseInt(idParam);
            boolean deleted = authorController.deleteAuthor(idAuthor);
            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(Map.of("deleted", true)));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo eliminar el autor.")));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("ID inválido.")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error del servidor: " + e.getMessage())));
        }
    }

    private static class AuthorRequest {
        private String action;
        private int idAuthor;
        private String name;
        private String nationality;
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
