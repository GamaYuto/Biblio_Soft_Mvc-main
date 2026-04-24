package com.mycompany.biblio_soft_mvc.controller;

import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.BookHistory;
import com.mycompany.biblio_soft_mvc.model.Loan;
import com.mycompany.biblio_soft_mvc.model.Reservation;
import com.mycompany.biblio_soft_mvc.model.ReservationStatus;
import com.mycompany.biblio_soft_mvc.model.User;
import com.mycompany.biblio_soft_mvc.model.UserHistory;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Centraliza el armado de historiales combinados para usuarios y libros.
 */
public class HistoryController {
    private final UserController userController = new UserController();
    private final BookController bookController = new BookController();
    private final LoanController loanController = new LoanController();
    private final ReservationController reservationController = new ReservationController();

    public UserHistory getUserHistory(int idUser, Date dateFrom, Date dateTo, String loanStatus, String reservationStatus) {
        User user = userController.findUserById(idUser);
        if (user == null) {
            return null;
        }
        List<Loan> loans = filterLoans(loanController.getLoansByUser(idUser), dateFrom, dateTo, loanStatus);
        List<Reservation> reservations = filterReservations(
                reservationController.getReservationsByUser(idUser),
                dateFrom,
                dateTo,
                reservationStatus
        );
        return new UserHistory(user, loans, reservations);
    }

    public BookHistory getBookHistory(int idBook, Date dateFrom, Date dateTo, String loanStatus, String reservationStatus) {
        Book book = bookController.findBookById(idBook);
        if (book == null) {
            return null;
        }
        List<Loan> loans = filterLoans(loanController.getLoansByBook(idBook), dateFrom, dateTo, loanStatus);
        List<Reservation> reservations = filterReservations(
                reservationController.getReservationsByBook(idBook),
                dateFrom,
                dateTo,
                reservationStatus
        );
        return new BookHistory(book, loans, reservations);
    }

    private List<Loan> filterLoans(List<Loan> loans, Date dateFrom, Date dateTo, String loanStatus) {
        String normalizedStatus = normalize(loanStatus);
        return loans.stream()
                .filter(loan -> isWithinRange(loan.getLoanDate(), dateFrom, dateTo))
                .filter(loan -> matchesLoanStatus(loan, normalizedStatus))
                .collect(Collectors.toList());
    }

    private List<Reservation> filterReservations(
            List<Reservation> reservations,
            Date dateFrom,
            Date dateTo,
            String reservationStatus
    ) {
        String normalizedStatus = normalize(reservationStatus);
        return reservations.stream()
                .filter(reservation -> isWithinRange(reservation.getReservationDate(), dateFrom, dateTo))
                .filter(reservation -> matchesReservationStatus(reservation, normalizedStatus))
                .collect(Collectors.toList());
    }

    private boolean isWithinRange(Date value, Date from, Date to) {
        if (value == null) {
            return false;
        }
        if (from != null && value.before(from)) {
            return false;
        }
        if (to != null && value.after(to)) {
            return false;
        }
        return true;
    }

    private boolean matchesLoanStatus(Loan loan, String status) {
        if (status == null || status.isBlank() || "ALL".equals(status)) {
            return true;
        }
        if ("ACTIVE".equals(status)) {
            return !loan.isReturned();
        }
        if ("RETURNED".equals(status)) {
            return loan.isReturned();
        }
        return true;
    }

    private boolean matchesReservationStatus(Reservation reservation, String status) {
        if (status == null || status.isBlank() || "ALL".equals(status)) {
            return true;
        }
        if (!ReservationStatus.isValid(status)) {
            return true;
        }
        return status.equalsIgnoreCase(reservation.getStatus());
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }
}
