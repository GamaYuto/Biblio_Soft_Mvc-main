<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mycompany.biblio_soft_mvc.model.AdminUser" %>
<%@ page import="com.mycompany.biblio_soft_mvc.servlet.LoginServlet" %>
<%
    AdminUser sessionAdmin = (AdminUser) session.getAttribute(LoginServlet.SESSION_ADMIN_USER);
    if (sessionAdmin != null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | BiblioSoft MVC</title>
    <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Ctext y='.9em' font-size='90'%3E%F0%9F%93%9A%3C/text%3E%3C/svg%3E">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">
    <style>
        body {
            min-height: 100vh;
            margin: 0;
            background:
                radial-gradient(circle at top left, rgba(59, 130, 246, 0.18), transparent 32%),
                radial-gradient(circle at bottom right, rgba(16, 185, 129, 0.18), transparent 28%),
                linear-gradient(135deg, #f3f4f6 0%, #e5eef7 100%);
            font-family: "Segoe UI", sans-serif;
        }
        .login-shell {
            min-height: 100vh;
        }
        .login-card {
            width: 100%;
            max-width: 430px;
            border: 0;
            border-radius: 1.25rem;
            box-shadow: 0 24px 80px rgba(15, 23, 42, 0.14);
        }
        .brand-badge {
            width: 64px;
            height: 64px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 18px;
            background: linear-gradient(135deg, #1d4ed8, #0f766e);
            color: #fff;
            font-size: 1.5rem;
        }
    </style>
</head>
<body>
    <div class="container login-shell d-flex align-items-center justify-content-center py-5">
        <div class="card login-card">
            <div class="card-body p-4 p-md-5">
                <div class="text-center mb-4">
                    <div class="brand-badge mx-auto mb-3">
                        <i class="fa-solid fa-book-open-reader"></i>
                    </div>
                    <h1 class="h3 mb-2">BiblioSoft MVC</h1>
                    <p class="text-muted mb-0">Accede al panel administrativo de la biblioteca.</p>
                </div>

                <form id="loginForm" novalidate>
                    <div class="mb-3">
                        <label for="username" class="form-label">Usuario</label>
                        <input type="text" class="form-control form-control-lg" id="username" name="username" autocomplete="username" required>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Contrasena</label>
                        <input type="password" class="form-control form-control-lg" id="password" name="password" autocomplete="current-password" required>
                    </div>
                    <div id="loginMessage" class="mb-3"></div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            <i class="fa-solid fa-right-to-bracket me-2"></i>Iniciar sesion
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        const loginForm = document.getElementById('loginForm');
        const loginMessage = document.getElementById('loginMessage');
        const contextPath = '<%= request.getContextPath() %>';
        const loginEndpoint = `${contextPath}/resources/login`;

        function showMessage(message, type) {
            loginMessage.innerHTML = `<div class="alert alert-${type} py-2 mb-0">${message}</div>`;
        }

        async function checkActiveSession() {
            const resp = await fetch(loginEndpoint, {
                headers: { 'Accept': 'application/json' }
            });
            if (!resp.ok) {
                return;
            }
            const data = await resp.json();
            if (data.loggedIn) {
                window.location.href = `${contextPath}/index.jsp`;
            }
        }

        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            loginMessage.innerHTML = '';

            const formData = new URLSearchParams();
            formData.append('username', document.getElementById('username').value.trim());
            formData.append('password', document.getElementById('password').value);

            const resp = await fetch(loginEndpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    'X-Requested-With': 'XMLHttpRequest',
                    'Accept': 'application/json'
                },
                body: formData.toString()
            });

            const data = await resp.json();
            if (resp.ok && data.success) {
                window.location.href = data.redirect || `${contextPath}/index.jsp`;
                return;
            }

            showMessage(data.error || 'No fue posible iniciar sesion.', 'danger');
        });

        checkActiveSession();
    </script>
</body>
</html>
