package com.mycompany.biblio_soft_mvc.controller;

import com.mycompany.biblio_soft_mvc.dao.BookDAO;
import com.mycompany.biblio_soft_mvc.dao.LoanDAO;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.BookStatus;
import com.mycompany.biblio_soft_mvc.model.Loan;
import com.mycompany.biblio_soft_mvc.model.User;
import java.util.Date;
import java.util.List;

/**
 * Controlador para la gestion de prestamos.
 */
public class LoanController {
    private final LoanDAO loanDAO = new LoanDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final ReservationController reservationController = new ReservationController();

    public boolean registerLoan(Date loanDate, Date returnDate, User user, Book book) {
        return registerLoanWithError(loanDate, returnDate, user, book) == null;
    }

    public String registerLoanWithError(Date loanDate, Date returnDate, User user, Book book) {
        Book storedBook = bookDAO.findById(book.getIdBook());
        if (storedBook == null) {
            return "El libro seleccionado no existe.";
        }
        boolean hasReservation = reservationController.hasActiveReservation(user.getIdUser(), book.getIdBook());
        boolean canLoanReservedBook = BookStatus.RESERVADO.name().equalsIgnoreCase(storedBook.getStatus()) && hasReservation;
        if (!BookStatus.DISPONIBLE.name().equalsIgnoreCase(storedBook.getStatus()) && !canLoanReservedBook) {
            return "El libro no esta disponible para prestamo. Estado actual: " + storedBook.getStatus() + ".";
        }

        Loan loan = new Loan();
        loan.setLoanDate(loanDate);
        loan.setReturnDate(returnDate);
        loan.setReturned(false);
        loan.setUser(user);
        loan.setBook(book);

        String error = loanDAO.addLoanWithError(loan);
        if (error == null) {
            if (hasReservation) {
                reservationController.fulfillReservationForLoan(user.getIdUser(), book.getIdBook());
            }
            bookDAO.updateBookStatus(book.getIdBook(), BookStatus.PRESTADO.name());
        }
        return error;
    }

    public List<Loan> listLoans() {
        return loanDAO.getAllLoans();
    }

    public List<Loan> getLoanHistory() {
        return loanDAO.getLoanHistory();
    }

    public List<Loan> getLoansByUser(int userId) {
        return loanDAO.getLoansByUser(userId);
    }

    public List<Loan> getLoansByBook(int bookId) {
        return loanDAO.getLoansByBook(bookId);
    }

    public boolean updateLoan(int idLoan, Date loanDate, Date returnDate, User user, Book book) {
        Loan currentLoan = loanDAO.findById(idLoan);
        if (currentLoan == null || currentLoan.isReturned() || currentLoan.getBook() == null) {
            return false;
        }

        int currentBookId = currentLoan.getBook().getIdBook();
        if (currentBookId != book.getIdBook()) {
            Book newBook = bookDAO.findById(book.getIdBook());
            boolean hasReservation = reservationController.hasActiveReservation(user.getIdUser(), book.getIdBook());
            boolean canLoanReservedBook = newBook != null
                    && BookStatus.RESERVADO.name().equalsIgnoreCase(newBook.getStatus())
                    && hasReservation;
            if (newBook == null
                    || (!BookStatus.DISPONIBLE.name().equalsIgnoreCase(newBook.getStatus()) && !canLoanReservedBook)) {
                return false;
            }
        }

        Loan loan = new Loan();
        loan.setIdLoan(idLoan);
        loan.setLoanDate(loanDate);
        loan.setReturnDate(returnDate);
        loan.setUser(user);
        loan.setBook(book);

        boolean updated = loanDAO.updateLoan(loan);
        if (!updated) {
            return false;
        }

        if (currentBookId != book.getIdBook()) {
            reservationController.syncBookStatus(currentBookId);
            if (reservationController.hasActiveReservation(user.getIdUser(), book.getIdBook())) {
                reservationController.fulfillReservationForLoan(user.getIdUser(), book.getIdBook());
            }
            bookDAO.updateBookStatus(book.getIdBook(), BookStatus.PRESTADO.name());
        }
        return true;
    }

    public boolean returnBook(int idLoan) {
        Loan currentLoan = loanDAO.findById(idLoan);
        if (currentLoan == null || currentLoan.isReturned() || currentLoan.getBook() == null) {
            return false;
        }

        boolean returned = loanDAO.returnBook(idLoan);
        if (returned) {
            int bookId = currentLoan.getBook().getIdBook();
            if (reservationController.hasActiveReservationsForBook(bookId)) {
                bookDAO.updateBookStatus(bookId, BookStatus.RESERVADO.name());
            } else {
                bookDAO.updateBookStatus(bookId, BookStatus.DISPONIBLE.name());
            }
        }
        return returned;
    }
}
