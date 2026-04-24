<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h2 class="card-title mb-0"><i class="fa-solid fa-book me-2"></i>Libros</h2>
                </div>
                <div class="card-body">
                    <form id="bookForm" class="row g-3 align-items-end">
                        <input type="hidden" id="bookId" value="" />
                        <div class="col-md-4">
                            <label for="bookTitle" class="form-label">Titulo</label>
                            <input type="text" id="bookTitle" class="form-control" required>
                        </div>
                        <div class="col-md-2">
                            <label for="bookIsbn" class="form-label">ISBN</label>
                            <input type="text" id="bookIsbn" class="form-control" required>
                        </div>
                        <div class="col-md-2">
                            <label for="bookYear" class="form-label">Anio</label>
                            <input type="number" id="bookYear" class="form-control" required>
                        </div>
                        <div class="col-md-2">
                            <label for="bookStatus" class="form-label">Estado</label>
                            <select id="bookStatus" class="form-select" required>
                                <option value="DISPONIBLE">Disponible</option>
                                <option value="PRESTADO">Prestado</option>
                                <option value="RESERVADO">Reservado</option>
                                <option value="PERDIDO">Perdido</option>
                                <option value="MANTENIMIENTO">Mantenimiento</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label for="bookAuthor" class="form-label">Autor</label>
                            <select id="bookAuthor" class="form-select" required></select>
                        </div>
                        <div class="col-md-3">
                            <label for="bookCategory" class="form-label">Categoria</label>
                            <select id="bookCategory" class="form-select" required></select>
                        </div>
                        <div class="col-md-12 d-grid">
                            <button type="submit" class="btn btn-success" id="bookSubmit">Registrar</button>
                            <button type="button" class="btn btn-secondary mt-2" id="bookCancel" style="display:none;">Cancelar</button>
                        </div>
                    </form>
                    <div class="row mt-4 g-2 align-items-end">
                        <div class="col-12 col-md-3">
                            <div class="input-group">
                                <span class="input-group-text">Buscar</span>
                                <input type="text" id="searchTitle" class="form-control" placeholder="Titulo">
                            </div>
                        </div>
                        <div class="col-12 col-md-3">
                            <div class="input-group">
                                <span class="input-group-text">Autor</span>
                                <input type="text" id="searchAuthor" class="form-control" placeholder="Nombre autor">
                            </div>
                        </div>
                        <div class="col-12 col-md-2">
                            <div class="input-group">
                                <span class="input-group-text">ISBN</span>
                                <input type="text" id="searchIsbn" class="form-control" placeholder="ISBN">
                            </div>
                        </div>
                        <div class="col-12 col-md-2">
                            <select id="searchCategory" class="form-select">
                                <option value="">Categoria</option>
                            </select>
                        </div>
                        <div class="col-6 col-md-1">
                            <input type="number" id="searchYearFrom" class="form-control" placeholder="Desde" min="1000">
                        </div>
                        <div class="col-6 col-md-1">
                            <input type="number" id="searchYearTo" class="form-control" placeholder="Hasta" min="1000">
                        </div>
                        <div class="col-12 col-md-1 d-grid">
                            <button type="button" class="btn btn-primary" id="bookSearch">Buscar</button>
                        </div>
                        <div class="col-12 col-md-1 d-grid">
                            <button type="button" class="btn btn-outline-secondary" id="bookSearchClear">Limpiar</button>
                        </div>
                    </div>
                    <div id="bookMessage" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row mb-4">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h3 class="card-title mb-0">Lista de Libros</h3>
                </div>
                <div class="card-body table-responsive" style="max-height: 360px; overflow-y: auto;">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Titulo</th>
                                <th>ISBN</th>
                                <th>Anio</th>
                                <th>Estado</th>
                                <th>Autor</th>
                                <th>Categoria</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="bookRows"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="row" id="bookHistorySection" style="display:none;">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2">
                    <div>
                        <h3 class="card-title mb-1"><i class="fa-solid fa-clock-rotate-left me-2"></i>Historial del Libro</h3>
                        <div class="text-muted small" id="bookHistorySummary">Seleccione un libro para ver su circulacion.</div>
                    </div>
                    <button type="button" class="btn btn-outline-secondary btn-sm" id="closeBookHistory">Cerrar</button>
                </div>
                <div class="card-body">
                    <div class="row g-3 align-items-end mb-3">
                        <div class="col-md-3">
                            <label for="bookHistoryDateFrom" class="form-label">Desde</label>
                            <input type="date" id="bookHistoryDateFrom" class="form-control">
                        </div>
                        <div class="col-md-3">
                            <label for="bookHistoryDateTo" class="form-label">Hasta</label>
                            <input type="date" id="bookHistoryDateTo" class="form-control">
                        </div>
                        <div class="col-md-3">
                            <label for="bookLoanStatus" class="form-label">Estado Prestamo</label>
                            <select id="bookLoanStatus" class="form-select">
                                <option value="ALL">Todos</option>
                                <option value="ACTIVE">Activos</option>
                                <option value="RETURNED">Devueltos</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label for="bookReservationStatus" class="form-label">Estado Reserva</label>
                            <select id="bookReservationStatus" class="form-select">
                                <option value="ALL">Todas</option>
                                <option value="ACTIVA">Activas</option>
                                <option value="ATENDIDA">Atendidas</option>
                                <option value="CANCELADA">Canceladas</option>
                            </select>
                        </div>
                        <div class="col-12 d-flex flex-wrap gap-2">
                            <button type="button" class="btn btn-primary" id="applyBookHistoryFilters">Aplicar filtros</button>
                            <button type="button" class="btn btn-outline-secondary" id="resetBookHistoryFilters">Limpiar filtros</button>
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
                                            <th>Usuario</th>
                                            <th>Fecha Prestamo</th>
                                            <th>Fecha Devolucion</th>
                                            <th>Estado</th>
                                        </tr>
                                    </thead>
                                    <tbody id="bookHistoryLoanRows"></tbody>
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
                                            <th>Usuario</th>
                                            <th>Fecha Reserva</th>
                                            <th>Estado Libro</th>
                                            <th>Estado Reserva</th>
                                        </tr>
                                    </thead>
                                    <tbody id="bookHistoryReservationRows"></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div id="bookHistoryMessage" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const apiBase = '<%= request.getContextPath() %>/resources';
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

    const bookForm = document.getElementById('bookForm');
    const bookMessage = document.getElementById('bookMessage');
    const bookIdInput = document.getElementById('bookId');
    const bookTitle = document.getElementById('bookTitle');
    const bookIsbn = document.getElementById('bookIsbn');
    const bookYear = document.getElementById('bookYear');
    const bookStatus = document.getElementById('bookStatus');
    const bookAuthor = document.getElementById('bookAuthor');
    const bookCategory = document.getElementById('bookCategory');
    const bookCancel = document.getElementById('bookCancel');
    const searchTitle = document.getElementById('searchTitle');
    const searchAuthor = document.getElementById('searchAuthor');
    const searchIsbn = document.getElementById('searchIsbn');
    const searchCategory = document.getElementById('searchCategory');
    const searchYearFrom = document.getElementById('searchYearFrom');
    const searchYearTo = document.getElementById('searchYearTo');

    const bookHistorySection = document.getElementById('bookHistorySection');
    const bookHistorySummary = document.getElementById('bookHistorySummary');
    const bookHistoryMessage = document.getElementById('bookHistoryMessage');
    const bookHistoryDateFrom = document.getElementById('bookHistoryDateFrom');
    const bookHistoryDateTo = document.getElementById('bookHistoryDateTo');
    const bookLoanStatus = document.getElementById('bookLoanStatus');
    const bookReservationStatus = document.getElementById('bookReservationStatus');

    let currentHistoryBookId = null;

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
        [bookTitle, bookIsbn, bookYear, bookStatus, bookAuthor, bookCategory].forEach((input) => {
            input.classList.remove('is-invalid', 'is-valid');
            const fb = input.nextElementSibling;
            if (fb && fb.classList.contains('invalid-feedback')) {
                fb.textContent = '';
            }
        });
    }

    function validateBookForm() {
        let ok = true;
        const currentYear = new Date().getFullYear();
        const allowedStatuses = Object.keys(BOOK_STATUS_LABELS);

        const title = bookTitle.value.trim();
        if (!title) {
            setError(bookTitle, 'El titulo del libro es obligatorio.');
            ok = false;
        } else {
            clearError(bookTitle);
        }

        const isbn = bookIsbn.value.trim();
        if (!isbn) {
            setError(bookIsbn, 'El ISBN es obligatorio.');
            ok = false;
        } else if (!/^[0-9-]+$/.test(isbn)) {
            setError(bookIsbn, 'El ISBN solo puede contener digitos y guiones.');
            ok = false;
        } else {
            clearError(bookIsbn);
        }

        const yearVal = bookYear.value.trim();
        const year = parseInt(yearVal, 10);
        if (!yearVal || Number.isNaN(year)) {
            setError(bookYear, 'El anio es obligatorio.');
            ok = false;
        } else if (year < 1000 || year > currentYear) {
            setError(bookYear, 'El anio debe estar entre 1000 y ' + currentYear + '.');
            ok = false;
        } else {
            clearError(bookYear);
        }

        if (!allowedStatuses.includes(bookStatus.value)) {
            setError(bookStatus, 'Debe seleccionar un estado valido.');
            ok = false;
        } else {
            clearError(bookStatus);
        }

        if (!bookAuthor.value || parseInt(bookAuthor.value, 10) <= 0) {
            setError(bookAuthor, 'Debe seleccionar un autor.');
            ok = false;
        } else {
            clearError(bookAuthor);
        }

        if (!bookCategory.value || parseInt(bookCategory.value, 10) <= 0) {
            setError(bookCategory, 'Debe seleccionar una categoria.');
            ok = false;
        } else {
            clearError(bookCategory);
        }

        return ok;
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
            ? '<span class="badge bg-secondary">Devuelto</span>'
            : '<span class="badge bg-primary">Activo</span>';
    }

    function showMessage(message, type = 'success') {
        bookMessage.innerHTML = '<div class="alert alert-' + type + ' py-2">' + message + '</div>';
    }

    function showBookHistoryMessage(message, type = 'info') {
        bookHistoryMessage.innerHTML = '<div class="alert alert-' + type + ' py-2 mb-0">' + message + '</div>';
    }

    function resetForm() {
        bookIdInput.value = '';
        bookTitle.value = '';
        bookIsbn.value = '';
        bookYear.value = '';
        bookStatus.value = 'DISPONIBLE';
        bookAuthor.value = '';
        bookCategory.value = '';
        bookCancel.style.display = 'none';
        document.getElementById('bookSubmit').textContent = 'Registrar';
        clearAllErrors();
    }

    function resetSearchFilters() {
        searchTitle.value = '';
        searchAuthor.value = '';
        searchIsbn.value = '';
        searchCategory.value = '';
        searchYearFrom.value = '';
        searchYearTo.value = '';
    }

    function resetBookHistoryFilters() {
        bookHistoryDateFrom.value = '';
        bookHistoryDateTo.value = '';
        bookLoanStatus.value = 'ALL';
        bookReservationStatus.value = 'ALL';
    }

    function closeBookHistory() {
        currentHistoryBookId = null;
        bookHistorySection.style.display = 'none';
        bookHistorySummary.textContent = 'Seleccione un libro para ver su circulacion.';
        document.getElementById('bookHistoryLoanRows').innerHTML = '';
        document.getElementById('bookHistoryReservationRows').innerHTML = '';
        bookHistoryMessage.innerHTML = '';
        resetBookHistoryFilters();
    }

    bookTitle.addEventListener('input', () => {
        bookTitle.value = bookTitle.value.toUpperCase();
    });
    bookIsbn.addEventListener('keydown', (event) => {
        const allowed = ['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 'Home', 'End', '-'];
        if (!allowed.includes(event.key) && !/^\d$/.test(event.key)) {
            event.preventDefault();
        }
    });
    bookYear.addEventListener('keydown', (event) => {
        const allowed = ['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 'Home', 'End'];
        if (!allowed.includes(event.key) && !/^\d$/.test(event.key)) {
            event.preventDefault();
        }
    });
    bookYear.addEventListener('input', () => {
        if (bookYear.value.length > 4) {
            bookYear.value = bookYear.value.slice(0, 4);
        }
    });
    bookYear.setAttribute('min', '1000');
    bookYear.setAttribute('max', String(new Date().getFullYear()));

    async function loadAuthors() {
        const resp = await fetch(apiBase + '/authors');
        const authors = await resp.json();
        bookAuthor.innerHTML = '<option value="">Seleccione autor</option>';
        authors.forEach((author) => {
            const option = document.createElement('option');
            option.value = author.idAuthor;
            option.textContent = author.name;
            bookAuthor.appendChild(option);
        });
    }

    async function loadCategories() {
        const resp = await fetch(apiBase + '/categories');
        const categories = await resp.json();
        bookCategory.innerHTML = '<option value="">Seleccione categoria</option>';
        searchCategory.innerHTML = '<option value="">Categoria</option>';
        categories.forEach((category) => {
            const formOption = document.createElement('option');
            formOption.value = category.idCategory;
            formOption.textContent = category.name;
            bookCategory.appendChild(formOption);

            const searchOption = document.createElement('option');
            searchOption.value = category.idCategory;
            searchOption.textContent = category.name;
            searchCategory.appendChild(searchOption);
        });
    }

    async function loadBooks(params = '') {
        const resp = await fetch(apiBase + '/books' + params);
        const books = await resp.json();
        const tbody = document.getElementById('bookRows');
        tbody.innerHTML = '';

        if (!resp.ok) {
            showMessage(books.error || 'No se pudieron cargar los libros.', 'danger');
            return;
        }

        if (!books.length) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No se encontraron libros con los filtros seleccionados.</td></tr>';
            return;
        }

        books.forEach((book) => {
            const row = document.createElement('tr');
            row.innerHTML = ''
                + '<td>' + book.idBook + '</td>'
                + '<td>' + (book.title || '') + '</td>'
                + '<td>' + (book.isbn || '') + '</td>'
                + '<td>' + (book.year || '') + '</td>'
                + '<td>' + getBookStatusBadge(book.status) + '</td>'
                + '<td>' + (book.author && book.author.name ? book.author.name : '') + '</td>'
                + '<td>' + (book.category && book.category.name ? book.category.name : 'Sin categoria') + '</td>'
                + '<td>'
                + '    <button class="btn btn-sm btn-outline-primary me-2" type="button" data-action="edit">Editar</button>'
                + '    <button class="btn btn-sm btn-outline-dark me-2" type="button" data-action="history">Historial</button>'
                + '    <button class="btn btn-sm btn-outline-danger" type="button" data-action="delete">Eliminar</button>'
                + '</td>';
            row.querySelector('[data-action="edit"]').addEventListener('click', () => editBook(book));
            row.querySelector('[data-action="history"]').addEventListener('click', () => openBookHistory(book));
            row.querySelector('[data-action="delete"]').addEventListener('click', () => deleteBook(book.idBook));
            tbody.appendChild(row);
        });
    }

    function editBook(book) {
        bookIdInput.value = book.idBook;
        bookTitle.value = book.title || '';
        bookIsbn.value = book.isbn || '';
        bookYear.value = book.year || '';
        bookStatus.value = (book.status || 'DISPONIBLE').toUpperCase();
        bookAuthor.value = book.author && book.author.idAuthor ? book.author.idAuthor : '';
        bookCategory.value = book.category && book.category.idCategory ? book.category.idCategory : '';
        bookCancel.style.display = 'block';
        document.getElementById('bookSubmit').textContent = 'Actualizar';
        clearAllErrors();
    }

    async function saveBook(event) {
        event.preventDefault();
        if (!validateBookForm()) {
            return;
        }

        const idBook = parseInt(bookIdInput.value, 10);
        const action = idBook > 0 ? 'update' : 'create';
        const body = {
            action: action,
            idBook: idBook > 0 ? idBook : undefined,
            title: bookTitle.value.trim(),
            isbn: bookIsbn.value.trim(),
            year: parseInt(bookYear.value, 10),
            status: bookStatus.value,
            author: { idAuthor: parseInt(bookAuthor.value, 10) },
            category: { idCategory: parseInt(bookCategory.value, 10) }
        };

        const resp = await fetch(apiBase + '/books', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage(idBook > 0 ? 'Libro actualizado correctamente.' : 'Libro registrado correctamente.');
            resetForm();
            loadBooks();
        } else {
            showMessage(result.error || 'Error al guardar libro.', 'danger');
        }
    }

    async function deleteBook(id) {
        if (!confirm('Eliminar este libro?')) {
            return;
        }
        const resp = await fetch(apiBase + '/books', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: 'delete', idBook: id })
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage('Libro eliminado correctamente.');
            if (currentHistoryBookId === id) {
                closeBookHistory();
            }
            loadBooks();
        } else {
            showMessage(result.error || 'Error al eliminar libro.', 'danger');
        }
    }

    function searchBooks() {
        const params = new URLSearchParams();
        const title = searchTitle.value.trim();
        const author = searchAuthor.value.trim();
        const isbn = searchIsbn.value.trim();
        const categoryId = searchCategory.value;
        const yearFrom = searchYearFrom.value.trim();
        const yearTo = searchYearTo.value.trim();

        if (title) {
            params.set('search', title);
        }
        if (author) {
            params.set('author', author);
        }
        if (isbn) {
            params.set('isbn', isbn);
        }
        if (categoryId) {
            params.set('categoryId', categoryId);
        }
        if (yearFrom) {
            params.set('yearFrom', yearFrom);
        }
        if (yearTo) {
            params.set('yearTo', yearTo);
        }

        if (yearFrom && yearTo && parseInt(yearFrom, 10) > parseInt(yearTo, 10)) {
            showMessage('El anio inicial no puede ser mayor que el anio final.', 'warning');
            return;
        }

        loadBooks(params.toString() ? '?' + params.toString() : '');
    }

    async function openBookHistory(book) {
        currentHistoryBookId = book.idBook;
        bookHistorySection.style.display = 'block';
        bookHistorySummary.textContent = 'Cargando historial de ' + (book.title || '') + '...';
        await loadBookHistory();
        bookHistorySection.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }

    async function loadBookHistory() {
        if (!currentHistoryBookId) {
            return;
        }

        const params = new URLSearchParams({
            history: 'true',
            idBook: String(currentHistoryBookId),
            loanStatus: bookLoanStatus.value,
            reservationStatus: bookReservationStatus.value
        });
        if (bookHistoryDateFrom.value) {
            params.set('dateFrom', bookHistoryDateFrom.value);
        }
        if (bookHistoryDateTo.value) {
            params.set('dateTo', bookHistoryDateTo.value);
        }

        const resp = await fetch(apiBase + '/books?' + params.toString());
        const result = await resp.json();
        if (!resp.ok) {
            showBookHistoryMessage(result.error || 'No se pudo cargar el historial.', 'danger');
            return;
        }

        bookHistorySummary.textContent = 'Libro: ' + (result.book && result.book.title ? result.book.title : '')
            + ' | Estado actual: ' + ((result.book && result.book.status) || 'SIN ESTADO');
        renderBookHistoryLoans(result.loans || []);
        renderBookHistoryReservations(result.reservations || []);
        const loansCount = (result.loans || []).length;
        const reservationsCount = (result.reservations || []).length;
        showBookHistoryMessage('Se encontraron ' + loansCount + ' prestamos y ' + reservationsCount + ' reservas.', 'info');
    }

    function renderBookHistoryLoans(loans) {
        const tbody = document.getElementById('bookHistoryLoanRows');
        tbody.innerHTML = '';
        if (!loans.length) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay prestamos para los filtros actuales.</td></tr>';
            return;
        }
        loans.forEach((loan) => {
            const row = document.createElement('tr');
            row.innerHTML = ''
                + '<td>' + loan.idLoan + '</td>'
                + '<td>' + (loan.user && loan.user.name ? loan.user.name : '') + '</td>'
                + '<td>' + (loan.loanDate || '') + '</td>'
                + '<td>' + (loan.returnDate || '') + '</td>'
                + '<td>' + getLoanStatusBadge(loan.returned) + '</td>';
            tbody.appendChild(row);
        });
    }

    function renderBookHistoryReservations(reservations) {
        const tbody = document.getElementById('bookHistoryReservationRows');
        tbody.innerHTML = '';
        if (!reservations.length) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay reservas para los filtros actuales.</td></tr>';
            return;
        }
        reservations.forEach((reservation) => {
            const row = document.createElement('tr');
            row.innerHTML = ''
                + '<td>' + reservation.idReservation + '</td>'
                + '<td>' + (reservation.user && reservation.user.name ? reservation.user.name : '') + '</td>'
                + '<td>' + (reservation.reservationDate || '') + '</td>'
                + '<td>' + getBookStatusBadge(reservation.book && reservation.book.status ? reservation.book.status : '') + '</td>'
                + '<td>' + getReservationStatusBadge(reservation.status) + '</td>';
            tbody.appendChild(row);
        });
    }

    bookForm.addEventListener('submit', saveBook);
    document.getElementById('bookSearch').addEventListener('click', searchBooks);
    document.getElementById('bookSearchClear').addEventListener('click', () => {
        resetSearchFilters();
        loadBooks();
    });
    bookCancel.addEventListener('click', resetForm);
    document.getElementById('applyBookHistoryFilters').addEventListener('click', loadBookHistory);
    document.getElementById('resetBookHistoryFilters').addEventListener('click', () => {
        resetBookHistoryFilters();
        loadBookHistory();
    });
    document.getElementById('closeBookHistory').addEventListener('click', closeBookHistory);

    Promise.all([loadAuthors(), loadCategories()]).then(() => {
        resetForm();
        resetSearchFilters();
        loadBooks();
    });
</script>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
