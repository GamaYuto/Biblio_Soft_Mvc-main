package com.mycompany.biblio_soft_mvc.model;

/**
 * Resume la demanda historica de un autor en el sistema.
 */
public class AuthorMetric {
    private int idAuthor;
    private String authorName;
    private String nationality;
    private int totalLoans;

    public AuthorMetric() {}

    public AuthorMetric(int idAuthor, String authorName, String nationality, int totalLoans) {
        this.idAuthor = idAuthor;
        this.authorName = authorName;
        this.nationality = nationality;
        this.totalLoans = totalLoans;
    }

    public int getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(int idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public int getTotalLoans() {
        return totalLoans;
    }

    public void setTotalLoans(int totalLoans) {
        this.totalLoans = totalLoans;
    }
}
