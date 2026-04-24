package com.mycompany.biblio_soft_mvc.model;

/**
 * Representa un libro en el inventario de la biblioteca.
 * Incluye datos de autor, estado operativo y metadatos de publicacion.
 */
public class Book {
    private int idBook;
    private String title;
    private String isbn;
    private int year;
    private String status;
    private Author author;
    private Category category;

    /**
     * Constructor vacio para facilitar la serializacion y creacion de instancias.
     */
    public Book() {}

    /**
     * Crea un libro con todos sus atributos.
     * @param idBook Identificador unico del libro.
     * @param title Titulo del libro.
     * @param isbn Codigo ISBN del libro.
     * @param year Anio de publicacion.
     * @param status Estado operativo del libro.
     * @param author Autor asociado al libro.
     * @param category Categoria asociada al libro.
     */
    public Book(int idBook, String title, String isbn, int year, String status, Author author, Category category) {
        this.idBook = idBook;
        this.title = title;
        this.isbn = isbn;
        this.year = year;
        this.status = status;
        this.author = author;
        this.category = category;
    }

    public int getIdBook() { return idBook; }

    public void setIdBook(int idBook) { this.idBook = idBook; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }

    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getYear() { return year; }

    public void setYear(int year) { this.year = year; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Author getAuthor() { return author; }

    public void setAuthor(Author author) { this.author = author; }

    public Category getCategory() { return category; }

    public void setCategory(Category category) { this.category = category; }
}
