package com.mycompany.biblio_soft_mvc.model;

import java.util.List;

/**
 * Resultado de historial combinado para un libro.
 */
public class BookHistory {
    private Book book;
    private List<Loan> loans;
    private List<Reservation> reservations;

    public BookHistory(Book book, List<Loan> loans, List<Reservation> reservations) {
        this.book = book;
        this.loans = loans;
        this.reservations = reservations;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
