package com.mycompany.biblio_soft_mvc.test;

import com.mycompany.biblio_soft_mvc.controller.AuthorController;
import com.mycompany.biblio_soft_mvc.controller.BookController;
import com.mycompany.biblio_soft_mvc.controller.LoanController;
import com.mycompany.biblio_soft_mvc.model.Author;
import com.mycompany.biblio_soft_mvc.model.Book;
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
        LoanController loanCtrl = new LoanController();

        // 1. Registrar autor
        System.out.println("\n1. Registrando Autor...");
        authCtrl.registerAuthor("Isabel Allende", "Chilena");

        // 2. Registrar libro (usando el autor registrado)
        System.out.println("\n2. Registrando Libro...");
        Author autor = authCtrl.listAuthors().stream()
                .filter(a -> a.getName().equals("Isabel Allende"))
                .findFirst().orElse(null);
        
        if (autor != null) {
            bookCtrl.registerBook("La casa de los espíritus", "978-0571131464", 1982, autor);
        }

        // 3. Registrar préstamo (usando el libro registrado y el usuario ID 1 existente)
        System.out.println("\n3. Registrando Préstamo...");
        Book libro = bookCtrl.searchBooks("La casa de los espíritus").get(0);
        User usuario = new User();
        usuario.setIdUser(1); // Usamos el usuario creado en la prueba anterior

        Date hoy = new Date();
        Date devolucion = new Date(hoy.getTime() + (1000L * 60 * 60 * 24 * 5)); // 5 días después
        loanCtrl.registerLoan(hoy, devolucion, usuario, libro);

        // 4. Listar préstamos activos
        System.out.println("\n4. Listado de Préstamos Activos:");
        List<Loan> prestamos = loanCtrl.listLoans();
        for (Loan l : prestamos) {
            System.out.println("Préstamo ID: " + l.getIdLoan() + 
                               " | Libro: " + l.getBook().getTitle() + 
                               " | Usuario: " + l.getUser().getName() + 
                               " | Fecha Devolución: " + l.getReturnDate());
        }

        System.out.println("\n=== Pruebas de Servicios Finalizadas ===");
    }
}
