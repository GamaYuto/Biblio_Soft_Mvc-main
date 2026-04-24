package com.mycompany.biblio_soft_mvc.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mycompany.biblio_soft_mvc.controller.BookController;
import com.mycompany.biblio_soft_mvc.controller.HistoryController;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.BookHistory;
import com.mycompany.biblio_soft_mvc.model.BookStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Servlet que ofrece la API REST para la entidad Book.
 */
@WebServlet({"/resources/books", "/books"})
public class BookServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final BookController bookController = new BookController();
    private final HistoryController historyController = new HistoryController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String titleSearch = req.getParameter("search");
            String authorSearch = req.getParameter("author");
            String isbnSearch = req.getParameter("isbn");
            Integer categoryId = parseOptionalInt(req.getParameter("categoryId"));
            Integer yearFrom = parseOptionalInt(req.getParameter("yearFrom"));
            Integer yearTo = parseOptionalInt(req.getParameter("yearTo"));
            String availableOnly = req.getParameter("available");
            String reservableOnly = req.getParameter("reservable");
            if ("true".equalsIgnoreCase(req.getParameter("history"))) {
                int idBook = parseHistoryId(req, "idBook");
                if (idBook <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del libro es obligatorio para consultar historial.")));
                    return;
                }

                Date dateFrom = parseDate(req.getParameter("dateFrom"));
                Date dateTo = parseDate(req.getParameter("dateTo"));
                BookHistory history = historyController.getBookHistory(
                        idBook,
                        dateFrom,
                        dateTo,
                        req.getParameter("loanStatus"),
                        req.getParameter("reservationStatus")
                );
                if (history == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se encontro el libro solicitado.")));
                    return;
                }
                resp.getWriter().write(gson.toJson(history));
                return;
            }
            List<Book> books;
            if ("true".equalsIgnoreCase(availableOnly)) {
                books = bookController.listAvailableBooks();
            } else if ("true".equalsIgnoreCase(reservableOnly)) {
                books = bookController.listReservableBooks();
            } else if (hasAdvancedFilters(titleSearch, authorSearch, isbnSearch, categoryId, yearFrom, yearTo)) {
                if (yearFrom != null && yearTo != null && yearFrom > yearTo) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El anio inicial no puede ser mayor que el anio final.")));
                    return;
                }
                books = bookController.searchBooksAdvanced(titleSearch, authorSearch, isbnSearch, categoryId, yearFrom, yearTo);
            } else {
                books = bookController.listBooks();
            }
            resp.getWriter().write(gson.toJson(books));
        } catch (ParseException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Formato de fecha invalido. Use yyyy-MM-dd.")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error al cargar libros: " + e.getMessage())));
        }
    }

    private int parseHistoryId(HttpServletRequest req, String primaryParam) {
        String idValue = req.getParameter(primaryParam);
        if (idValue == null || idValue.isBlank()) {
            idValue = req.getParameter("id");
        }
        if (idValue == null || idValue.isBlank()) {
            return -1;
        }
        try {
            return Integer.parseInt(idValue);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private Integer parseOptionalInt(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean hasAdvancedFilters(String title, String author, String isbn, Integer categoryId, Integer yearFrom, Integer yearTo) {
        return (title != null && !title.isBlank())
                || (author != null && !author.isBlank())
                || (isbn != null && !isbn.isBlank())
                || (categoryId != null && categoryId > 0)
                || yearFrom != null
                || yearTo != null;
    }

    private Date parseDate(String value) throws ParseException {
        if (value == null || value.isBlank()) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        return format.parse(value);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            BookRequest request = gson.fromJson(req.getReader(), BookRequest.class);
            if (request == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("JSON invalido.")));
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

            if (!validateBookFields(request, resp)) {
                return;
            }

            if ("update".equalsIgnoreCase(request.action)) {
                if (request.idBook <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del libro es obligatorio para actualizar.")));
                    return;
                }
                boolean updated = bookController.updateBook(
                        request.idBook,
                        request.title.trim(),
                        request.isbn.trim(),
                        request.year,
                        request.status,
                        request.author,
                        request.category
                );
                if (updated) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(request));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo actualizar el libro.")));
                }
                return;
            }

            boolean created = bookController.registerBook(
                    request.title.trim(),
                    request.isbn.trim(),
                    request.year,
                    request.status,
                    request.author,
                    request.category
            );
            if (created) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(request));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo registrar el libro en la base de datos.")));
            }
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("JSON invalido: " + e.getMessage())));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error del servidor: " + e.getMessage())));
        }
    }

    private boolean validateBookFields(BookRequest request, HttpServletResponse resp) throws IOException {
        if (request.title == null || request.title.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El titulo del libro es obligatorio.")));
            return false;
        }
        if (request.author == null || request.author.getIdAuthor() <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El autor del libro es obligatorio.")));
            return false;
        }
        if (request.category == null || request.category.getIdCategory() <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("La categoria del libro es obligatoria.")));
            return false;
        }
        if (request.isbn == null || request.isbn.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El ISBN del libro es obligatorio.")));
            return false;
        }
        if (!request.isbn.trim().matches("[0-9\\-]+")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El ISBN solo puede contener digitos y guiones.")));
            return false;
        }
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        if (request.year < 1000 || request.year > currentYear) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El anio debe estar entre 1000 y " + currentYear + ".")));
            return false;
        }
        if (!BookStatus.isValid(request.status)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El estado del libro es invalido.")));
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
        private String status;
        private com.mycompany.biblio_soft_mvc.model.Author author;
        private com.mycompany.biblio_soft_mvc.model.Category category;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            Book book = gson.fromJson(req.getReader(), Book.class);
            if (book == null || book.getIdBook() <= 0 || book.getTitle() == null || book.getTitle().isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("ID y titulo del libro son obligatorios.")));
                return;
            }
            if (book.getAuthor() == null || book.getAuthor().getIdAuthor() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("El autor del libro es obligatorio.")));
                return;
            }
            if (book.getCategory() == null || book.getCategory().getIdCategory() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("La categoria del libro es obligatoria.")));
                return;
            }
            if (!BookStatus.isValid(book.getStatus())) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("El estado del libro es invalido.")));
                return;
            }
            boolean updated = bookController.updateBook(
                    book.getIdBook(),
                    book.getTitle(),
                    book.getIsbn(),
                    book.getYear(),
                    book.getStatus(),
                    book.getAuthor(),
                    book.getCategory()
            );
            if (updated) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(book));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo actualizar el libro.")));
            }
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("JSON invalido: " + e.getMessage())));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error del servidor: " + e.getMessage())));
        }
    }

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
            resp.getWriter().write(gson.toJson(new ErrorResponse("ID invalido.")));
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
