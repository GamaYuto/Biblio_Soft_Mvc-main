<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h2 class="card-title mb-0"><i class="fa-solid fa-bookmark me-2"></i>Reservas</h2>
                </div>
                <div class="card-body">
                    <form id="reservationForm" class="row g-3 align-items-end">
                        <div class="col-md-4">
                            <label for="reservationUser" class="form-label">Usuario</label>
                            <select id="reservationUser" class="form-select" required></select>
                        </div>
                        <div class="col-md-4">
                            <label for="reservationBook" class="form-label">Libro</label>
                            <select id="reservationBook" class="form-select" required></select>
                            <div class="form-text">Solo se muestran libros prestados o ya reservados.</div>
                        </div>
                        <div class="col-md-2">
                            <label for="reservationDate" class="form-label">Fecha Reserva</label>
                            <input type="date" id="reservationDate" class="form-control" required>
                        </div>
                        <div class="col-md-2 d-grid">
                            <button type="submit" class="btn btn-secondary">Registrar</button>
                        </div>
                    </form>
                    <div class="alert alert-light border mt-3 mb-0">
                        Cuando un libro prestado se devuelve y tiene reservas activas, el sistema cambia su estado a <strong>RESERVADO</strong>.
                        Desde el modulo de prestamos, el usuario con reserva activa ya podra registrar el prestamo.
                    </div>
                    <div class="row mt-4 g-2">
                        <div class="col-12 col-md-3">
                            <input type="number" id="filterReservationUserId" class="form-control" placeholder="Filtrar por userId">
                        </div>
                        <div class="col-12 col-md-3">
                            <input type="number" id="filterReservationBookId" class="form-control" placeholder="Filtrar por bookId">
                        </div>
                        <div class="col-12 col-md-6 d-flex flex-wrap gap-2">
                            <button type="button" class="btn btn-primary" id="reservationFilter">Filtrar</button>
                            <button type="button" class="btn btn-success" id="reservationActive">Activas</button>
                            <button type="button" class="btn btn-dark" id="reservationHistory">Historial</button>
                        </div>
                    </div>
                    <div id="reservationMessage" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h3 class="card-title mb-0">Tabla de Reservas</h3>
                </div>
                <div class="card-body table-responsive" style="max-height: 360px; overflow-y: auto;">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Usuario</th>
                                <th>Libro</th>
                                <th>Estado Libro</th>
                                <th>Fecha Reserva</th>
                                <th>Estado Reserva</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="reservationRows"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const apiBase = '<%= request.getContextPath() %>/resources';
    const reservationForm = document.getElementById('reservationForm');
    const reservationMessage = document.getElementById('reservationMessage');
    const reservationUser = document.getElementById('reservationUser');
    const reservationBook = document.getElementById('reservationBook');
    const reservationDate = document.getElementById('reservationDate');
    const filterReservationUserId = document.getElementById('filterReservationUserId');
    const filterReservationBookId = document.getElementById('filterReservationBookId');

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

    reservationDate.value = new Date().toISOString().split('T')[0];

    function setError(input, msg) {
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
        let feedback = input.nextElementSibling;
        if (!feedback || !feedback.classList.contains('invalid-feedback')) {
            feedback = document.createElement('div');
            feedback.className = 'invalid-feedback';
            input.insertAdjacentElement('afterend', feedback);
        }
        feedback.textContent = msg;
    }

    function clearError(input) {
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
        const feedback = input.nextElementSibling;
        if (feedback && feedback.classList.contains('invalid-feedback')) {
            feedback.textContent = '';
        }
    }

    function clearAllErrors() {
        [reservationUser, reservationBook, reservationDate].forEach((input) => {
            input.classList.remove('is-invalid', 'is-valid');
            const feedback = input.nextElementSibling;
            if (feedback && feedback.classList.contains('invalid-feedback')) {
                feedback.textContent = '';
            }
        });
    }

    function showMessage(message, type = 'success') {
        reservationMessage.innerHTML = '<div class="alert alert-' + type + ' py-2">' + message + '</div>';
    }

    function getBadge(labelMap, status) {
        const normalized = (status || '').toUpperCase();
        let badgeClass = 'bg-secondary';
        if (normalized === 'DISPONIBLE' || normalized === 'ACTIVA') {
            badgeClass = normalized === 'ACTIVA' ? 'bg-success' : 'bg-success';
        } else if (normalized === 'PRESTADO') {
            badgeClass = 'bg-primary';
        } else if (normalized === 'RESERVADO') {
            badgeClass = 'bg-warning text-dark';
        } else if (normalized === 'PERDIDO' || normalized === 'CANCELADA') {
            badgeClass = normalized === 'CANCELADA' ? 'bg-danger' : 'bg-danger';
        } else if (normalized === 'MANTENIMIENTO') {
            badgeClass = 'bg-dark';
        } else if (normalized === 'ATENDIDA') {
            badgeClass = 'bg-info text-dark';
        }
        return '<span class="badge ' + badgeClass + '">' + (labelMap[normalized] || normalized) + '</span>';
    }

    function validateForm() {
        let ok = true;
        if (!reservationUser.value || parseInt(reservationUser.value, 10) <= 0) {
            setError(reservationUser, 'Debe seleccionar un usuario.');
            ok = false;
        } else {
            clearError(reservationUser);
        }

        if (!reservationBook.value || parseInt(reservationBook.value, 10) <= 0) {
            setError(reservationBook, 'Debe seleccionar un libro reservable.');
            ok = false;
        } else {
            clearError(reservationBook);
        }

        if (!reservationDate.value) {
            setError(reservationDate, 'La fecha de reserva es obligatoria.');
            ok = false;
        } else {
            clearError(reservationDate);
        }

        return ok;
    }

    async function loadUsers() {
        const resp = await fetch(apiBase + '/users');
        const users = await resp.json();
        reservationUser.innerHTML = '<option value="">Seleccione usuario</option>';
        users.forEach((user) => {
            const option = document.createElement('option');
            option.value = user.idUser;
            option.textContent = user.name + ' (' + (user.email || 'sin correo') + ')';
            reservationUser.appendChild(option);
        });
    }

    async function loadBooks() {
        const resp = await fetch(apiBase + '/books?reservable=true');
        const books = await resp.json();
        reservationBook.innerHTML = '<option value="">Seleccione libro</option>';
        books.forEach((book) => {
            const option = document.createElement('option');
            option.value = book.idBook;
            option.textContent = '[' + (BOOK_STATUS_LABELS[(book.status || '').toUpperCase()] || book.status) + '] '
                + book.title + ' - ' + (book.author && book.author.name ? book.author.name : '');
            reservationBook.appendChild(option);
        });
    }

    async function loadReservations(params = '') {
        const resp = await fetch(apiBase + '/reservations' + params);
        const reservations = await resp.json();
        const tbody = document.getElementById('reservationRows');
        tbody.innerHTML = '';

        reservations.forEach((reservation) => {
            const row = document.createElement('tr');
            row.innerHTML = ''
                + '<td>' + reservation.idReservation + '</td>'
                + '<td>' + (reservation.user && reservation.user.name ? reservation.user.name : '') + '</td>'
                + '<td>' + (reservation.book && reservation.book.title ? reservation.book.title : '') + '</td>'
                + '<td>' + getBadge(BOOK_STATUS_LABELS, reservation.book && reservation.book.status ? reservation.book.status : '') + '</td>'
                + '<td>' + (reservation.reservationDate || '') + '</td>'
                + '<td>' + getBadge(RESERVATION_STATUS_LABELS, reservation.status) + '</td>'
                + '<td></td>';

            const actionsCell = row.querySelector('td:last-child');
            if ((reservation.status || '').toUpperCase() === 'ACTIVA') {
                const attendButton = document.createElement('button');
                attendButton.type = 'button';
                attendButton.className = 'btn btn-sm btn-outline-primary me-2';
                attendButton.textContent = 'Atender';
                attendButton.addEventListener('click', () => updateReservation(reservation.idReservation, 'fulfill'));

                const cancelButton = document.createElement('button');
                cancelButton.type = 'button';
                cancelButton.className = 'btn btn-sm btn-outline-danger';
                cancelButton.textContent = 'Cancelar';
                cancelButton.addEventListener('click', () => updateReservation(reservation.idReservation, 'cancel'));

                actionsCell.appendChild(attendButton);
                actionsCell.appendChild(cancelButton);
            } else {
                actionsCell.innerHTML = '<span class="text-muted">Sin acciones</span>';
            }
            tbody.appendChild(row);
        });
    }

    async function saveReservation(event) {
        event.preventDefault();
        if (!validateForm()) {
            return;
        }

        const resp = await fetch(apiBase + '/reservations', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                action: 'create',
                reservationDate: reservationDate.value,
                user: { idUser: parseInt(reservationUser.value, 10) },
                book: { idBook: parseInt(reservationBook.value, 10) }
            })
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage('Reserva registrada correctamente.');
            clearAllErrors();
            reservationForm.reset();
            reservationDate.value = new Date().toISOString().split('T')[0];
            loadBooks();
            loadReservations();
        } else {
            showMessage(result.error || 'Error al registrar reserva.', 'danger');
        }
    }

    async function updateReservation(idReservation, action) {
        const message = action === 'cancel' ? 'Cancelar esta reserva?' : 'Marcar esta reserva como atendida?';
        if (!confirm(message)) {
            return;
        }

        const resp = await fetch(apiBase + '/reservations', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: action, idReservation: idReservation })
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage(action === 'cancel' ? 'Reserva cancelada correctamente.' : 'Reserva atendida correctamente.');
            loadBooks();
            loadReservations();
        } else {
            showMessage(result.error || 'Error al actualizar reserva.', 'danger');
        }
    }

    function filterReservations() {
        const userId = filterReservationUserId.value.trim();
        const bookId = filterReservationBookId.value.trim();
        if (userId) {
            loadReservations('?userId=' + encodeURIComponent(userId));
        } else if (bookId) {
            loadReservations('?bookId=' + encodeURIComponent(bookId));
        } else {
            showMessage('Ingrese userId o bookId para filtrar.', 'warning');
        }
    }

    reservationForm.addEventListener('submit', saveReservation);
    document.getElementById('reservationFilter').addEventListener('click', filterReservations);
    document.getElementById('reservationActive').addEventListener('click', () => loadReservations());
    document.getElementById('reservationHistory').addEventListener('click', () => loadReservations('?history=true'));

    Promise.all([loadUsers(), loadBooks()]).then(() => loadReservations());
</script>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
