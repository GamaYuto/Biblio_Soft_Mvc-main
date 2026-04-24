package com.mycompany.biblio_soft_mvc.controller;

import com.mycompany.biblio_soft_mvc.dao.BookDAO;
import com.mycompany.biblio_soft_mvc.model.Author;
import com.mycompany.biblio_soft_mvc.model.Book;
import com.mycompany.biblio_soft_mvc.model.BookStatus;
import com.mycompany.biblio_soft_mvc.model.Category;
import java.util.List;

/**
 * Controlador para la gestion de libros.
 */
public class BookController {
    private final BookDAO bookDAO = new BookDAO();

    public boolean registerBook(String title, String isbn, int year, Author author, Category category) {
        return registerBook(title, isbn, year, BookStatus.DISPONIBLE.name(), author, category);
    }

    public boolean registerBook(String title, String isbn, int year, String status, Author author, Category category) {
        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setYear(year);
        book.setStatus(BookStatus.normalize(status));
        book.setAuthor(author);
        book.setCategory(category);
        return bookDAO.addBook(book);
    }

    public boolean updateBook(int idBook, String title, String isbn, int year, Author author, Category category) {
        return updateBook(idBook, title, isbn, year, BookStatus.DISPONIBLE.name(), author, category);
    }

    public boolean updateBook(int idBook, String title, String isbn, int year, String status, Author author, Category category) {
        Book book = new Book();
        book.setIdBook(idBook);
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setYear(year);
        book.setStatus(BookStatus.normalize(status));
        book.setAuthor(author);
        book.setCategory(category);
        return bookDAO.updateBook(book);
    }

    public boolean deleteBook(int idBook) {
        return bookDAO.deleteBook(idBook);
    }

    public List<Book> listBooks() {
        return bookDAO.getAllBooks();
    }

    public List<Book> listAvailableBooks() {
        return bookDAO.getAvailableBooks();
    }

    public List<Book> listReservableBooks() {
        return bookDAO.getReservableBooks();
    }

    public List<Book> searchBooks(String title) {
        return bookDAO.searchBooks(title);
    }

    public List<Book> searchBooksByAuthor(String authorName) {
        return bookDAO.searchBooksByAuthor(authorName);
    }

    public List<Book> searchBooksByIsbn(String isbn) {
        return bookDAO.searchBooksByIsbn(isbn);
    }

    public List<Book> searchBooksAdvanced(String title, String authorName, String isbn, Integer categoryId, Integer yearFrom, Integer yearTo) {
        return bookDAO.searchBooksAdvanced(title, authorName, isbn, categoryId, yearFrom, yearTo);
    }

    public Book findBookById(int idBook) {
        return bookDAO.findById(idBook);
    }

    public boolean updateBookStatus(int idBook, String status) {
        return bookDAO.updateBookStatus(idBook, status);
    }
}
