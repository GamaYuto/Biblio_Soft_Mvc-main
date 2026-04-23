<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h2 class="card-title mb-0"><i class="fa-solid fa-user-tie me-2"></i>Autores</h2>
                </div>
                <div class="card-body">
                    <form id="authorForm" class="row g-3 align-items-end">
                        <input type="hidden" id="authorId" value="" />
                        <div class="col-md-5">
                            <label for="authorName" class="form-label">Nombre</label>
                            <input type="text" id="authorName" class="form-control" required>
                        </div>
                        <div class="col-md-5">
                            <label for="authorNationality" class="form-label">Nacionalidad</label>
                            <input type="text" id="authorNationality" class="form-control" required>
                        </div>
                        <div class="col-md-2 d-grid">
                            <button type="submit" class="btn btn-primary" id="authorSubmit">Registrar</button>
                            <button type="button" class="btn btn-secondary mt-2" id="authorCancel" style="display:none;">Cancelar</button>
                        </div>
                    </form>
                    <div id="authorMessage" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h3 class="card-title mb-0">Lista de Autores</h3>
                </div>
                <div class="card-body table-responsive" style="max-height: 360px; overflow-y: auto;">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Nacionalidad</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="authorRows"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const apiBase = '<%= request.getContextPath() %>/resources';
    const authorForm = document.getElementById('authorForm');
    const authorMessage = document.getElementById('authorMessage');
    const authorIdInput = document.getElementById('authorId');
    const authorName = document.getElementById('authorName');
    const authorNationality = document.getElementById('authorNationality');
    const authorCancel = document.getElementById('authorCancel');

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
        [authorName, authorNationality].forEach(inp => {
            inp.classList.remove('is-invalid', 'is-valid');
            const fb = inp.nextElementSibling;
            if (fb && fb.classList.contains('invalid-feedback')) fb.textContent = '';
        });
    }

    function validateAuthorForm() {
        let ok = true;

        const name = authorName.value.trim();
        if (!name) {
            setError(authorName, 'El nombre es obligatorio.');
            ok = false;
        } else if (!SOLO_LETRAS.test(name)) {
            setError(authorName, 'El nombre solo puede contener letras y espacios.');
            ok = false;
        } else {
            clearError(authorName);
        }

        const nat = authorNationality.value.trim();
        if (!nat) {
            setError(authorNationality, 'La nacionalidad es obligatoria.');
            ok = false;
        } else if (!SOLO_LETRAS.test(nat)) {
            setError(authorNationality, 'La nacionalidad solo puede contener letras y espacios.');
            ok = false;
        } else {
            clearError(authorNationality);
        }

        return ok;
    }

    // ── Transformaciones en tiempo real ─────────────────────────────
    authorName.addEventListener('input', () => { authorName.value = authorName.value.toUpperCase(); });
    authorNationality.addEventListener('input', () => { authorNationality.value = authorNationality.value.toUpperCase(); });

    // ── Carga de autores ────────────────────────────────────────────
    async function loadAuthors() {
        const resp = await fetch(`${apiBase}/authors`);
        const authors = await resp.json();
        const tbody = document.getElementById('authorRows');
        tbody.innerHTML = '';
        authors.forEach(author => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${author.idAuthor}</td>
                <td>${author.name}</td>
                <td>${author.nationality}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-2" type="button">Editar</button>
                    <button class="btn btn-sm btn-outline-danger" type="button">Eliminar</button>
                </td>
            `;
            row.querySelector('button[type="button"]').addEventListener('click', () => editAuthor(author));
            row.querySelectorAll('button[type="button"]')[1].addEventListener('click', () => deleteAuthor(author.idAuthor));
            tbody.appendChild(row);
        });
    }

    function showMessage(message, type = 'success') {
        authorMessage.innerHTML = `<div class="alert alert-${type} py-2">${message}</div>`;
    }

    function resetForm() {
        authorIdInput.value = '';
        authorName.value = '';
        authorNationality.value = '';
        authorCancel.style.display = 'none';
        document.getElementById('authorSubmit').textContent = 'Registrar';
        clearAllErrors();
    }

    function editAuthor(author) {
        authorIdInput.value = author.idAuthor;
        authorName.value = author.name;
        authorNationality.value = author.nationality;
        authorCancel.style.display = 'block';
        document.getElementById('authorSubmit').textContent = 'Actualizar';
        clearAllErrors();
    }

    async function saveAuthor(event) {
        event.preventDefault();
        if (!validateAuthorForm()) return;
        const idAuthor = parseInt(authorIdInput.value, 10);
        const action = idAuthor > 0 ? 'update' : 'create';
        const body = {
            action,
            idAuthor: idAuthor > 0 ? idAuthor : undefined,
            name: authorName.value.trim(),
            nationality: authorNationality.value.trim()
        };

        const resp = await fetch(`${apiBase}/authors`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage(idAuthor > 0 ? 'Autor actualizado correctamente.' : 'Autor registrado correctamente.');
            resetForm();
            loadAuthors();
        } else {
            showMessage(result.error || 'Error al guardar autor.', 'danger');
        }
    }

    async function deleteAuthor(id) {
        if (!confirm('¿Eliminar este autor?')) return;
        const resp = await fetch(`${apiBase}/authors`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: 'delete', idAuthor: id })
        });
        const result = await resp.json();
        if (resp.ok) {
            showMessage('Autor eliminado correctamente.');
            loadAuthors();
        } else {
            showMessage(result.error || 'Error al eliminar autor.', 'danger');
        }
    }

    authorForm.addEventListener('submit', saveAuthor);
    authorCancel.addEventListener('click', resetForm);
    loadAuthors();
</script>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
