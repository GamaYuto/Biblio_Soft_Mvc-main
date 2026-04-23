<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h2 class="card-title mb-0"><i class="fa-solid fa-scroll me-2"></i>Préstamos</h2>
                </div>
                <div class="card-body">
                    <form id="loanForm" class="row g-3 align-items-end">
                        <input type="hidden" id="loanId" value="" />
                        <div class="col-md-3">
                            <label for="loanUser" class="form-label">Usuario</label>
                            <select id="loanUser" class="form-select" required></select>
                        </div>
                        <div class="col-md-3">
                            <label for="loanBook" class="form-label">Libro</label>
                            <select id="loanBook" class="form-select" required></select>
                        </div>
                        <div class="col-md-2">
                            <label for="loanDate" class="form-label">Fecha Préstamo</label>
                            <input type="date" id="loanDate" class="form-control" required>
                        </div>
                        <div class="col-md-2">
                            <label for="returnDate" class="form-label">Fecha Devolución</label>
                            <input type="date" id="returnDate" class="form-control">
                        </div>
                        <div class="col-md-2 d-grid">
                            <button type="submit" class="btn btn-info" id="loanSubmit">Registrar</button>
                            <button type="button" class="btn btn-secondary mt-2" id="loanCancel" style="display:none;">Cancelar</button>
                        </div>
                    </form>
                    <div class="row mt-4 g-2">
                        <div class="col-12 col-md-3">
                            <input type="number" id="filterUserId" class="form-control" placeholder="Filtrar por userId">
                        </div>
                        <div class="col-12 col-md-3">
                            <input type="number" id="filterBookId" class="form-control" placeholder="Filtrar por bookId">
                        </div>
                        <div class="col-12 col-md-6 d-flex flex-wrap gap-2">
                            <button type="button" class="btn btn-primary" id="loanFilter">Filtrar</button>
                            <button type="button" class="btn btn-success" id="loanActive">Préstamos Activos</button>
                            <button type="button" class="btn btn-secondary" id="loanHistory">Historial</button>
                        </div>
                    </div>
                    <div id="loanMessage" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h3 class="card-title mb-0">Tabla de Préstamos</h3>
                </div>
                <div class="card-body table-responsive" style="max-height: 360px; overflow-y: auto;">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Usuario</th>
                                <th>Libro</th>
                                <th>Fecha Préstamo</th>
                                <th>Fecha Devolución</th>
                                <th>Devuelto</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="loanRows"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const apiBase = '<%= request.getContextPath() %>/resources';
    const loanForm = document.getElementById('loanForm');
    const loanMessage = document.getElementById('loanMessage');
    const loanIdInput = document.getElementById('loanId');
    const loanUser = document.getElementById('loanUser');
    const loanBook = document.getElementById('loanBook');
    const loanDate = document.getElementById('loanDate');
    const returnDate = document.getElementById('returnDate');
    const loanCancel = document.getElementById('loanCancel');
    const filterUserId = document.getElementById('filterUserId');
    const filterBookId = document.getElementById('filterBookId');

    // ── Límite máximo de fecha de préstamo: hoy ──────────────────────
    const today = new Date().toISOString().split('T')[0];
    loanDate.setAttribute('max', today);

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
        [loanUser, loanBook, loanDate, returnDate].forEach(inp => {
            inp.classList.remove('is-invalid', 'is-valid');
            const fb = inp.nextElementSibling;
            if (fb && fb.classList.contains('invalid-feedback')) fb.textContent = '';
        });
    }

    function validateLoanForm() {
        let ok = true;

        if (!loanUser.value || parseInt(loanUser.value, 10) <= 0) {
            setError(loanUser, 'Debe seleccionar un usuario.');
            ok = false;
        } else {
            clearError(loanUser);
        }

        if (!loanBook.value || parseInt(loanBook.value, 10) <= 0) {
            setError(loanBook, 'Debe seleccionar un libro.');
            ok = false;
        } else {
            clearError(loanBook);
        }

        const ld = loanDate.value;
        if (!ld) {
            setError(loanDate, 'La fecha de préstamo es obligatoria.');
            ok = false;
        } else if (ld > today) {
            setError(loanDate, 'La fecha de préstamo no puede ser una fecha futura.');
            ok = false;
        } else {
            clearError(loanDate);
        }

        const rd = returnDate.value;
        if (rd) {
            if (ld && rd < ld) {
                setError(returnDate, 'La fecha de devolución no puede ser menor a la fecha de préstamo.');
                ok = false;
            } else {
                clearError(returnDate);
            }
        } else {
            returnDate.classList.remove('is-invalid', 'is-valid');
        }

        return ok;
    }

    // ── Validación dinámica de fechas ────────────────────────────────
    loanDate.addEventListener('change', () => {
        const ld = loanDate.value;
        if (ld) returnDate.setAttribute('min', ld);
        if (returnDate.value && returnDate.value < ld) {
            setError(returnDate, 'La fecha de devolución no puede ser menor a la fecha de préstamo.');
        } else {
            returnDate.classList.remove('is-invalid');
        }
    });

    returnDate.addEventListener('change', () => {
        const ld = loanDate.value;
        const rd = returnDate.value;
        if (ld && rd && rd < ld) {
            setError(returnDate, 'La fecha de devolución no puede ser menor a la fecha de préstamo.');
        } else if (rd) {
            clearError(returnDate);
        }
    });

    // ── Carga de datos ───────────────────────────────────────────────
    async function loadUsers() {
        const resp = await fetch(`${apiBase}/users`);
        const users = await resp.json();
        loanUser.innerHTML = '<option value="">Seleccione usuario</option>';
        users.forEach(user => {
            const option = document.createElement('option');
            option.value = user.idUser;
            option.textContent = `${user.name} (${user.email})`;
            loanUser.appendChild(option);
        });
    }

    async function loadBooks() {
        const resp = await fetch(`${apiBase}/books`);
        const books = await resp.json();
        loanBook.innerHTML = '<option value="">Seleccione libro</option>';
        books.forEach(book => {
            const option = document.createElement('option');
            option.value = book.idBook;
            option.textContent = `${book.title} - ${book.author?.name || ''}`;
            loanBook.appendChild(option);
        });
    }

    async function loadLoans(params = '') {
        const resp = await fetch(`${apiBase}/loans${params}`);
        const loans = await resp.json();
        renderLoans(loans);
    }

    function renderLoans(loans) {
        const tbody = document.getElementById('loanRows');
        tbody.innerHTML = '';
        loans.forEach(loan => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${loan.idLoan}</td>
                <td>${loan.user?.name || ''}</td>
                <td>${loan.book?.title || ''}</td>
                <td>${loan.loanDate || ''}</td>
                <td>${loan.returnDate || ''}</td>
                <td>${loan.returned ? 'Sí' : 'No'}</td>
                <td></td>
            `;
            const actionsCell = row.querySelector('td:last-child');
            if (!loan.returned) {
                const editBtn = document.createElement('button');
                editBtn.type = 'button';
                editBtn.className = 'btn btn-sm btn-outline-primary me-2';
                editBtn.textContent = 'Editar';
                editBtn.addEventListener('click', () => editLoan(loan));
                const returnBtn = document.createElement('button');
                returnBtn.type = 'button';
                returnBtn.className = 'btn btn-sm btn-outline-success';
                returnBtn.textContent = 'Devolver';
                returnBtn.addEventListener('click', () => returnLoan(loan.idLoan));
                actionsCell.appendChild(editBtn);
                actionsCell.appendChild(returnBtn);
            } else {
                actionsCell.innerHTML = '<span class="badge bg-secondary">Devuelto</span>';
            }
            tbody.appendChild(row);
        });
    }

    function showMessage(message, type = 'success') {
        loanMessage.innerHTML = `<div class="alert alert-${type} py-2">${message}</div>`;
    }

    function resetForm() {
        loanIdInput.value = '';
        loanUser.value = '';
        loanBook.value = '';
        loanDate.value = '';
        returnDate.value = '';
        loanCancel.style.display = 'none';
        document.getElementById('loanSubmit').textContent = 'Registrar';
        clearAllErrors();
    }

    function editLoan(loan) {
        loanIdInput.value = loan.idLoan;
        loanUser.value = loan.user?.idUser || '';
        loanBook.value = loan.book?.idBook || '';
        loanDate.value = loan.loanDate || '';
        returnDate.value = loan.returnDate || '';
        loanCancel.style.display = 'block';
        document.getElementById('loanSubmit').textContent = 'Actualizar';
        clearAllErrors();
        if (loanDate.value) returnDate.setAttribute('min', loanDate.value);
    }

    async function saveLoan(event) {
        event.preventDefault();
        if (!validateLoanForm()) return;
        const idLoan = parseInt(loanIdInput.value, 10);
        const action = idLoan > 0 ? 'update' : 'create';
        const body = {
            action,
            idLoan: idLoan > 0 ? idLoan : undefined,
            loanDate: loanDate.value,
            returnDate: returnDate.value || undefined,
            user: { idUser: parseInt(loanUser.value, 10) },
            book: { idBook: parseInt(loanBook.value, 10) }
        };

        const resp = await fetch(`${apiBase}/loans`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage(idLoan > 0 ? 'Préstamo actualizado correctamente.' : 'Préstamo registrado correctamente.');
            resetForm();
            loadLoans();
        } else {
            showMessage(result.error || 'Error al guardar préstamo.', 'danger');
        }
    }

    async function returnLoan(idLoan) {
        if (!confirm('Marcar préstamo como devuelto?')) return;
        const resp = await fetch(`${apiBase}/loans`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: 'return', idLoan })
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage('Préstamo marcado como devuelto.');
            loadLoans();
        } else {
            showMessage(result.error || 'Error al devolver préstamo.', 'danger');
        }
    }

    function filterLoans() {
        const userId = filterUserId.value.trim();
        const bookId = filterBookId.value.trim();
        if (userId) {
            loadLoans(`?userId=${encodeURIComponent(userId)}`);
        } else if (bookId) {
            loadLoans(`?bookId=${encodeURIComponent(bookId)}`);
        } else {
            showMessage('Ingrese userId o bookId para filtrar.', 'warning');
        }
    }

    loanForm.addEventListener('submit', saveLoan);
    loanCancel.addEventListener('click', resetForm);
    document.getElementById('loanFilter').addEventListener('click', filterLoans);
    document.getElementById('loanActive').addEventListener('click', () => loadLoans());
    document.getElementById('loanHistory').addEventListener('click', () => loadLoans('?history=true'));

    Promise.all([loadUsers(), loadBooks()]).then(() => loadLoans());
</script>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
