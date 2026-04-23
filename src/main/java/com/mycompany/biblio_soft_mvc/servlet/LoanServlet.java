package com.mycompany.biblio_soft_mvc.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mycompany.biblio_soft_mvc.controller.LoanController;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.Loan;
import com.mycompany.biblio_soft_mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * Servlet que administra las operaciones sobre préstamos.
 * Soporta consultas filtradas, creación, actualización y devolución de libros.
 */
@WebServlet({"/resources/loans", "/loans"})
public class LoanServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final LoanController loanController = new LoanController();
    private final Gson gson = new Gson();

    /**
     * Maneja consultas GET para listado de préstamos.
     * Puede devolver préstamos activos, histórico, por usuario o por libro.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String historyParam = req.getParameter("history");
        String userIdParam = req.getParameter("userId");
        String bookIdParam = req.getParameter("bookId");

        List<Loan> loans;
        try {
            if (historyParam != null && historyParam.equalsIgnoreCase("true")) {
                loans = loanController.getLoanHistory();
            } else if (userIdParam != null && !userIdParam.isBlank()) {
                try {
                    int userId = Integer.parseInt(userIdParam);
                    loans = loanController.getLoansByUser(userId);
                } catch (NumberFormatException ex) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("userId inválido.")));
                    return;
                }
            } else if (bookIdParam != null && !bookIdParam.isBlank()) {
                try {
                    int bookId = Integer.parseInt(bookIdParam);
                    loans = loanController.getLoansByBook(bookId);
                } catch (NumberFormatException ex) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("bookId inválido.")));
                    return;
                }
            } else {
                loans = loanController.listLoans();
            }
            resp.getWriter().write(gson.toJson(loans));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error al cargar préstamos: " + e.getMessage())));
        }
    }

    /**
     * Procesa la creación, actualización o devolución de préstamos.
     * La acción se determina por el campo action del JSON.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            LoanRequest loanRequest = gson.fromJson(req.getReader(), LoanRequest.class);
            if (loanRequest == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("Cuerpo de la petición vacío o inválido.")));
                return;
            }

            if ("return".equalsIgnoreCase(loanRequest.action)) {
                if (loanRequest.idLoan <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del préstamo es obligatorio para devolver.")));
                    return;
                }
                boolean returned = loanController.returnBook(loanRequest.idLoan);
                if (returned) {
                    resp.getWriter().write(gson.toJson(Map.of("returned", true)));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo devolver el libro. Verifique que el préstamo exista y no haya sido devuelto ya.")));
                }
                return;
            }

            if (loanRequest.user == null || loanRequest.book == null
                    || loanRequest.user.idUser <= 0 || loanRequest.book.idBook <= 0
                    || loanRequest.loanDate == null || loanRequest.loanDate.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("LoanDate, usuario y libro son obligatorios.")));
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date loanDate;
            Date returnDate = null;
            try {
                loanDate = sdf.parse(loanRequest.loanDate);
                if (loanRequest.returnDate != null && !loanRequest.returnDate.isBlank()) {
                    returnDate = sdf.parse(loanRequest.returnDate);
                }
            } catch (ParseException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("Fecha inválida: " + e.getMessage())));
                return;
            }

            try {
                SimpleDateFormat dayOnly = new SimpleDateFormat("yyyy-MM-dd");
                Date todayDate = dayOnly.parse(dayOnly.format(new java.util.Date()));
                if (loanDate.compareTo(todayDate) > 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("La fecha de préstamo no puede ser una fecha futura.")));
                    return;
                }
            } catch (ParseException ignored) { }

            if (returnDate != null && returnDate.before(loanDate)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("La fecha de devolución no puede ser anterior a la fecha de préstamo.")));
                return;
            }

            User user = new User();
            user.setIdUser(loanRequest.user.idUser);
            Book book = new Book();
            book.setIdBook(loanRequest.book.idBook);

            if ("update".equalsIgnoreCase(loanRequest.action)) {
                if (loanRequest.idLoan <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del préstamo es obligatorio para actualizar.")));
                    return;
                }
                boolean updated = loanController.updateLoan(loanRequest.idLoan, loanDate, returnDate, user, book);
                if (updated) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(loanRequest));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo actualizar el préstamo.")));
                }
                return;
            }

            String errorMsg = loanController.registerLoanWithError(loanDate, returnDate, user, book);
            if (errorMsg == null) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(loanRequest));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(new ErrorResponse(errorMsg)));
            }
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("JSON inválido: " + e.getMessage())));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error del servidor: " + e.getMessage())));
        }
    }

    private static class LoanRequest {
        private String action;
        private int idLoan;
        private String loanDate;
        private String returnDate;
        private IdWrapper user;
        private IdWrapper book;
    }

    private static class IdWrapper {
        private int idUser;
        private int idBook;
    }

    /**
     * Marca un préstamo como devuelto usando el parámetro id de la query.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        int idLoan = parseIdFromQuery(req);
        if (idLoan <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del préstamo es obligatorio.")));
            return;
        }
        boolean returned = loanController.returnBook(idLoan);
        if (returned) {
            resp.getWriter().write(gson.toJson(Map.of("returned", true)));
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo devolver el libro.")));
        }
    }

    /**
     * Extrae el parámetro id del query string manualmente.
     * @param req Petición HTTP.
     * @return El ID parseado, o -1 si no es válido.
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

    private static class ErrorResponse {
        private final String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }
}
