package com.mycompany.biblio_soft_mvc.model;

/**
 * Representa un libro en el inventario de la biblioteca.
 * Incluye datos de autor y metadatos de publicación.
 */
public class Book {
    private int idBook;
    private String title;
    private String isbn;
    private int year;
    private Author author;

    /**
     * Constructor vacío para facilitar la serialización y creación de instancias.
     */
    public Book() {}

    /**
     * Crea un libro con todos sus atributos.
     * @param idBook Identificador único del libro.
     * @param title Título del libro.
     * @param isbn Código ISBN del libro.
     * @param year Año de publicación.
     * @param author Autor asociado al libro.
     */
    public Book(int idBook, String title, String isbn, int year, Author author) {
        this.idBook = idBook;
        this.title = title;
        this.isbn = isbn;
        this.year = year;
        this.author = author;
    }

    /**
     * Obtiene el identificador del libro.
     * @return idBook
     */
    public int getIdBook() { return idBook; }

    /**
     * Establece el identificador del libro.
     * @param idBook Identificador del libro.
     */
    public void setIdBook(int idBook) { this.idBook = idBook; }

    /**
     * Obtiene el título del libro.
     * @return title
     */
    public String getTitle() { return title; }

    /**
     * Establece el título del libro.
     * @param title Título del libro.
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Obtiene el ISBN del libro.
     * @return isbn
     */
    public String getIsbn() { return isbn; }

    /**
     * Establece el ISBN del libro.
     * @param isbn ISBN del libro.
     */
    public void setIsbn(String isbn) { this.isbn = isbn; }

    /**
     * Obtiene el año de publicación.
     * @return year
     */
    public int getYear() { return year; }

    /**
     * Establece el año de publicación.
     * @param year Año del libro.
     */
    public void setYear(int year) { this.year = year; }

    /**
     * Obtiene el autor asociado al libro.
     * @return author
     */
    public Author getAuthor() { return author; }

    /**
     * Establece el autor asociado al libro.
     * @param author Autor del libro.
     */
    public void setAuthor(Author author) { this.author = author; }
}
