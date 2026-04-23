<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h2 class="card-title mb-0"><i class="fa-solid fa-users me-2"></i>Usuarios</h2>
                </div>
                <div class="card-body">
                    <form id="userForm" class="row g-3 align-items-end">
                        <input type="hidden" id="userId" value="" />
                        <div class="col-md-4">
                            <label for="userName" class="form-label">Nombre</label>
                            <input type="text" id="userName" class="form-control" required>
                        </div>
                        <div class="col-md-4">
                            <label for="userEmail" class="form-label">Email</label>
                            <input type="email" id="userEmail" class="form-control" required>
                        </div>
                        <div class="col-md-3">
                            <label for="userPhone" class="form-label">Teléfono</label>
                            <input type="text" id="userPhone" class="form-control" required>
                        </div>
                        <div class="col-md-1 d-grid">
                            <button type="submit" class="btn btn-primary" id="userSubmit">Registrar</button>
                            <button type="button" class="btn btn-secondary mt-2" id="userCancel" style="display:none;">Cancelar</button>
                        </div>
                    </form>
                    <div id="userMessage" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h3 class="card-title mb-0">Lista de Usuarios</h3>
                </div>
                <div class="card-body table-responsive" style="max-height: 360px; overflow-y: auto;">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Email</th>
                                <th>Teléfono</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="userRows"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const apiBase = '<%= request.getContextPath() %>/resources';
    const userForm = document.getElementById('userForm');
    const userMessage = document.getElementById('userMessage');
    const userIdInput = document.getElementById('userId');
    const userName = document.getElementById('userName');
    const userEmail = document.getElementById('userEmail');
    const userPhone = document.getElementById('userPhone');
    const userCancel = document.getElementById('userCancel');

    // ── Utilidades de validación ────────────────────────────────────
    const SOLO_LETRAS = /^[\p{L}\s]+$/u;

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
        [userName, userEmail, userPhone].forEach(inp => {
            inp.classList.remove('is-invalid', 'is-valid');
            const fb = inp.nextElementSibling;
            if (fb && fb.classList.contains('invalid-feedback')) fb.textContent = '';
        });
    }

    function validateUserForm() {
        let ok = true;

        const name = userName.value.trim();
        if (!name) {
            setError(userName, 'El nombre es obligatorio.');
            ok = false;
        } else if (!SOLO_LETRAS.test(name)) {
            setError(userName, 'El nombre solo puede contener letras y espacios.');
            ok = false;
        } else {
            clearError(userName);
        }

        const email = userEmail.value.trim();
        if (!email) {
            setError(userEmail, 'El email es obligatorio.');
            ok = false;
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            setError(userEmail, 'El email no tiene un formato válido.');
            ok = false;
        } else {
            clearError(userEmail);
        }

        const phone = userPhone.value.trim();
        if (!phone) {
            setError(userPhone, 'El teléfono es obligatorio.');
            ok = false;
        } else if (!/^\d{1,15}$/.test(phone)) {
            setError(userPhone, 'El teléfono debe contener solo dígitos (máximo 15).');
            ok = false;
        } else {
            clearError(userPhone);
        }

        return ok;
    }

    // ── Transformaciones en tiempo real ─────────────────────────────
    userName.addEventListener('input', () => { userName.value = userName.value.toUpperCase(); });
    userEmail.addEventListener('input', () => { userEmail.value = userEmail.value.toUpperCase(); });
    userPhone.addEventListener('keydown', e => {
        const allowed = ['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 'Home', 'End'];
        if (!allowed.includes(e.key) && !/^\d$/.test(e.key)) e.preventDefault();
    });
    userPhone.setAttribute('maxlength', '15');

    // ── Carga de usuarios ───────────────────────────────────────────
    async function loadUsers() {
        const resp = await fetch(`${apiBase}/users`);
        const users = await resp.json();
        const tbody = document.getElementById('userRows');
        tbody.innerHTML = '';
        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${user.idUser}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>${user.phone}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-2" type="button">Editar</button>
                    <button class="btn btn-sm btn-outline-danger" type="button">Eliminar</button>
                </td>
            `;
            row.querySelector('button[type="button"]').addEventListener('click', () => editUser(user));
            row.querySelectorAll('button[type="button"]')[1].addEventListener('click', () => deleteUser(user.idUser));
            tbody.appendChild(row);
        });
    }

    function showMessage(message, type = 'success') {
        userMessage.innerHTML = `<div class="alert alert-${type} py-2">${message}</div>`;
    }

    function resetForm() {
        userIdInput.value = '';
        userName.value = '';
        userEmail.value = '';
        userPhone.value = '';
        userCancel.style.display = 'none';
        document.getElementById('userSubmit').textContent = 'Registrar';
        clearAllErrors();
    }

    function editUser(user) {
        userIdInput.value = user.idUser;
        userName.value = user.name;
        userEmail.value = user.email;
        userPhone.value = user.phone;
        userCancel.style.display = 'block';
        document.getElementById('userSubmit').textContent = 'Actualizar';
        clearAllErrors();
    }

    async function saveUser(event) {
        event.preventDefault();
        if (!validateUserForm()) return;
        const idUser = parseInt(userIdInput.value, 10);
        const action = idUser > 0 ? 'update' : 'create';
        const body = {
            action,
            idUser: idUser > 0 ? idUser : undefined,
            name: userName.value.trim(),
            email: userEmail.value.trim(),
            phone: userPhone.value.trim()
        };
        const resp = await fetch(`${apiBase}/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage(idUser > 0 ? 'Usuario actualizado correctamente.' : 'Usuario registrado correctamente.');
            resetForm();
            loadUsers();
        } else {
            showMessage(result.error || 'Error al guardar usuario.', 'danger');
        }
    }

    async function deleteUser(id) {
        if (!confirm('¿Eliminar este usuario?')) return;
        const resp = await fetch(`${apiBase}/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: 'delete', idUser: id })
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage('Usuario eliminado correctamente.');
            loadUsers();
        } else {
            showMessage(result.error || 'Error al eliminar usuario.', 'danger');
        }
    }

    userForm.addEventListener('submit', saveUser);
    userCancel.addEventListener('click', resetForm);
    loadUsers();
</script>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
