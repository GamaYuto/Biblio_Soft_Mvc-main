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
                            <label for="userPhone" class="form-label">Telefono</label>
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

    <div class="row mb-4">
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
                                <th>Telefono</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="userRows"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="row" id="userHistorySection" style="display:none;">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2">
                    <div>
                        <h3 class="card-title mb-1"><i class="fa-solid fa-clock-rotate-left me-2"></i>Historial del Usuario</h3>
                        <div class="text-muted small" id="userHistorySummary">Seleccione un usuario para ver su historial.</div>
                    </div>
                    <button type="button" class="btn btn-outline-secondary btn-sm" id="closeUserHistory">Cerrar</button>
                </div>
                <div class="card-body">
                    <div class="row g-3 align-items-end mb-3">
                        <div class="col-md-3">
                            <label for="userHistoryDateFrom" class="form-label">Desde</label>
                            <input type="date" id="userHistoryDateFrom" class="form-control">
                        </div>
                        <div class="col-md-3">
                            <label for="userHistoryDateTo" class="form-label">Hasta</label>
                            <input type="date" id="userHistoryDateTo" class="form-control">
                        </div>
                        <div class="col-md-3">
                            <label for="userLoanStatus" class="form-label">Estado Prestamo</label>
                            <select id="userLoanStatus" class="form-select">
                                <option value="ALL">Todos</option>
                                <option value="ACTIVE">Activos</option>
                                <option value="RETURNED">Devueltos</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label for="userReservationStatus" class="form-label">Estado Reserva</label>
                            <select id="userReservationStatus" class="form-select">
                                <option value="ALL">Todas</option>
                                <option value="ACTIVA">Activas</option>
                                <option value="ATENDIDA">Atendidas</option>
                                <option value="CANCELADA">Canceladas</option>
                            </select>
                        </div>
                        <div class="col-12 d-flex flex-wrap gap-2">
                            <button type="button" class="btn btn-primary" id="applyUserHistoryFilters">Aplicar filtros</button>
                            <button type="button" class="btn btn-outline-secondary" id="resetUserHistoryFilters">Limpiar filtros</button>
                        </div>
                    </div>

                    <div class="row g-4">
                        <div class="col-lg-6">
                            <h4 class="h6">Prestamos</h4>
                            <div class="table-responsive" style="max-height: 280px; overflow-y: auto;">
                                <table class="table table-sm table-hover align-middle">
                                    <thead class="table-light">
                                        <tr>
                                            <th>ID</th>
                                            <th>Libro</th>
                                            <th>Fecha Prestamo</th>
                                            <th>Fecha Devolucion</th>
                                            <th>Estado</th>
                                        </tr>
                                    </thead>
                                    <tbody id="userHistoryLoanRows"></tbody>
                                </table>
                            </div>
                        </div>
                        <div class="col-lg-6">
                            <h4 class="h6">Reservas</h4>
                            <div class="table-responsive" style="max-height: 280px; overflow-y: auto;">
                                <table class="table table-sm table-hover align-middle">
                                    <thead class="table-light">
                                        <tr>
                                            <th>ID</th>
                                            <th>Libro</th>
                                            <th>Fecha Reserva</th>
                                            <th>Estado Libro</th>
                                            <th>Estado Reserva</th>
                                        </tr>
                                    </thead>
                                    <tbody id="userHistoryReservationRows"></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div id="userHistoryMessage" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const apiBase = '<%= request.getContextPath() %>/resources';
    const LOAN_STATUS_LABELS = { ACTIVE: 'Activo', RETURNED: 'Devuelto' };
    const BOOK_STATUS_LABELS = {
        DISPONIBLE: 'Disponible',
        PRESTADO: 'Prestado',
        RESERVADO: 'Reservado',
        PERDIDO: 'Perdido',
        MANTENIMIENTO: 'Mantenimiento'
    };
    const RESERVATION_STATUS_LABELS = {
        ACTIVA: 'Activa',
        ATENDIDA: 'Atendida',
        CANCELADA: 'Cancelada'
    };

    const userForm = document.getElementById('userForm');
    const userMessage = document.getElementById('userMessage');
    const userIdInput = document.getElementById('userId');
    const userName = document.getElementById('userName');
    const userEmail = document.getElementById('userEmail');
    const userPhone = document.getElementById('userPhone');
    const userCancel = document.getElementById('userCancel');

    const userHistorySection = document.getElementById('userHistorySection');
    const userHistorySummary = document.getElementById('userHistorySummary');
    const userHistoryMessage = document.getElementById('userHistoryMessage');
    const userHistoryDateFrom = document.getElementById('userHistoryDateFrom');
    const userHistoryDateTo = document.getElementById('userHistoryDateTo');
    const userLoanStatus = document.getElementById('userLoanStatus');
    const userReservationStatus = document.getElementById('userReservationStatus');

    let currentHistoryUserId = null;

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
        if (fb && fb.classList.contains('invalid-feedback')) {
            fb.textContent = '';
        }
    }

    function clearAllErrors() {
        [userName, userEmail, userPhone].forEach((input) => {
            input.classList.remove('is-invalid', 'is-valid');
            const fb = input.nextElementSibling;
            if (fb && fb.classList.contains('invalid-feedback')) {
                fb.textContent = '';
            }
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
            setError(userEmail, 'El email no tiene un formato valido.');
            ok = false;
        } else {
            clearError(userEmail);
        }

        const phone = userPhone.value.trim();
        if (!phone) {
            setError(userPhone, 'El telefono es obligatorio.');
            ok = false;
        } else if (!/^\d{1,15}$/.test(phone)) {
            setError(userPhone, 'El telefono debe contener solo digitos (maximo 15).');
            ok = false;
        } else {
            clearError(userPhone);
        }
        return ok;
    }

    function showMessage(message, type = 'success') {
        userMessage.innerHTML = '<div class="alert alert-' + type + ' py-2">' + message + '</div>';
    }

    function showUserHistoryMessage(message, type = 'info') {
        userHistoryMessage.innerHTML = '<div class="alert alert-' + type + ' py-2 mb-0">' + message + '</div>';
    }

    function getBookStatusBadge(status) {
        const normalized = (status || '').toUpperCase();
        let badgeClass = 'bg-secondary';
        if (normalized === 'DISPONIBLE') {
            badgeClass = 'bg-success';
        } else if (normalized === 'PRESTADO') {
            badgeClass = 'bg-primary';
        } else if (normalized === 'RESERVADO') {
            badgeClass = 'bg-warning text-dark';
        } else if (normalized === 'PERDIDO') {
            badgeClass = 'bg-danger';
        } else if (normalized === 'MANTENIMIENTO') {
            badgeClass = 'bg-dark';
        }
        return '<span class="badge ' + badgeClass + '">' + (BOOK_STATUS_LABELS[normalized] || normalized) + '</span>';
    }

    function getReservationStatusBadge(status) {
        const normalized = (status || '').toUpperCase();
        let badgeClass = 'bg-secondary';
        if (normalized === 'ACTIVA') {
            badgeClass = 'bg-success';
        } else if (normalized === 'ATENDIDA') {
            badgeClass = 'bg-info text-dark';
        } else if (normalized === 'CANCELADA') {
            badgeClass = 'bg-danger';
        }
        return '<span class="badge ' + badgeClass + '">' + (RESERVATION_STATUS_LABELS[normalized] || normalized) + '</span>';
    }

    function getLoanStatusBadge(returned) {
        return returned
            ? '<span class="badge bg-secondary">' + LOAN_STATUS_LABELS.RETURNED + '</span>'
            : '<span class="badge bg-primary">' + LOAN_STATUS_LABELS.ACTIVE + '</span>';
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

    function resetUserHistoryFilters() {
        userHistoryDateFrom.value = '';
        userHistoryDateTo.value = '';
        userLoanStatus.value = 'ALL';
        userReservationStatus.value = 'ALL';
    }

    function closeUserHistory() {
        currentHistoryUserId = null;
        userHistorySection.style.display = 'none';
        document.getElementById('userHistoryLoanRows').innerHTML = '';
        document.getElementById('userHistoryReservationRows').innerHTML = '';
        userHistoryMessage.innerHTML = '';
        userHistorySummary.textContent = 'Seleccione un usuario para ver su historial.';
        resetUserHistoryFilters();
    }

    async function loadUsers() {
        const resp = await fetch(apiBase + '/users');
        const users = await resp.json();
        const tbody = document.getElementById('userRows');
        tbody.innerHTML = '';

        users.forEach((user) => {
            const row = document.createElement('tr');
            row.innerHTML = ''
                + '<td>' + user.idUser + '</td>'
                + '<td>' + (user.name || '') + '</td>'
                + '<td>' + (user.email || '') + '</td>'
                + '<td>' + (user.phone || '') + '</td>'
                + '<td>'
                + '    <button class="btn btn-sm btn-outline-primary me-2" type="button" data-action="edit">Editar</button>'
                + '    <button class="btn btn-sm btn-outline-dark me-2" type="button" data-action="history">Historial</button>'
                + '    <button class="btn btn-sm btn-outline-danger" type="button" data-action="delete">Eliminar</button>'
                + '</td>';
            row.querySelector('[data-action="edit"]').addEventListener('click', () => editUser(user));
            row.querySelector('[data-action="history"]').addEventListener('click', () => openUserHistory(user));
            row.querySelector('[data-action="delete"]').addEventListener('click', () => deleteUser(user.idUser));
            tbody.appendChild(row);
        });
    }

    function editUser(user) {
        userIdInput.value = user.idUser;
        userName.value = user.name || '';
        userEmail.value = user.email || '';
        userPhone.value = user.phone || '';
        userCancel.style.display = 'block';
        document.getElementById('userSubmit').textContent = 'Actualizar';
        clearAllErrors();
    }

    async function saveUser(event) {
        event.preventDefault();
        if (!validateUserForm()) {
            return;
        }

        const idUser = parseInt(userIdInput.value, 10);
        const action = idUser > 0 ? 'update' : 'create';
        const body = {
            action: action,
            idUser: idUser > 0 ? idUser : undefined,
            name: userName.value.trim(),
            email: userEmail.value.trim(),
            phone: userPhone.value.trim()
        };
        const resp = await fetch(apiBase + '/users', {
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
        if (!confirm('Eliminar este usuario?')) {
            return;
        }
        const resp = await fetch(apiBase + '/users', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: 'delete', idUser: id })
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage('Usuario eliminado correctamente.');
            if (currentHistoryUserId === id) {
                closeUserHistory();
            }
            loadUsers();
        } else {
            showMessage(result.error || 'Error al eliminar usuario.', 'danger');
        }
    }

    async function openUserHistory(user) {
        currentHistoryUserId = user.idUser;
        userHistorySection.style.display = 'block';
        userHistorySummary.textContent = 'Cargando historial de ' + (user.name || '') + '...';
        await loadUserHistory();
        userHistorySection.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }

    async function loadUserHistory() {
        if (!currentHistoryUserId) {
            return;
        }

        const params = new URLSearchParams({
            history: 'true',
            idUser: String(currentHistoryUserId),
            loanStatus: userLoanStatus.value,
            reservationStatus: userReservationStatus.value
        });
        if (userHistoryDateFrom.value) {
            params.set('dateFrom', userHistoryDateFrom.value);
        }
        if (userHistoryDateTo.value) {
            params.set('dateTo', userHistoryDateTo.value);
        }

        const resp = await fetch(apiBase + '/users?' + params.toString());
        const result = await resp.json();
        if (!resp.ok) {
            showUserHistoryMessage(result.error || 'No se pudo cargar el historial.', 'danger');
            return;
        }

        userHistorySummary.textContent = 'Usuario: ' + (result.user && result.user.name ? result.user.name : '')
            + ' | Email: ' + (result.user && result.user.email ? result.user.email : 'sin correo');
        renderUserHistoryLoans(result.loans || []);
        renderUserHistoryReservations(result.reservations || []);
        const loansCount = (result.loans || []).length;
        const reservationsCount = (result.reservations || []).length;
        showUserHistoryMessage('Se encontraron ' + loansCount + ' prestamos y ' + reservationsCount + ' reservas.', 'info');
    }

    function renderUserHistoryLoans(loans) {
        const tbody = document.getElementById('userHistoryLoanRows');
        tbody.innerHTML = '';
        if (!loans.length) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay prestamos para los filtros actuales.</td></tr>';
            return;
        }
        loans.forEach((loan) => {
            const row = document.createElement('tr');
            row.innerHTML = ''
                + '<td>' + loan.idLoan + '</td>'
                + '<td>' + (loan.book && loan.book.title ? loan.book.title : '') + '</td>'
                + '<td>' + (loan.loanDate || '') + '</td>'
                + '<td>' + (loan.returnDate || '') + '</td>'
                + '<td>' + getLoanStatusBadge(loan.returned) + '</td>';
            tbody.appendChild(row);
        });
    }

    function renderUserHistoryReservations(reservations) {
        const tbody = document.getElementById('userHistoryReservationRows');
        tbody.innerHTML = '';
        if (!reservations.length) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay reservas para los filtros actuales.</td></tr>';
            return;
        }
        reservations.forEach((reservation) => {
            const row = document.createElement('tr');
            row.innerHTML = ''
                + '<td>' + reservation.idReservation + '</td>'
                + '<td>' + (reservation.book && reservation.book.title ? reservation.book.title : '') + '</td>'
                + '<td>' + (reservation.reservationDate || '') + '</td>'
                + '<td>' + getBookStatusBadge(reservation.book && reservation.book.status ? reservation.book.status : '') + '</td>'
                + '<td>' + getReservationStatusBadge(reservation.status) + '</td>';
            tbody.appendChild(row);
        });
    }

    userName.addEventListener('input', () => { userName.value = userName.value.toUpperCase(); });
    userEmail.addEventListener('input', () => { userEmail.value = userEmail.value.toUpperCase(); });
    userPhone.addEventListener('keydown', (event) => {
        const allowed = ['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 'Home', 'End'];
        if (!allowed.includes(event.key) && !/^\d$/.test(event.key)) {
            event.preventDefault();
        }
    });
    userPhone.setAttribute('maxlength', '15');

    userForm.addEventListener('submit', saveUser);
    userCancel.addEventListener('click', resetForm);
    document.getElementById('applyUserHistoryFilters').addEventListener('click', loadUserHistory);
    document.getElementById('resetUserHistoryFilters').addEventListener('click', () => {
        resetUserHistoryFilters();
        loadUserHistory();
    });
    document.getElementById('closeUserHistory').addEventListener('click', closeUserHistory);

    loadUsers();
</script>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
