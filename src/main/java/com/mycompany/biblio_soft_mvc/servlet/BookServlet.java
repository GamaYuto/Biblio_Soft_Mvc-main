package com.mycompany.biblio_soft_mvc.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mycompany.biblio_soft_mvc.controller.BookController;
import com.mycompany.biblio_soft_mvc.model.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servlet que ofrece la API REST para la entidad Book.
 * Permite listar, buscar, crear, actualizar y eliminar libros.
 */
@WebServlet({"/resources/books", "/books"})
public class BookServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final BookController bookController = new BookController();
    private final Gson gson = new Gson();

    /**
     * Maneja consultas GET con filtros opcionales por título, autor o ISBN.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String titleSearch = req.getParameter("search");
            String authorSearch = req.getParameter("author");
            String isbnSearch = req.getParameter("isbn");
            List<Book> books;
            if (titleSearch != null && !titleSearch.isBlank()) {
                books = bookController.searchBooks(titleSearch);
            } else if (authorSearch != null && !authorSearch.isBlank()) {
                books = bookController.searchBooksByAuthor(authorSearch);
            } else if (isbnSearch != null && !isbnSearch.isBlank()) {
                books = bookController.searchBooksByIsbn(isbnSearch);
            } else {
                books = bookController.listBooks();
            }
            resp.getWriter().write(gson.toJson(books));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error al cargar libros: " + e.getMessage())));
        }
    }

    /**
     * Procesa POST para crear, actualizar o eliminar libros.
     * La acción se decide mediante el campo action del JSON.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            BookRequest request = gson.fromJson(req.getReader(), BookRequest.class);
            if (request == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("JSON inválido.")));
                return;
            }

            if ("delete".equalsIgnoreCase(request.action)) {
                if (request.idBook <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del libro es obligatorio para eliminar.")));
                    return;
                }
                boolean deleted = bookController.deleteBook(request.idBook);
                if (deleted) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(Map.of("deleted", true)));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo eliminar el libro.")));
                }
                return;
            }

            if ("update".equalsIgnoreCase(request.action)) {
                if (request.idBook <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del libro es obligatorio para actualizar.")));
                    return;
                }
                if (!validateBookFields(request, resp)) {
                    return;
                }
                boolean updated = bookController.updateBook(request.idBook, request.title.trim(), request.isbn.trim(), request.year, request.author);
                if (updated) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(request));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo actualizar el libro.")));
                }
                return;
            }

            if (!validateBookFields(request, resp)) {
                return;
            }
            boolean created = bookController.registerBook(request.title.trim(), request.isbn.trim(), request.year, request.author);
            if (created) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(request));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo registrar el libro en la base de datos.")));
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
     * Valida los campos del libro antes de procesar la petición.
     * @param request Datos del libro.
     * @param resp Respuesta HTTP para retornar errores.
     * @return true si los datos son válidos.
     * @throws IOException Si falla la escritura en la respuesta.
     */
    private boolean validateBookFields(BookRequest request, HttpServletResponse resp) throws IOException {
        if (request.title == null || request.title.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El título del libro es obligatorio.")));
            return false;
        }
        if (request.author == null || request.author.getIdAuthor() <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El autor del libro es obligatorio.")));
            return false;
        }
        if (request.isbn == null || request.isbn.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El ISBN del libro es obligatorio.")));
            return false;
        }
        if (!request.isbn.trim().matches("[0-9\\-]+")) {
            // El ISBN solo puede contener dígitos y guiones.
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El ISBN solo puede contener dígitos y guiones.")));
            return false;
        }
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        if (request.year < 1000 || request.year > currentYear) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El año debe estar entre 1000 y " + currentYear + ".")));
            return false;
        }
        return true;
    }

    private static class BookRequest {
        private String action;
        private int idBook;
        private String title;
        private String isbn;
        private int year;
        private com.mycompany.biblio_soft_mvc.model.Author author;
    }

    /**
     * Actualiza un libro a través de la petición PUT.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            Book book = gson.fromJson(req.getReader(), Book.class);
            if (book == null || book.getIdBook() <= 0 || book.getTitle() == null || book.getTitle().isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("ID y título del libro son obligatorios.")));
                return;
            }
            if (book.getAuthor() == null || book.getAuthor().getIdAuthor() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("El autor del libro es obligatorio.")));
                return;
            }
            boolean updated = bookController.updateBook(book.getIdBook(), book.getTitle(), book.getIsbn(), book.getYear(), book.getAuthor());
            if (updated) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(book));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo actualizar el libro.")));
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
     * Elimina un libro por ID recibido como parámetro de consulta.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del libro es obligatorio.")));
            return;
        }
        try {
            int idBook = Integer.parseInt(idParam);
            boolean deleted = bookController.deleteBook(idBook);
            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(Map.of("deleted", true)));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo eliminar el libro.")));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("ID inválido.")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error del servidor: " + e.getMessage())));
        }
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
