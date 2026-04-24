package com.mycompany.biblio_soft_mvc.model;

/**
 * Representa la actividad reciente de un usuario en prestamos y reservas.
 */
public class UserActivityMetric {
    private int idUser;
    private String userName;
    private String email;
    private int recentLoans;
    private int recentReservations;
    private String lastActivityDate;

    public UserActivityMetric() {}

    public UserActivityMetric(int idUser, String userName, String email, int recentLoans, int recentReservations, String lastActivityDate) {
        this.idUser = idUser;
        this.userName = userName;
        this.email = email;
        this.recentLoans = recentLoans;
        this.recentReservations = recentReservations;
        this.lastActivityDate = lastActivityDate;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRecentLoans() {
        return recentLoans;
    }

    public void setRecentLoans(int recentLoans) {
        this.recentLoans = recentLoans;
    }

    public int getRecentReservations() {
        return recentReservations;
    }

    public void setRecentReservations(int recentReservations) {
        this.recentReservations = recentReservations;
    }

    public String getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(String lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
}
