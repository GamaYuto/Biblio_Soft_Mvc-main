package com.mycompany.biblio_soft_mvc.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mycompany.biblio_soft_mvc.controller.ReservationController;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.Reservation;
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
 * Servlet REST para gestionar reservas.
 */
@WebServlet({"/resources/reservations", "/reservations"})
public class ReservationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ReservationController reservationController = new ReservationController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String historyParam = req.getParameter("history");
        String userIdParam = req.getParameter("userId");
        String bookIdParam = req.getParameter("bookId");

        List<Reservation> reservations;
        try {
            if (historyParam != null && historyParam.equalsIgnoreCase("true")) {
                reservations = reservationController.getReservationHistory();
            } else if (userIdParam != null && !userIdParam.isBlank()) {
                reservations = reservationController.getReservationsByUser(Integer.parseInt(userIdParam));
            } else if (bookIdParam != null && !bookIdParam.isBlank()) {
                reservations = reservationController.getReservationsByBook(Integer.parseInt(bookIdParam));
            } else {
                reservations = reservationController.listActiveReservations();
            }
            resp.getWriter().write(gson.toJson(reservations));
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Parametro de filtro invalido.")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Error al cargar reservas: " + e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            ReservationRequest reservationRequest = gson.fromJson(req.getReader(), ReservationRequest.class);
            if (reservationRequest == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("Cuerpo de la peticion vacio o invalido.")));
                return;
            }

            if ("cancel".equalsIgnoreCase(reservationRequest.action)) {
                handleStatusChange(resp, reservationRequest.idReservation, true);
                return;
            }

            if ("fulfill".equalsIgnoreCase(reservationRequest.action)) {
                handleStatusChange(resp, reservationRequest.idReservation, false);
                return;
            }

            if (reservationRequest.user == null || reservationRequest.book == null
                    || reservationRequest.user.idUser <= 0 || reservationRequest.book.idBook <= 0
                    || reservationRequest.reservationDate == null || reservationRequest.reservationDate.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("Fecha de reserva, usuario y libro son obligatorios.")));
                return;
            }

            Date reservationDate;
            try {
                reservationDate = new SimpleDateFormat("yyyy-MM-dd").parse(reservationRequest.reservationDate);
            } catch (ParseException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("Fecha invalida: " + e.getMessage())));
                return;
            }

            User user = new User();
            user.setIdUser(reservationRequest.user.idUser);
            Book book = new Book();
            book.setIdBook(reservationRequest.book.idBook);

            String errorMsg = reservationController.registerReservationWithError(reservationDate, user, book);
            if (errorMsg == null) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(reservationRequest));
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

    private void handleStatusChange(HttpServletResponse resp, int idReservation, boolean cancel) throws IOException {
        if (idReservation <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("El ID de la reserva es obligatorio.")));
            return;
        }

        boolean updated = cancel
                ? reservationController.cancelReservation(idReservation)
                : reservationController.fulfillReservation(idReservation);

        if (updated) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(Map.of(
                    "updated", true,
                    "status", cancel ? "CANCELADA" : "ATENDIDA"
            )));
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse(
                    cancel ? "No se pudo cancelar la reserva." : "No se pudo atender la reserva."
            )));
        }
    }

    private static class ReservationRequest {
        private String action;
        private int idReservation;
        private String reservationDate;
        private IdWrapper user;
        private IdWrapper book;
    }

    private static class IdWrapper {
        private int idUser;
        private int idBook;
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
