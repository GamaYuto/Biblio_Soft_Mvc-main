package com.mycompany.biblio_soft_mvc.test;

import com.mycompany.biblio_soft_mvc.controller.AuthorController;
import com.mycompany.biblio_soft_mvc.controller.BookController;
import com.mycompany.biblio_soft_mvc.controller.CategoryController;
import com.mycompany.biblio_soft_mvc.controller.LoanController;
import com.mycompany.biblio_soft_mvc.controller.UserController;
import com.mycompany.biblio_soft_mvc.model.Author;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.Category;
import com.mycompany.biblio_soft_mvc.model.User;
import java.util.Date;

/**
 * Clase temporal para probar la integracion del backend.
 */
public class MainTest {
    public static void main(String[] args) {
        System.out.println("=== Iniciando Prueba de Backend ===");

        UserController userCtrl = new UserController();
        AuthorController authCtrl = new AuthorController();
        BookController bookCtrl = new BookController();
        CategoryController categoryCtrl = new CategoryController();
        LoanController loanCtrl = new LoanController();

        System.out.println("\n--- Probando Usuario ---");
        userCtrl.registerUser("Juan Perez", "juan@example.com", "555-1234");

        System.out.println("\n--- Probando Autor ---");
        authCtrl.registerAuthor("Gabriel Garcia Marquez", "Colombiano");

        System.out.println("\n--- Probando Libro ---");
        Author author = new Author();
        author.setIdAuthor(1);
        Category category = categoryCtrl.listCategories().stream().findFirst().orElse(null);
        if (category != null) {
            bookCtrl.registerBook("Cien anos de soledad", "978-0307474728", 1967, author, category);
        }

        System.out.println("\n--- Probando Prestamo ---");
        User user = new User();
        user.setIdUser(1);
        Book book = new Book();
        book.setIdBook(1);

        Date hoy = new Date();
        Date entrega = new Date(hoy.getTime() + (1000L * 60 * 60 * 24 * 7));

        loanCtrl.registerLoan(hoy, entrega, user, book);

        System.out.println("\n=== Prueba Finalizada ===");
    }
}
