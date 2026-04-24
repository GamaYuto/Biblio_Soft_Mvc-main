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
import java.util.List;
import java.util.Map;

/**
 * Servlet que administra las operaciones sobre prestamos.
 */
@WebServlet({"/resources/loans", "/loans"})
public class LoanServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final LoanController loanController = new LoanController();
    private final Gson gson = new Gson();

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
                int userId = Integer.parseInt(userIdParam);
                loans = loanController.getLoansByUser(userId);
            } else if (bookIdParam != null && !bookIdParam.isBlank()) {
                int bookId = Integer.parseInt(bookIdParam);
                loans = loanController.getLoansByBook(bookId);
            } else {
                loans = loanController.listLoans();
            }
            resp.getWriter().write(gson.toJson(loans));
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Parametro de filtro invalido.")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error al cargar prestamos: " + e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            LoanRequest loanRequest = gson.fromJson(req.getReader(), LoanRequest.class);
            if (loanRequest == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("Cuerpo de la peticion vacio o invalido.")));
                return;
            }

            if ("return".equalsIgnoreCase(loanRequest.action)) {
                if (loanRequest.idLoan <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del prestamo es obligatorio para devolver.")));
                    return;
                }
                boolean returned = loanController.returnBook(loanRequest.idLoan);
                if (returned) {
                    resp.getWriter().write(gson.toJson(Map.of("returned", true)));
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo devolver el libro. Verifica que el prestamo exista y siga activo.")));
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
                resp.getWriter().write(gson.toJson(new ErrorResponse("Fecha invalida: " + e.getMessage())));
                return;
            }

            try {
                SimpleDateFormat dayOnly = new SimpleDateFormat("yyyy-MM-dd");
                Date todayDate = dayOnly.parse(dayOnly.format(new java.util.Date()));
                if (loanDate.compareTo(todayDate) > 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("La fecha de prestamo no puede ser una fecha futura.")));
                    return;
                }
            } catch (ParseException ignored) { }

            if (returnDate != null && returnDate.before(loanDate)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("La fecha de devolucion no puede ser anterior a la fecha de prestamo.")));
                return;
            }

            User user = new User();
            user.setIdUser(loanRequest.user.idUser);
            Book book = new Book();
            book.setIdBook(loanRequest.book.idBook);

            if ("update".equalsIgnoreCase(loanRequest.action)) {
                if (loanRequest.idLoan <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del prestamo es obligatorio para actualizar.")));
                    return;
                }
                boolean updated = loanController.updateLoan(loanRequest.idLoan, loanDate, returnDate, user, book);
                if (updated) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(loanRequest));
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo actualizar el prestamo. Verifica que el libro destino este disponible.")));
                }
                return;
            }

            String errorMsg = loanController.registerLoanWithError(loanDate, returnDate, user, book);
            if (errorMsg == null) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(loanRequest));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse(errorMsg)));
            }
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("JSON invalido: " + e.getMessage())));
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        int idLoan = parseIdFromQuery(req);
        if (idLoan <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El ID del prestamo es obligatorio.")));
            return;
        }
        boolean returned = loanController.returnBook(idLoan);
        if (returned) {
            resp.getWriter().write(gson.toJson(Map.of("returned", true)));
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("No se pudo devolver el libro.")));
        }
    }

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
