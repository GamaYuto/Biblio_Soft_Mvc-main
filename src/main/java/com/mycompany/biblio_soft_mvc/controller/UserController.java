package com.mycompany.biblio_soft_mvc.controller;

import com.mycompany.biblio_soft_mvc.dao.UserDAO;
import com.mycompany.biblio_soft_mvc.model.User;
import java.util.List;

/**
 * Controlador para la gestión de usuarios.
 * Realiza la coordinación entre la vista y el DAO de usuarios.
 */
public class UserController {
    private UserDAO userDAO = new UserDAO();

    /**
     * Registra un nuevo usuario.
     * @param name Nombre completo del usuario.
     * @param email Correo electrónico del usuario.
     * @param phone Teléfono de contacto del usuario.
     * @return true si el usuario fue agregado.
     */
    public boolean registerUser(String name, String email, String phone) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        return userDAO.addUser(user);
    }

    /**
     * Obtiene la lista de todos los usuarios.
     * @return Lista de usuarios.
     */
    public List<User> listUsers() {
        return userDAO.getAllUsers();
    }

    /**
     * Actualiza la información de un usuario.
     * @param idUser Identificador del usuario.
     * @param name Nombre actualizado.
     * @param email Email actualizado.
     * @param phone Teléfono actualizado.
     * @return true si la actualización fue exitosa.
     */
    public boolean updateUser(int idUser, String name, String email, String phone) {
        User user = new User();
        user.setIdUser(idUser);
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        return userDAO.updateUser(user);
    }

    /**
     * Elimina un usuario por su ID.
     * @param idUser Identificador del usuario.
     * @return true si el usuario se eliminó.
     */
    public boolean deleteUser(int idUser) {
        return userDAO.deleteUser(idUser);
    }
}
