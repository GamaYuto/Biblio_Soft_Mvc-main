<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mycompany.biblio_soft_mvc.model.AdminUser" %>
<%@ page import="com.mycompany.biblio_soft_mvc.servlet.LoginServlet" %>
<%
    AdminUser adminUser = (AdminUser) session.getAttribute(LoginServlet.SESSION_ADMIN_USER);
    String displayName = adminUser != null && adminUser.getFullName() != null && !adminUser.getFullName().isBlank()
            ? adminUser.getFullName()
            : adminUser != null ? adminUser.getUsername() : "Invitado";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BiblioSoft MVC</title>
    <link rel="icon" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><text y='.9em' font-size='90'>📚</text></svg>">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">
    <style>
        html, body { height: 100%; }
        body { min-height: 100vh; background-color: #f8f9fa; margin: 0; }
        .app-layout { min-height: 100vh; height: 100vh; overflow: hidden; }
        .app-layout > .flex-grow-1 { display: flex; flex-direction: column; min-height: 0; overflow: hidden; }
        .sidebar { width: 240px; position: sticky; top: 0; height: 100vh; background: #1f2937; color: white; flex-shrink: 0; }
        .sidebar a { color: rgba(255,255,255,.8); text-decoration: none; }
        .sidebar a.active, .sidebar a:hover { color: white; background-color: rgba(255,255,255,.08); border-radius: .375rem; }
        .sidebar .nav-link { padding: .75rem 1.25rem; }
        .sidebar .logout-link, .offcanvas-sidebar .logout-link { color: #fca5a5; }
        .sidebar .logout-link:hover, .offcanvas-sidebar .logout-link:hover { color: #fff; background-color: rgba(239,68,68,.18); }
        .offcanvas-sidebar { background: #1f2937 !important; }
        .offcanvas-sidebar .nav-link { color: rgba(255,255,255,.8); padding: .75rem 1.25rem; }
        .offcanvas-sidebar .nav-link:hover { color: white; background-color: rgba(255,255,255,.08); border-radius: .375rem; }
        .content-wrapper { padding: 16px; flex: 1 1 auto; min-height: 0; overflow-y: auto; }
        @media (min-width: 768px) { .content-wrapper { padding: 24px; } }
        .card-header { background: #fff; }
    </style>
</head>
<body>

<div class="offcanvas offcanvas-start offcanvas-sidebar" tabindex="-1" id="mobileSidebar" aria-labelledby="mobileSidebarLabel">
    <div class="offcanvas-header border-bottom border-secondary">
        <span class="fs-5 fw-semibold text-white" id="mobileSidebarLabel">
            <i class="fa-solid fa-book-open-reader me-2"></i>BiblioSoft
        </span>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas" aria-label="Cerrar"></button>
    </div>
    <div class="offcanvas-body p-3 d-flex flex-column">
        <ul class="nav nav-pills flex-column mb-auto">
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/index.jsp" class="nav-link"><i class="fa-solid fa-house me-2"></i>Inicio</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/authors.jsp" class="nav-link"><i class="fa-solid fa-user-tie me-2"></i>Autores</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/books.jsp" class="nav-link"><i class="fa-solid fa-book me-2"></i>Libros</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/users.jsp" class="nav-link"><i class="fa-solid fa-users me-2"></i>Usuarios</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/admin-users.jsp" class="nav-link"><i class="fa-solid fa-user-shield me-2"></i>Admins</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/loans.jsp" class="nav-link"><i class="fa-solid fa-scroll me-2"></i>Prestamos</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/reservations.jsp" class="nav-link"><i class="fa-solid fa-bookmark me-2"></i>Reservas</a>
            </li>
        </ul>
        <div class="pt-3 border-top border-secondary mt-3">
            <div class="small text-white-50 px-3 mb-2"><i class="fa-solid fa-user-shield me-2"></i><%= displayName %></div>
            <a href="<%= request.getContextPath() %>/logout" class="nav-link logout-link"><i class="fa-solid fa-right-from-bracket me-2"></i>Cerrar sesion</a>
        </div>
    </div>
</div>

<div class="d-flex app-layout">
    <nav class="sidebar p-3 d-none d-md-flex flex-column">
        <a href="<%= request.getContextPath() %>/index.jsp" class="d-flex align-items-center mb-3 text-white text-decoration-none">
            <span class="fs-5 fw-semibold"><i class="fa-solid fa-book-open-reader me-2"></i>BiblioSoft</span>
        </a>
        <hr class="border-secondary">
        <ul class="nav nav-pills flex-column mb-auto">
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/index.jsp" class="nav-link text-white"><i class="fa-solid fa-house me-2"></i>Inicio</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/authors.jsp" class="nav-link text-white"><i class="fa-solid fa-user-tie me-2"></i>Autores</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/books.jsp" class="nav-link text-white"><i class="fa-solid fa-book me-2"></i>Libros</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/users.jsp" class="nav-link text-white"><i class="fa-solid fa-users me-2"></i>Usuarios</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/admin-users.jsp" class="nav-link text-white"><i class="fa-solid fa-user-shield me-2"></i>Admins</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/loans.jsp" class="nav-link text-white"><i class="fa-solid fa-scroll me-2"></i>Prestamos</a>
            </li>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/view/reservations.jsp" class="nav-link text-white"><i class="fa-solid fa-bookmark me-2"></i>Reservas</a>
            </li>
        </ul>
        <hr class="border-secondary">
        <div class="mt-auto">
            <div class="small text-white-50 px-3 mb-2"><i class="fa-solid fa-user-shield me-2"></i><%= displayName %></div>
            <a href="<%= request.getContextPath() %>/logout" class="nav-link logout-link text-white"><i class="fa-solid fa-right-from-bracket me-2"></i>Cerrar sesion</a>
        </div>
    </nav>

    <div class="flex-grow-1 overflow-hidden">
        <header class="bg-white border-bottom py-3 px-3 px-md-4 d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center gap-2 gap-md-3">
                <button class="btn btn-outline-secondary d-md-none" type="button"
                        data-bs-toggle="offcanvas" data-bs-target="#mobileSidebar"
                        aria-controls="mobileSidebar" aria-label="Abrir menu">
                    <i class="fa-solid fa-bars"></i>
                </button>
                <div>
                    <h1 class="h5 h4-md mb-0">BiblioSoft MVC</h1>
                    <small class="text-muted d-none d-sm-inline">Sistema de biblioteca en arquitectura MVC</small>
                </div>
            </div>
            <div class="text-end">
                <span class="badge bg-primary">Bienvenido, <%= displayName %></span>
            </div>
        </header>
        <main class="content-wrapper">
