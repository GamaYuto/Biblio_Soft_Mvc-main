package com.mycompany.biblio_soft_mvc.controller;

import com.mycompany.biblio_soft_mvc.dao.AuthorDAO;
import com.mycompany.biblio_soft_mvc.model.Author;
import java.util.List;

/**
 * Controlador para la gestión de autores.
 * Orquesta las operaciones entre la vista y el DAO de autores.
 */
public class AuthorController {
    private AuthorDAO authorDAO = new AuthorDAO();

    /**
     * Registra un nuevo autor en la aplicación.
     * @param name Nombre del autor.
     * @param nationality Nacionalidad del autor.
     * @return true si el autor se agregó correctamente.
     */
    public boolean registerAuthor(String name, String nationality) {
        Author author = new Author();
        author.setName(name);
        author.setNationality(nationality);
        return authorDAO.addAuthor(author);
    }

    /**
     * Actualiza los datos de un autor existente.
     * @param idAuthor Identificador del autor.
     * @param name Nombre actualizado.
     * @param nationality Nacionalidad actualizada.
     * @return true si la actualización fue exitosa.
     */
    public boolean updateAuthor(int idAuthor, String name, String nationality) {
        Author author = new Author();
        author.setIdAuthor(idAuthor);
        author.setName(name);
        author.setNationality(nationality);
        return authorDAO.updateAuthor(author);
    }

    /**
     * Elimina un autor por su ID.
     * @param idAuthor Identificador del autor.
     * @return true si la eliminación fue exitosa.
     */
    public boolean deleteAuthor(int idAuthor) {
        return authorDAO.deleteAuthor(idAuthor);
    }

    /**
     * Obtiene la lista completa de autores.
     * @return Lista de autores.
     */
    public List<Author> listAuthors() {
        return authorDAO.getAllAuthors();
    }
}
