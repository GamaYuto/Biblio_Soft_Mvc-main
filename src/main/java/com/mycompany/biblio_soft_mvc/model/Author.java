package com.mycompany.biblio_soft_mvc.model;

/**
 * Representa al autor de uno o varios libros.
 * Contiene información básica de identidad y país.
 */
public class Author {
    private int idAuthor;
    private String name;
    private String nationality;

    /**
     * Constructor vacío para facilitar la serialización y creación de instancias.
     */
    public Author() {}

    /**
     * Crea un autor con todos sus datos.
     * @param idAuthor Identificador único del autor.
     * @param name Nombre del autor.
     * @param nationality Nacionalidad del autor.
     */
    public Author(int idAuthor, String name, String nationality) {
        this.idAuthor = idAuthor;
        this.name = name;
        this.nationality = nationality;
    }

    /**
     * Obtiene el identificador del autor.
     * @return idAuthor
     */
    public int getIdAuthor() { return idAuthor; }

    /**
     * Establece el identificador del autor.
     * @param idAuthor Identificador del autor.
     */
    public void setIdAuthor(int idAuthor) { this.idAuthor = idAuthor; }

    /**
     * Obtiene el nombre del autor.
     * @return name
     */
    public String getName() { return name; }

    /**
     * Establece el nombre del autor.
     * @param name Nombre del autor.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Obtiene la nacionalidad del autor.
     * @return nationality
     */
    public String getNationality() { return nationality; }

    /**
     * Establece la nacionalidad del autor.
     * @param nationality Nacionalidad del autor.
     */
    public void setNationality(String nationality) { this.nationality = nationality; }
}
