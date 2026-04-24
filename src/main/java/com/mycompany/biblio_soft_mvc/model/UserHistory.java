package com.mycompany.biblio_soft_mvc.model;

import java.util.List;

/**
 * Resultado de historial combinado para un usuario.
 */
public class UserHistory {
    private User user;
    private List<Loan> loans;
    private List<Reservation> reservations;

    public UserHistory(User user, List<Loan> loans, List<Reservation> reservations) {
        this.user = user;
        this.loans = loans;
        this.reservations = reservations;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
