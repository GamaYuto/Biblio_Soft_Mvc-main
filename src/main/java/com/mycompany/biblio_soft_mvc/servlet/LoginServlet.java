package com.mycompany.biblio_soft_mvc.servlet;

import com.google.gson.Gson;
import com.mycompany.biblio_soft_mvc.model.AdminUser;
import com.mycompany.biblio_soft_mvc.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Controla el inicio de sesion del panel administrativo.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/resources/login", "/login"})
public class LoginServlet extends HttpServlet {

    public static final String SESSION_ADMIN_USER = "adminUser";

    private final Gson gson = new Gson();
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession(false);
        AdminUser adminUser = session != null ? (AdminUser) session.getAttribute(SESSION_ADMIN_USER) : null;

        if (adminUser != null) {
            resp.getWriter().write(gson.toJson(Map.of(
                    "loggedIn", true,
                    "username", adminUser.getUsername(),
                    "fullName", adminUser.getFullName() != null ? adminUser.getFullName() : "",
                    "idAdmin", adminUser.getIdAdmin()
            )));
            return;
        }

        resp.getWriter().write(gson.toJson(Map.of("loggedIn", false)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (isBlank(username) || isBlank(password)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of(
                    "success", false,
                    "error", "Debes ingresar usuario y contrasena."
            )));
            return;
        }

        AdminUser adminUser = authService.getAuthenticatedUser(username, password);
        if (adminUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(Map.of(
                    "success", false,
                    "error", "Credenciales invalidas."
            )));
            return;
        }

        HttpSession currentSession = req.getSession(false);
        if (currentSession != null) {
            currentSession.invalidate();
        }

        HttpSession session = req.getSession(true);
        session.setAttribute(SESSION_ADMIN_USER, adminUser);
        session.setMaxInactiveInterval(30 * 60);

        resp.getWriter().write(gson.toJson(Map.of(
                "success", true,
                "username", adminUser.getUsername(),
                "fullName", adminUser.getFullName() != null ? adminUser.getFullName() : "",
                "redirect", req.getContextPath() + "/index.jsp"
        )));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
