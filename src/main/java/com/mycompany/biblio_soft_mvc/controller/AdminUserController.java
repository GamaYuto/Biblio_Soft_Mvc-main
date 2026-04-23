package com.mycompany.biblio_soft_mvc.controller;

import com.mycompany.biblio_soft_mvc.dao.AdminUserDAO;
import com.mycompany.biblio_soft_mvc.model.AdminUser;
import com.mycompany.biblio_soft_mvc.service.AuthService;
import java.util.List;

/**
 * Controlador para la gestion de administradores del sistema.
 */
public class AdminUserController {

    private final AdminUserDAO adminUserDAO = new AdminUserDAO();

    public List<AdminUser> listAdminUsers() {
        return adminUserDAO.findAll();
    }

    public boolean createAdminUser(String username, String fullName, String password) {
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(username);
        adminUser.setFullName(fullName);
        adminUser.setPasswordHash(AuthService.hashPassword(password));
        return adminUserDAO.add(adminUser);
    }

    public boolean updateAdminUser(int idAdmin, String username, String fullName, String password) {
        AdminUser adminUser = new AdminUser();
        adminUser.setIdAdmin(idAdmin);
        adminUser.setUsername(username);
        adminUser.setFullName(fullName);

        boolean updated = adminUserDAO.update(adminUser);
        if (!updated) {
            return false;
        }

        if (password != null && !password.isBlank()) {
            return adminUserDAO.updatePassword(idAdmin, AuthService.hashPassword(password));
        }

        return true;
    }

    public boolean deleteAdminUser(int idAdmin) {
        return adminUserDAO.delete(idAdmin);
    }
}
