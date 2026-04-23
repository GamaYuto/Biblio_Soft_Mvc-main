package com.mycompany.biblio_soft_mvc.controller;

import com.mycompany.biblio_soft_mvc.dao.BookDAO;
import com.mycompany.biblio_soft_mvc.model.Author;
import com.mycompany.biblio_soft_mvc.model.Book;
import java.util.List;

/**
 * Controlador para la gestión de libros.
 * Actúa como intermediario entre la interfaz y el DAO de libros.
 */
public class BookController {
    private BookDAO bookDAO = new BookDAO();

    /**
     * Registra un nuevo libro.
     * @param title Título del libro.
     * @param isbn ISBN del libro.
     * @param year Año de publicación.
     * @param author Autor del libro.
     * @return true si el libro se creó correctamente.
     */
    public boolean registerBook(String title, String isbn, int year, Author author) {
        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setYear(year);
        book.setAuthor(author);
        return bookDAO.addBook(book);
    }

    /**
     * Actualiza un libro existente.
     * @param idBook Identificador del libro.
     * @param title Título actualizado.
     * @param isbn ISBN actualizado.
     * @param year Año actualizado.
     * @param author Autor actualizado.
     * @return true si la actualización fue exitosa.
     */
    public boolean updateBook(int idBook, String title, String isbn, int year, Author author) {
        Book book = new Book();
        book.setIdBook(idBook);
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setYear(year);
        book.setAuthor(author);
        return bookDAO.updateBook(book);
    }

    /**
     * Elimina un libro por su ID.
     * @param idBook Identificador del libro.
     * @return true si el libro se eliminó.
     */
    public boolean deleteBook(int idBook) {
        return bookDAO.deleteBook(idBook);
    }

    /**
     * Devuelve la lista completa de libros.
     * @return Lista de libros.
     */
    public List<Book> listBooks() {
        return bookDAO.getAllBooks();
    }

    /**
     * Busca libros por título.
     * @param title Texto a buscar en el título.
     * @return Lista de libros que coinciden.
     */
    public List<Book> searchBooks(String title) {
        return bookDAO.searchBooks(title);
    }

    /**
     * Busca libros por autor.
     * @param authorName Nombre del autor.
     * @return Lista de libros del autor.
     */
    public List<Book> searchBooksByAuthor(String authorName) {
        return bookDAO.searchBooksByAuthor(authorName);
    }

    /**
     * Busca libros por ISBN.
     * @param isbn Texto a buscar en el ISBN.
     * @return Lista de libros que coinciden con el ISBN.
     */
    public List<Book> searchBooksByIsbn(String isbn) {
        return bookDAO.searchBooksByIsbn(isbn);
    }
}
