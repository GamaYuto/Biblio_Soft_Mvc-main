<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h2 class="card-title mb-0"><i class="fa-solid fa-user-shield me-2"></i>Administradores</h2>
                </div>
                <div class="card-body">
                    <form id="adminUserForm" class="row g-3 align-items-end">
                        <input type="hidden" id="adminUserId" value="" />
                        <div class="col-md-3">
                            <label for="adminUsername" class="form-label">Username</label>
                            <input type="text" id="adminUsername" class="form-control" required>
                        </div>
                        <div class="col-md-4">
                            <label for="adminFullName" class="form-label">Nombre completo</label>
                            <input type="text" id="adminFullName" class="form-control">
                        </div>
                        <div class="col-md-3">
                            <label for="adminPassword" class="form-label">Contrasena</label>
                            <input type="password" id="adminPassword" class="form-control" required>
                            <div class="form-text">En edicion, deja este campo vacio para conservar la clave actual.</div>
                        </div>
                        <div class="col-md-2 d-grid">
                            <button type="submit" class="btn btn-primary" id="adminUserSubmit">Registrar</button>
                            <button type="button" class="btn btn-secondary mt-2" id="adminUserCancel" style="display:none;">Cancelar</button>
                        </div>
                    </form>
                    <div id="adminUserMessage" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h3 class="card-title mb-0">Lista de administradores</h3>
                </div>
                <div class="card-body table-responsive" style="max-height: 360px; overflow-y: auto;">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Nombre completo</th>
                                <th>Creado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="adminUserRows"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const apiBase = '<%= request.getContextPath() %>/resources';
    const adminUserForm = document.getElementById('adminUserForm');
    const adminUserMessage = document.getElementById('adminUserMessage');
    const adminUserIdInput = document.getElementById('adminUserId');
    const adminUsername = document.getElementById('adminUsername');
    const adminFullName = document.getElementById('adminFullName');
    const adminPassword = document.getElementById('adminPassword');
    const adminUserCancel = document.getElementById('adminUserCancel');

    function setError(input, msg) {
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
        let fb = input.nextElementSibling;
        if (!fb || !fb.classList.contains('invalid-feedback')) {
            fb = document.createElement('div');
            fb.className = 'invalid-feedback';
            input.insertAdjacentElement('afterend', fb);
        }
        fb.textContent = msg;
    }

    function clearError(input) {
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
        const fb = input.nextElementSibling;
        if (fb && fb.classList.contains('invalid-feedback')) fb.textContent = '';
    }

    function clearAllErrors() {
        [adminUsername, adminFullName, adminPassword].forEach(inp => {
            inp.classList.remove('is-invalid', 'is-valid');
            const fb = inp.nextElementSibling;
            if (fb && fb.classList.contains('invalid-feedback')) fb.textContent = '';
        });
    }

    function validateAdminUserForm() {
        let ok = true;
        const editing = parseInt(adminUserIdInput.value, 10) > 0;
        const username = adminUsername.value.trim();
        const fullName = adminFullName.value.trim();
        const password = adminPassword.value;

        if (!username) {
            setError(adminUsername, 'El username es obligatorio.');
            ok = false;
        } else if (!/^[A-Za-z0-9._-]{3,30}$/.test(username)) {
            setError(adminUsername, 'Usa entre 3 y 30 caracteres: letras, numeros, punto, guion o guion bajo.');
            ok = false;
        } else {
            clearError(adminUsername);
        }

        if (fullName && !/^[\p{L}\s]{3,100}$/u.test(fullName)) {
            setError(adminFullName, 'El nombre completo solo puede contener letras y espacios.');
            ok = false;
        } else {
            clearError(adminFullName);
        }

        if (!editing && !password) {
            setError(adminPassword, 'La contrasena es obligatoria.');
            ok = false;
        } else if (password && password.length < 8) {
            setError(adminPassword, 'La contrasena debe tener al menos 8 caracteres.');
            ok = false;
        } else {
            clearError(adminPassword);
        }

        return ok;
    }

    async function loadAdminUsers() {
        const resp = await fetch(apiBase + '/admin-users');
        const adminUsers = await resp.json();
        const tbody = document.getElementById('adminUserRows');
        tbody.innerHTML = '';
        adminUsers.forEach(admin => {
            const row = document.createElement('tr');
            const createdAt = admin.createdAt ? new Date(admin.createdAt).toLocaleString() : '';
            row.innerHTML = `
                <td>${admin.idAdmin}</td>
                <td>${admin.username}</td>
                <td>${admin.fullName || ''}</td>
                <td>${createdAt}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-2" type="button">Editar</button>
                    <button class="btn btn-sm btn-outline-danger" type="button">Eliminar</button>
                </td>
            `;
            row.querySelector('button[type="button"]').addEventListener('click', () => editAdminUser(admin));
            row.querySelectorAll('button[type="button"]')[1].addEventListener('click', () => deleteAdminUser(admin.idAdmin, admin.username));
            tbody.appendChild(row);
        });
    }

    function showMessage(message, type = 'success') {
        adminUserMessage.innerHTML = '<div class="alert alert-' + type + ' py-2">' + message + '</div>';
    }

    function resetForm() {
        adminUserIdInput.value = '';
        adminUsername.value = '';
        adminFullName.value = '';
        adminPassword.value = '';
        adminPassword.required = true;
        adminUserCancel.style.display = 'none';
        document.getElementById('adminUserSubmit').textContent = 'Registrar';
        clearAllErrors();
    }

    function editAdminUser(admin) {
        adminUserIdInput.value = admin.idAdmin;
        adminUsername.value = admin.username;
        adminFullName.value = admin.fullName || '';
        adminPassword.value = '';
        adminPassword.required = false;
        adminUserCancel.style.display = 'block';
        document.getElementById('adminUserSubmit').textContent = 'Actualizar';
        clearAllErrors();
    }

    async function saveAdminUser(event) {
        event.preventDefault();
        if (!validateAdminUserForm()) return;

        const idAdmin = parseInt(adminUserIdInput.value, 10);
        const body = {
            action: idAdmin > 0 ? 'update' : 'create',
            idAdmin: idAdmin > 0 ? idAdmin : undefined,
            username: adminUsername.value.trim(),
            fullName: adminFullName.value.trim(),
            password: adminPassword.value
        };

        const resp = await fetch(apiBase + '/admin-users', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage(idAdmin > 0 ? 'Administrador actualizado correctamente.' : 'Administrador creado correctamente.');
            resetForm();
            loadAdminUsers();
        } else {
            showMessage(result.error || 'Error al guardar administrador.', 'danger');
        }
    }

    async function deleteAdminUser(id, username) {
        if (!confirm('Eliminar el administrador ' + username + '?')) return;
        const resp = await fetch(apiBase + '/admin-users', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: 'delete', idAdmin: id })
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage('Administrador eliminado correctamente.');
            loadAdminUsers();
        } else {
            showMessage(result.error || 'Error al eliminar administrador.', 'danger');
        }
    }

    adminUserForm.addEventListener('submit', saveAdminUser);
    adminUserCancel.addEventListener('click', resetForm);
    loadAdminUsers();
</script>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
