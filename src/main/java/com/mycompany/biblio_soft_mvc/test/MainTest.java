package com.mycompany.biblio_soft_mvc.test;

import com.mycompany.biblio_soft_mvc.controller.AuthorController;
import com.mycompany.biblio_soft_mvc.controller.BookController;
import com.mycompany.biblio_soft_mvc.controller.LoanController;
import com.mycompany.biblio_soft_mvc.controller.UserController;
import com.mycompany.biblio_soft_mvc.model.Author;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.User;
import java.util.Date;

/**
 * Clase temporal para probar la integración del backend.
 */
public class MainTest {
    public static void main(String[] args) {
        System.out.println("=== Iniciando Prueba de Backend ===");

        // 1. Instanciar Controladores
        UserController userCtrl = new UserController();
        AuthorController authCtrl = new AuthorController();
        BookController bookCtrl = new BookController();
        LoanController loanCtrl = new LoanController();

        // 2. Probar registro de Usuario
        System.out.println("\n--- Probando Usuario ---");
        userCtrl.registerUser("Juan Perez", "juan@example.com", "555-1234");

        // 3. Probar registro de Autor
        System.out.println("\n--- Probando Autor ---");
        authCtrl.registerAuthor("Gabriel Garcia Marquez", "Colombiano");

        // 4. Probar registro de Libro (Simulamos que el autor ya tiene ID 1)
        System.out.println("\n--- Probando Libro ---");
        Author author = new Author();
        author.setIdAuthor(1); 
        bookCtrl.registerBook("Cien años de soledad", "978-0307474728", 1967, author);

        // 5. Probar registro de Préstamo (Simulamos Usuario ID 1 y Libro ID 1)
        System.out.println("\n--- Probando Préstamo ---");
        User user = new User();
        user.setIdUser(1);
        Book book = new Book();
        book.setIdBook(1);
        
        Date hoy = new Date();
        Date entrega = new Date(hoy.getTime() + (1000 * 60 * 60 * 24 * 7)); // +7 días
        
        loanCtrl.registerLoan(hoy, entrega, user, book);

        System.out.println("\n=== Prueba Finalizada ===");
    }
}
