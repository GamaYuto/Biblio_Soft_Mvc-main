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
                            <label for="bookTitle" class="form-label">Título</label>
                            <input type="text" id="bookTitle" class="form-control" required>
                        </div>
                        <div class="col-md-3">
                            <label for="bookIsbn" class="form-label">ISBN</label>
                            <input type="text" id="bookIsbn" class="form-control" required>
                        </div>
                        <div class="col-md-2">
                            <label for="bookYear" class="form-label">Año</label>
                            <input type="number" id="bookYear" class="form-control" required>
                        </div>
                        <div class="col-md-3">
                            <label for="bookAuthor" class="form-label">Autor</label>
                            <select id="bookAuthor" class="form-select" required></select>
                        </div>
                        <div class="col-md-12 d-grid">
                            <button type="submit" class="btn btn-success" id="bookSubmit">Registrar</button>
                            <button type="button" class="btn btn-secondary mt-2" id="bookCancel" style="display:none;">Cancelar</button>
                        </div>
                    </form>
                    <div class="row mt-4 g-2 align-items-end">
                        <div class="col-12 col-md-4">
                            <div class="input-group">
                                <span class="input-group-text">Buscar</span>
                                <input type="text" id="searchTitle" class="form-control" placeholder="Título">
                            </div>
                        </div>
                        <div class="col-12 col-md-4">
                            <div class="input-group">
                                <span class="input-group-text">Autor</span>
                                <input type="text" id="searchAuthor" class="form-control" placeholder="Nombre autor">
                            </div>
                        </div>
                        <div class="col-12 col-md-3">
                            <div class="input-group">
                                <span class="input-group-text">ISBN</span>
                                <input type="text" id="searchIsbn" class="form-control" placeholder="ISBN">
                            </div>
                        </div>
                        <div class="col-12 col-md-1 d-grid">
                            <button type="button" class="btn btn-primary" id="bookSearch">Buscar</button>
                        </div>
                    </div>
                    <div id="bookMessage" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
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
                                <th>Título</th>
                                <th>ISBN</th>
                                <th>Año</th>
                                <th>Autor</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="bookRows"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const apiBase = '<%= request.getContextPath() %>/resources';
    const bookForm = document.getElementById('bookForm');
    const bookMessage = document.getElementById('bookMessage');
    const bookIdInput = document.getElementById('bookId');
    const bookTitle = document.getElementById('bookTitle');
    const bookIsbn = document.getElementById('bookIsbn');
    const bookYear = document.getElementById('bookYear');
    const bookAuthor = document.getElementById('bookAuthor');
    const bookCancel = document.getElementById('bookCancel');
    const searchTitle = document.getElementById('searchTitle');
    const searchAuthor = document.getElementById('searchAuthor');
    const searchIsbn = document.getElementById('searchIsbn');

    // ── Utilidades de validación ────────────────────────────────────
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
        [bookTitle, bookIsbn, bookYear, bookAuthor].forEach(inp => {
            inp.classList.remove('is-invalid', 'is-valid');
            const fb = inp.nextElementSibling;
            if (fb && fb.classList.contains('invalid-feedback')) fb.textContent = '';
        });
    }

    function validateBookForm() {
        let ok = true;
        const currentYear = new Date().getFullYear();

        const title = bookTitle.value.trim();
        if (!title) {
            setError(bookTitle, 'El título del libro es obligatorio.');
            ok = false;
        } else {
            clearError(bookTitle);
        }

        const isbn = bookIsbn.value.trim();
        if (!isbn) {
            setError(bookIsbn, 'El ISBN es obligatorio.');
            ok = false;
        } else if (!/^[0-9\-]+$/.test(isbn)) {
            setError(bookIsbn, 'El ISBN solo puede contener dígitos y guiones.');
            ok = false;
        } else {
            clearError(bookIsbn);
        }

        const yearVal = bookYear.value.trim();
        const year = parseInt(yearVal, 10);
        if (!yearVal || isNaN(year)) {
            setError(bookYear, 'El año es obligatorio.');
            ok = false;
        } else if (year < 1000 || year > currentYear) {
            setError(bookYear, `El año debe estar entre 1000 y ${currentYear}.`);
            ok = false;
        } else {
            clearError(bookYear);
        }

        if (!bookAuthor.value || parseInt(bookAuthor.value, 10) <= 0) {
            setError(bookAuthor, 'Debe seleccionar un autor.');
            ok = false;
        } else {
            clearError(bookAuthor);
        }

        return ok;
    }

    // ── Transformaciones en tiempo real ─────────────────────────────
    bookTitle.addEventListener('input', () => { bookTitle.value = bookTitle.value.toUpperCase(); });
    bookIsbn.addEventListener('keydown', e => {
        const allowed = ['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 'Home', 'End', '-'];
        if (!allowed.includes(e.key) && !/^\d$/.test(e.key)) e.preventDefault();
    });
    bookYear.addEventListener('keydown', e => {
        const allowed = ['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 'Home', 'End'];
        if (!allowed.includes(e.key) && !/^\d$/.test(e.key)) e.preventDefault();
    });
    bookYear.addEventListener('input', () => {
        if (bookYear.value.length > 4) bookYear.value = bookYear.value.slice(0, 4);
    });
    bookYear.setAttribute('min', '1000');
    bookYear.setAttribute('max', new Date().getFullYear());

    // ── Carga de datos ──────────────────────────────────────────────
    async function loadAuthors() {
        const resp = await fetch(`${apiBase}/authors`);
        const authors = await resp.json();
        bookAuthor.innerHTML = '<option value="">Seleccione autor</option>';
        authors.forEach(author => {
            const option = document.createElement('option');
            option.value = author.idAuthor;
            option.textContent = author.name;
            bookAuthor.appendChild(option);
        });
    }

    async function loadBooks(params = '') {
        const resp = await fetch(`${apiBase}/books${params}`);
        const books = await resp.json();
        const tbody = document.getElementById('bookRows');
        tbody.innerHTML = '';
        books.forEach(book => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${book.idBook}</td>
                <td>${book.title}</td>
                <td>${book.isbn}</td>
                <td>${book.year}</td>
                <td>${book.author?.name || ''}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-2" type="button">Editar</button>
                    <button class="btn btn-sm btn-outline-danger" type="button">Eliminar</button>
                </td>
            `;
            row.querySelector('button[type="button"]').addEventListener('click', () => editBook(book));
            row.querySelectorAll('button[type="button"]')[1].addEventListener('click', () => deleteBook(book.idBook));
            tbody.appendChild(row);
        });
    }

    function showMessage(message, type = 'success') {
        bookMessage.innerHTML = `<div class="alert alert-${type} py-2">${message}</div>`;
    }

    function resetForm() {
        bookIdInput.value = '';
        bookTitle.value = '';
        bookIsbn.value = '';
        bookYear.value = '';
        bookAuthor.value = '';
        bookCancel.style.display = 'none';
        document.getElementById('bookSubmit').textContent = 'Registrar';
        clearAllErrors();
    }

    function editBook(book) {
        bookIdInput.value = book.idBook;
        bookTitle.value = book.title;
        bookIsbn.value = book.isbn;
        bookYear.value = book.year;
        bookAuthor.value = book.author?.idAuthor || '';
        bookCancel.style.display = 'block';
        document.getElementById('bookSubmit').textContent = 'Actualizar';
        clearAllErrors();
    }

    async function saveBook(event) {
        event.preventDefault();
        if (!validateBookForm()) return;
        const idBook = parseInt(bookIdInput.value, 10);
        const action = idBook > 0 ? 'update' : 'create';
        const body = {
            action,
            idBook: idBook > 0 ? idBook : undefined,
            title: bookTitle.value.trim(),
            isbn: bookIsbn.value.trim(),
            year: parseInt(bookYear.value, 10),
            author: { idAuthor: parseInt(bookAuthor.value, 10) }
        };
        const resp = await fetch(`${apiBase}/books`, {
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
        if (!confirm('¿Eliminar este libro?')) return;
        const resp = await fetch(`${apiBase}/books`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: 'delete', idBook: id })
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage('Libro eliminado correctamente.');
            loadBooks();
        } else {
            showMessage(result.error || 'Error al eliminar libro.', 'danger');
        }
    }

    function searchBooks() {
        const title = searchTitle.value.trim();
        const author = searchAuthor.value.trim();
        const isbn = searchIsbn.value.trim();
        let params = '';
        if (title) params = `?search=${encodeURIComponent(title)}`;
        else if (author) params = `?author=${encodeURIComponent(author)}`;
        else if (isbn) params = `?isbn=${encodeURIComponent(isbn)}`;
        loadBooks(params);
    }

    bookForm.addEventListener('submit', saveBook);
    document.getElementById('bookSearch').addEventListener('click', searchBooks);
    bookCancel.addEventListener('click', resetForm);
    loadAuthors().then(() => loadBooks());
</script>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
