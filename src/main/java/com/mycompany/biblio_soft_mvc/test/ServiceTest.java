package com.mycompany.biblio_soft_mvc.test;

import com.mycompany.biblio_soft_mvc.controller.AuthorController;
import com.mycompany.biblio_soft_mvc.controller.BookController;
import com.mycompany.biblio_soft_mvc.controller.CategoryController;
import com.mycompany.biblio_soft_mvc.controller.LoanController;
import com.mycompany.biblio_soft_mvc.model.Author;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.Category;
import com.mycompany.biblio_soft_mvc.model.Loan;
import com.mycompany.biblio_soft_mvc.model.User;
import java.util.Date;
import java.util.List;

/**
 * Pruebas de los servicios definidos en el punto 15 del leeme.txt.
 */
public class ServiceTest {
    public static void main(String[] args) {
        System.out.println("=== Ejecutando Servicios de Prueba (Punto 15) ===");

        AuthorController authCtrl = new AuthorController();
        BookController bookCtrl = new BookController();
        CategoryController categoryCtrl = new CategoryController();
        LoanController loanCtrl = new LoanController();

        System.out.println("\n1. Registrando Autor...");
        authCtrl.registerAuthor("Isabel Allende", "Chilena");

        System.out.println("\n2. Registrando Libro...");
        Author autor = authCtrl.listAuthors().stream()
                .filter(a -> a.getName().equals("Isabel Allende"))
                .findFirst().orElse(null);
        Category categoria = categoryCtrl.listCategories().stream().findFirst().orElse(null);

        if (autor != null && categoria != null) {
            bookCtrl.registerBook("La casa de los espiritus", "978-0571131464", 1982, autor, categoria);
        }

        System.out.println("\n3. Registrando Prestamo...");
        Book libro = bookCtrl.searchBooks("La casa de los espiritus").get(0);
        User usuario = new User();
        usuario.setIdUser(1);

        Date hoy = new Date();
        Date devolucion = new Date(hoy.getTime() + (1000L * 60 * 60 * 24 * 5));
        loanCtrl.registerLoan(hoy, devolucion, usuario, libro);

        System.out.println("\n4. Listado de Prestamos Activos:");
        List<Loan> prestamos = loanCtrl.listLoans();
        for (Loan l : prestamos) {
            System.out.println("Prestamo ID: " + l.getIdLoan()
                    + " | Libro: " + l.getBook().getTitle()
                    + " | Usuario: " + l.getUser().getName()
                    + " | Fecha Devolucion: " + l.getReturnDate());
        }

        System.out.println("\n=== Pruebas de Servicios Finalizadas ===");
    }
}
