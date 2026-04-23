package com.mycompany.biblio_soft_mvc.model;

/**
 * Representa a un usuario de la biblioteca.
 * Contiene los datos de contacto necesarios para los préstamos.
 */
public class User {
    private int idUser;
    private String name;
    private String email;
    private String phone;

    /**
     * Constructor vacío para facilitar la serialización y creación de instancias.
     */
    public User() {}

    /**
     * Crea un usuario con todos sus datos.
     * @param idUser Identificador único del usuario.
     * @param name Nombre completo del usuario.
     * @param email Correo electrónico del usuario.
     * @param phone Teléfono de contacto del usuario.
     */
    public User(int idUser, String name, String email, String phone) {
        this.idUser = idUser;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    /**
     * Obtiene el identificador del usuario.
     * @return idUser
     */
    public int getIdUser() { return idUser; }

    /**
     * Establece el identificador del usuario.
     * @param idUser Identificador del usuario.
     */
    public void setIdUser(int idUser) { this.idUser = idUser; }

    /**
     * Obtiene el nombre del usuario.
     * @return name
     */
    public String getName() { return name; }

    /**
     * Establece el nombre del usuario.
     * @param name Nombre del usuario.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Obtiene el correo electrónico del usuario.
     * @return email
     */
    public String getEmail() { return email; }

    /**
     * Establece el correo electrónico del usuario.
     * @param email Correo electrónico del usuario.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Obtiene el teléfono de contacto del usuario.
     * @return phone
     */
    public String getPhone() { return phone; }

    /**
     * Establece el teléfono del usuario.
     * @param phone Teléfono de contacto.
     */
    public void setPhone(String phone) { this.phone = phone; }
}
