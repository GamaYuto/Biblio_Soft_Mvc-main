package com.mycompany.biblio_soft_mvc.model;

import java.util.Date;

/**
 * Registra el préstamo de un libro a un usuario.
 * Incluye fechas de préstamo, devolución y el estado del préstamo.
 */
public class Loan {
    private int idLoan;
    private Date loanDate;
    private Date returnDate;
    private boolean returned;
    private Date actualReturnDate;
    private User user;
    private Book book;

    /**
     * Constructor vacío para facilitar la serialización y creación de instancias.
     */
    public Loan() {}

    /**
     * Crea un préstamo con todos los datos necesarios.
     * @param idLoan Identificador del préstamo.
     * @param loanDate Fecha en que se entregó el libro.
     * @param returnDate Fecha estimada de devolución.
     * @param returned Indica si el libro ya fue devuelto.
     * @param actualReturnDate Fecha real de devolución cuando el libro se entrega.
     * @param user Usuario que solicita el préstamo.
     * @param book Libro prestado.
     */
    public Loan(int idLoan, Date loanDate, Date returnDate, boolean returned, Date actualReturnDate, User user, Book book) {
        this.idLoan = idLoan;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.returned = returned;
        this.actualReturnDate = actualReturnDate;
        this.user = user;
        this.book = book;
    }

    /**
     * Obtiene el identificador del préstamo.
     * @return idLoan
     */
    public int getIdLoan() { return idLoan; }

    /**
     * Establece el identificador del préstamo.
     * @param idLoan Identificador del préstamo.
     */
    public void setIdLoan(int idLoan) { this.idLoan = idLoan; }

    /**
     * Obtiene la fecha de préstamo.
     * @return loanDate
     */
    public Date getLoanDate() { return loanDate; }

    /**
     * Establece la fecha de préstamo.
     * @param loanDate Fecha en que se realiza el préstamo.
     */
    public void setLoanDate(Date loanDate) { this.loanDate = loanDate; }

    /**
     * Obtiene la fecha estimada de devolución.
     * @return returnDate
     */
    public Date getReturnDate() { return returnDate; }

    /**
     * Establece la fecha estimada de devolución.
     * @param returnDate Fecha estimada de devolución.
     */
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    /**
     * Indica si el libro ya fue devuelto.
     * @return returned
     */
    public boolean isReturned() { return returned; }

    /**
     * Marca si el libro fue devuelto o no.
     * @param returned true si el libro ya fue devuelto.
     */
    public void setReturned(boolean returned) { this.returned = returned; }

    /**
     * Obtiene la fecha real de devolución.
     * @return actualReturnDate
     */
    public Date getActualReturnDate() { return actualReturnDate; }

    /**
     * Establece la fecha real en la que se devolvió el libro.
     * @param actualReturnDate Fecha real de devolución.
     */
    public void setActualReturnDate(Date actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    /**
     * Obtiene el usuario que realizó el préstamo.
     * @return user
     */
    public User getUser() { return user; }

    /**
     * Establece el usuario que solicita el préstamo.
     * @param user Usuario asociado al préstamo.
     */
    public void setUser(User user) { this.user = user; }

    /**
     * Obtiene el libro prestado.
     * @return book
     */
    public Book getBook() { return book; }

    /**
     * Establece el libro que se presta.
     * @param book Libro asociado al préstamo.
     */
    public void setBook(Book book) { this.book = book; }
}
