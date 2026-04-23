package com.mycompany.biblio_soft_mvc.controller;

import com.mycompany.biblio_soft_mvc.dao.LoanDAO;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.Loan;
import com.mycompany.biblio_soft_mvc.model.User;
import java.util.Date;
import java.util.List;

/**
 * Controlador para la gestión de préstamos.
 * Coordina la lógica entre la capa de presentación y el DAO de préstamos.
 */
public class LoanController {
    private LoanDAO loanDAO = new LoanDAO();

    /**
     * Registra un nuevo préstamo.
     * @param loanDate Fecha de préstamo.
     * @param returnDate Fecha estimada de devolución.
     * @param user Usuario que solicita el préstamo.
     * @param book Libro que se presta.
     * @return true si se registró correctamente.
     */
    public boolean registerLoan(Date loanDate, Date returnDate, User user, Book book) {
        Loan loan = new Loan();
        loan.setLoanDate(loanDate);
        loan.setReturnDate(returnDate);
        loan.setReturned(false);
        loan.setUser(user);
        loan.setBook(book);
        return loanDAO.addLoan(loan);
    }

    /**
     * Registra un préstamo y devuelve detalles de error si falla.
     * @param loanDate Fecha de préstamo.
     * @param returnDate Fecha estimada de devolución.
     * @param user Usuario que solicita el préstamo.
     * @param book Libro que se presta.
     * @return null si tuvo éxito, o el mensaje de error.
     */
    public String registerLoanWithError(Date loanDate, Date returnDate, User user, Book book) {
        Loan loan = new Loan();
        loan.setLoanDate(loanDate);
        loan.setReturnDate(returnDate);
        loan.setReturned(false);
        loan.setUser(user);
        loan.setBook(book);
        return loanDAO.addLoanWithError(loan);
    }

    /**
     * Lista los préstamos activos.
     * @return Lista de préstamos no devueltos.
     */
    public List<Loan> listLoans() {
        return loanDAO.getAllLoans();
    }

    /**
     * Obtiene el historial completo de préstamos.
     * @return Lista de todos los préstamos.
     */
    public List<Loan> getLoanHistory() {
        return loanDAO.getLoanHistory();
    }

    /**
     * Obtiene los préstamos de un usuario específico.
     * @param userId Identificador del usuario.
     * @return Lista de préstamos del usuario.
     */
    public List<Loan> getLoansByUser(int userId) {
        return loanDAO.getLoansByUser(userId);
    }

    /**
     * Obtiene los préstamos asociados a un libro.
     * @param bookId Identificador del libro.
     * @return Lista de préstamos del libro.
     */
    public List<Loan> getLoansByBook(int bookId) {
        return loanDAO.getLoansByBook(bookId);
    }

    /**
     * Actualiza datos de un préstamo existente.
     * @param idLoan Identificador del préstamo.
     * @param loanDate Fecha de préstamo.
     * @param returnDate Fecha estimada de devolución.
     * @param user Usuario asociado.
     * @param book Libro asociado.
     * @return true si la actualización fue exitosa.
     */
    public boolean updateLoan(int idLoan, java.util.Date loanDate, java.util.Date returnDate, User user, Book book) {
        Loan loan = new Loan();
        loan.setIdLoan(idLoan);
        loan.setLoanDate(loanDate);
        loan.setReturnDate(returnDate);
        loan.setUser(user);
        loan.setBook(book);
        return loanDAO.updateLoan(loan);
    }

    /**
     * Marca un préstamo como devuelto.
     * @param idLoan Identificador del préstamo.
     * @return true si el cambio de estado se guardó correctamente.
     */
    public boolean returnBook(int idLoan) {
        return loanDAO.returnBook(idLoan);
    }
}
