package com.mycompany.biblio_soft_mvc.service;

import com.mycompany.biblio_soft_mvc.dao.AdminUserDAO;
import com.mycompany.biblio_soft_mvc.model.AdminUser;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Servicio de autenticacion para administradores.
 */
public class AuthService {

    private final AdminUserDAO adminUserDAO = new AdminUserDAO();

    public boolean authenticate(String username, String password) {
        return getAuthenticatedUser(username, password) != null;
    }

    public AdminUser getAuthenticatedUser(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        AdminUser adminUser = adminUserDAO.findByUsername(username.trim());
        if (adminUser == null || adminUser.getPasswordHash() == null || adminUser.getPasswordHash().isBlank()) {
            return null;
        }

        boolean matches = BCrypt.checkpw(password, adminUser.getPasswordHash());
        if (!matches) {
            return null;
        }

        AdminUser safeUser = new AdminUser();
        safeUser.setIdAdmin(adminUser.getIdAdmin());
        safeUser.setUsername(adminUser.getUsername());
        safeUser.setFullName(adminUser.getFullName());
        safeUser.setCreatedAt(adminUser.getCreatedAt());
        return safeUser;
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }
}
