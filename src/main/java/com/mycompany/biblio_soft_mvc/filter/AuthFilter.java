package com.mycompany.biblio_soft_mvc.filter;

import com.google.gson.Gson;
import com.mycompany.biblio_soft_mvc.servlet.LoginServlet;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Protege rutas administrativas mediante sesion.
 */
public class AuthFilter implements Filter {

    private final Gson gson = new Gson();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String requestUri = req.getRequestURI();
        String path = requestUri.substring(contextPath.length());

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        boolean authenticated = session != null && session.getAttribute(LoginServlet.SESSION_ADMIN_USER) != null;
        if (authenticated) {
            chain.doFilter(request, response);
            return;
        }

        if (isAjaxRequest(req) || path.startsWith("/resources/")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(Map.of(
                    "loggedIn", false,
                    "error", "Sesion expirada o no autenticada.",
                    "redirect", contextPath + "/view/login.jsp"
            )));
            return;
        }

        resp.sendRedirect(contextPath + "/view/login.jsp");
    }

    private boolean isPublicPath(String path) {
        return "/login".equals(path)
                || "/resources/login".equals(path)
                || "/logout".equals(path)
                || "/view/login.jsp".equals(path)
                || "/favicon.ico".equals(path)
                || path.startsWith("/jakarta.faces.resource/")
                || path.startsWith("/assets/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/img/")
                || path.startsWith("/images/")
                || path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|woff|woff2|ttf)$");
    }

    private boolean isAjaxRequest(HttpServletRequest req) {
        String requestedWith = req.getHeader("X-Requested-With");
        String accept = req.getHeader("Accept");
        return "XMLHttpRequest".equalsIgnoreCase(requestedWith)
                || (accept != null && accept.contains("application/json"));
    }
}
