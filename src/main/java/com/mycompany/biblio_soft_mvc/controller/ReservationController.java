package com.mycompany.biblio_soft_mvc.controller;

import com.mycompany.biblio_soft_mvc.dao.BookDAO;
import com.mycompany.biblio_soft_mvc.dao.LoanDAO;
import com.mycompany.biblio_soft_mvc.dao.ReservationDAO;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.BookStatus;
import com.mycompany.biblio_soft_mvc.model.Reservation;
import com.mycompany.biblio_soft_mvc.model.ReservationStatus;
import com.mycompany.biblio_soft_mvc.model.User;
import java.util.Date;
import java.util.List;

/**
 * Controlador para la gestion de reservas.
 */
public class ReservationController {
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final LoanDAO loanDAO = new LoanDAO();

    public boolean registerReservation(Date reservationDate, User user, Book book) {
        return registerReservationWithError(reservationDate, user, book) == null;
    }

    public String registerReservationWithError(Date reservationDate, User user, Book book) {
        Book storedBook = bookDAO.findById(book.getIdBook());
        if (storedBook == null) {
            return "El libro seleccionado no existe.";
        }
        String currentStatus = storedBook.getStatus();
        if (!BookStatus.PRESTADO.name().equalsIgnoreCase(currentStatus)
                && !BookStatus.RESERVADO.name().equalsIgnoreCase(currentStatus)) {
            return "Solo se pueden reservar libros prestados o reservados.";
        }
        if (reservationDAO.hasActiveReservation(user.getIdUser(), book.getIdBook())) {
            return "El usuario ya tiene una reserva activa para este libro.";
        }

        Reservation reservation = new Reservation();
        reservation.setReservationDate(reservationDate);
        reservation.setStatus(ReservationStatus.ACTIVA.name());
        reservation.setUser(user);
        reservation.setBook(book);
        return reservationDAO.addReservationWithError(reservation);
    }

    public List<Reservation> listActiveReservations() {
        return reservationDAO.getActiveReservations();
    }

    public List<Reservation> getReservationHistory() {
        return reservationDAO.getReservationHistory();
    }

    public List<Reservation> getReservationsByUser(int userId) {
        return reservationDAO.getReservationsByUser(userId);
    }

    public List<Reservation> getReservationsByBook(int bookId) {
        return reservationDAO.getReservationsByBook(bookId);
    }

    public Reservation findReservationById(int idReservation) {
        return reservationDAO.findById(idReservation);
    }

    public boolean cancelReservation(int idReservation) {
        Reservation reservation = reservationDAO.findById(idReservation);
        if (reservation == null || !ReservationStatus.ACTIVA.name().equalsIgnoreCase(reservation.getStatus())) {
            return false;
        }
        boolean updated = reservationDAO.updateReservationStatus(idReservation, ReservationStatus.CANCELADA.name());
        if (updated) {
            syncBookStatus(reservation.getBook().getIdBook());
        }
        return updated;
    }

    public boolean fulfillReservation(int idReservation) {
        Reservation reservation = reservationDAO.findById(idReservation);
        if (reservation == null || !ReservationStatus.ACTIVA.name().equalsIgnoreCase(reservation.getStatus())) {
            return false;
        }
        boolean updated = reservationDAO.updateReservationStatus(idReservation, ReservationStatus.ATENDIDA.name());
        if (updated) {
            syncBookStatus(reservation.getBook().getIdBook());
        }
        return updated;
    }

    public boolean fulfillReservationForLoan(int userId, int bookId) {
        Reservation reservation = reservationDAO.findFirstActiveReservationForUserAndBook(userId, bookId);
        if (reservation == null) {
            return false;
        }
        return reservationDAO.updateReservationStatus(reservation.getIdReservation(), ReservationStatus.ATENDIDA.name());
    }

    public boolean hasActiveReservation(int userId, int bookId) {
        return reservationDAO.hasActiveReservation(userId, bookId);
    }

    public boolean hasActiveReservationsForBook(int bookId) {
        return reservationDAO.hasActiveReservationsForBook(bookId);
    }

    public void syncBookStatus(int bookId) {
        boolean hasActiveLoan = loanDAO.hasActiveLoanForBook(bookId);
        boolean hasActiveReservations = reservationDAO.hasActiveReservationsForBook(bookId);

        if (hasActiveLoan) {
            bookDAO.updateBookStatus(bookId, BookStatus.PRESTADO.name());
        } else if (hasActiveReservations) {
            bookDAO.updateBookStatus(bookId, BookStatus.RESERVADO.name());
        } else {
            bookDAO.updateBookStatus(bookId, BookStatus.DISPONIBLE.name());
        }
    }
}
