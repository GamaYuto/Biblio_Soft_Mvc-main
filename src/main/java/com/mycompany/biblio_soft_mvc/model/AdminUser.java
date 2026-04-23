package com.mycompany.biblio_soft_mvc.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Representa a un usuario administrador autenticable.
 */
public class AdminUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idAdmin;
    private String username;
    private String passwordHash;
    private String fullName;
    private Timestamp createdAt;

    public AdminUser() {
    }

    public AdminUser(int idAdmin, String username, String passwordHash, String fullName, Timestamp createdAt) {
        this.idAdmin = idAdmin;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.createdAt = createdAt;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
