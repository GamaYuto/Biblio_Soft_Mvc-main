package com.mycompany.biblio_soft_mvc.model;

import java.util.Date;

/**
 * Representa una reserva activa o historica sobre un libro fisico.
 */
public class Reservation {
    private int idReservation;
    private Date reservationDate;
    private String status;
    private User user;
    private Book book;

    public Reservation() {}

    public Reservation(int idReservation, Date reservationDate, String status, User user, Book book) {
        this.idReservation = idReservation;
        this.reservationDate = reservationDate;
        this.status = status;
        this.user = user;
        this.book = book;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
