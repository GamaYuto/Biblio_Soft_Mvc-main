<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col-12">
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <div class="d-flex flex-column flex-lg-row justify-content-between align-items-lg-center gap-3">
                        <div>
                            <h2 class="card-title mb-2">Dashboard</h2>
                            <p class="card-text mb-0">Bienvenido al panel de control de BiblioSoft. Usa el menu de la izquierda para gestionar autores, libros, usuarios, administradores, prestamos y reservas.</p>
                        </div>
                        <div class="text-muted small">
                            <i class="fa-solid fa-chart-line me-2"></i>Resumen operativo en tiempo real
                        </div>
                    </div>
                    <div id="dashboardMessage" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-3 mb-4" id="dashboardCards">
        <div class="col-sm-6 col-xl-3">
            <div class="card shadow-sm border-primary h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <div class="text-muted small text-uppercase">Libros prestados</div>
                            <div class="display-6 fw-semibold" id="metricBorrowedBooks">--</div>
                        </div>
                        <span class="badge bg-primary-subtle text-primary"><i class="fa-solid fa-book-open"></i></span>
                    </div>
                    <p class="text-muted mb-0 mt-3 small">Prestamos activos actualmente.</p>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-xl-3">
            <div class="card shadow-sm border-danger h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <div class="text-muted small text-uppercase">Devoluciones pendientes</div>
                            <div class="display-6 fw-semibold" id="metricPendingReturns">--</div>
                        </div>
                        <span class="badge bg-danger-subtle text-danger"><i class="fa-solid fa-hourglass-half"></i></span>
                    </div>
                    <p class="text-muted mb-0 mt-3 small" id="metricPendingReturnsDetail">Prestamos aun sin registrar devolucion.</p>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-xl-3">
            <div class="card shadow-sm border-success h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <div class="text-muted small text-uppercase">Usuarios activos</div>
                            <div class="display-6 fw-semibold" id="metricActiveUsers">--</div>
                        </div>
                        <span class="badge bg-success-subtle text-success"><i class="fa-solid fa-users"></i></span>
                    </div>
                    <p class="text-muted mb-0 mt-3 small" id="metricActiveUsersDetail">Usuarios con movimientos recientes.</p>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-xl-3">
            <div class="card shadow-sm border-warning h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <div class="text-muted small text-uppercase">Autor lider</div>
                            <div class="h4 fw-semibold mb-0" id="metricTopAuthor">--</div>
                        </div>
                        <span class="badge bg-warning-subtle text-warning"><i class="fa-solid fa-medal"></i></span>
                    </div>
                    <p class="text-muted mb-0 mt-3 small" id="metricTopAuthorDetail">Autor con mayor volumen historico de prestamos.</p>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-3 mb-4">
        <div class="col-lg-6">
            <div class="card shadow-sm h-100">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h3 class="card-title h6 mb-0"><i class="fa-solid fa-ranking-star me-2"></i>Autores mas consultados</h3>
                    <span class="badge text-bg-light">Top 5</span>
                </div>
                <div class="card-body table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>#</th>
                                <th>Autor</th>
                                <th>Nacionalidad</th>
                                <th>Prestamos</th>
                            </tr>
                        </thead>
                        <tbody id="topAuthorsRows">
                            <tr><td colspan="4" class="text-center text-muted">Cargando metricas...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="col-lg-6">
            <div class="card shadow-sm h-100">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h3 class="card-title h6 mb-0"><i class="fa-solid fa-user-clock me-2"></i>Usuarios activos recientes</h3>
                    <span class="badge text-bg-light" id="activeUsersWindowLabel">Ultimos -- dias</span>
                </div>
                <div class="card-body table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Usuario</th>
                                <th>Prestamos</th>
                                <th>Reservas</th>
                                <th>Ultima actividad</th>
                            </tr>
                        </thead>
                        <tbody id="activeUsersRows">
                            <tr><td colspan="4" class="text-center text-muted">Cargando metricas...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h3 class="card-title h5">Accesos rapidos</h3>
                    <p class="card-text">Atajos a los modulos principales del sistema.</p>
                    <div class="row gx-3">
                        <div class="col-md-3 mb-3">
                            <div class="card border-primary h-100">
                                <div class="card-body">
                                    <h5 class="card-title"><i class="fa-solid fa-user-tie me-2"></i>Autores</h5>
                                    <p class="card-text">Registrar y administrar autores.</p>
                                    <a href="view/authors.jsp" class="btn btn-primary btn-sm">Ir a Autores</a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card border-success h-100">
                                <div class="card-body">
                                    <h5 class="card-title"><i class="fa-solid fa-book me-2"></i>Libros</h5>
                                    <p class="card-text">Registrar, buscar y administrar libros.</p>
                                    <a href="view/books.jsp" class="btn btn-success btn-sm">Ir a Libros</a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card border-warning h-100">
                                <div class="card-body">
                                    <h5 class="card-title"><i class="fa-solid fa-users me-2"></i>Usuarios</h5>
                                    <p class="card-text">Registrar y administrar usuarios.</p>
                                    <a href="view/users.jsp" class="btn btn-warning btn-sm">Ir a Usuarios</a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card border-info h-100">
                                <div class="card-body">
                                    <h5 class="card-title"><i class="fa-solid fa-user-shield me-2"></i>Administradores</h5>
                                    <p class="card-text">Crear y mantener accesos del panel.</p>
                                    <a href="view/admin-users.jsp" class="btn btn-info btn-sm text-white">Ir a Admins</a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card border-dark h-100">
                                <div class="card-body">
                                    <h5 class="card-title"><i class="fa-solid fa-scroll me-2"></i>Prestamos</h5>
                                    <p class="card-text">Registrar prestamos y ver historial.</p>
                                    <a href="view/loans.jsp" class="btn btn-dark btn-sm">Ir a Prestamos</a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card border-secondary h-100">
                                <div class="card-body">
                                    <h5 class="card-title"><i class="fa-solid fa-bookmark me-2"></i>Reservas</h5>
                                    <p class="card-text">Gestionar reservas activas e historicas de libros.</p>
                                    <a href="view/reservations.jsp" class="btn btn-secondary btn-sm">Ir a Reservas</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const dashboardApi = '<%= request.getContextPath() %>/resources/dashboard-metrics';

    function setDashboardMessage(message, type = 'info') {
        document.getElementById('dashboardMessage').innerHTML = '<div class="alert alert-' + type + ' py-2 mb-0">' + message + '</div>';
    }

    function renderTopAuthors(authors) {
        const tbody = document.getElementById('topAuthorsRows');
        tbody.innerHTML = '';
        if (!authors || !authors.length) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">Aun no hay prestamos suficientes para generar ranking.</td></tr>';
            return;
        }

        authors.forEach((author, index) => {
            const row = document.createElement('tr');
            row.innerHTML = ''
                + '<td>' + (index + 1) + '</td>'
                + '<td>' + (author.authorName || '') + '</td>'
                + '<td>' + (author.nationality || '-') + '</td>'
                + '<td><span class="badge text-bg-primary">' + (author.totalLoans || 0) + '</span></td>';
            tbody.appendChild(row);
        });
    }

    function renderActiveUsers(users) {
        const tbody = document.getElementById('activeUsersRows');
        tbody.innerHTML = '';
        if (!users || !users.length) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No se registran usuarios con movimientos recientes.</td></tr>';
            return;
        }

        users.forEach((user) => {
            const row = document.createElement('tr');
            row.innerHTML = ''
                + '<td><div class="fw-semibold">' + (user.userName || '') + '</div><div class="small text-muted">' + (user.email || '') + '</div></td>'
                + '<td>' + (user.recentLoans || 0) + '</td>'
                + '<td>' + (user.recentReservations || 0) + '</td>'
                + '<td>' + (user.lastActivityDate || '-') + '</td>';
            tbody.appendChild(row);
        });
    }

    function renderMetrics(metrics) {
        document.getElementById('metricBorrowedBooks').textContent = metrics.borrowedBooks ?? 0;
        document.getElementById('metricPendingReturns').textContent = metrics.pendingReturns ?? 0;
        document.getElementById('metricPendingReturnsDetail').textContent = 'Prestamos activos sin devolucion. Vencidos: ' + (metrics.overdueReturns ?? 0) + '.';
        document.getElementById('metricActiveUsers').textContent = metrics.activeUsers ?? 0;
        document.getElementById('metricActiveUsersDetail').textContent = 'Usuarios con prestamos o reservas en los ultimos ' + (metrics.recentWindowDays ?? 0) + ' dias.';
        document.getElementById('activeUsersWindowLabel').textContent = 'Ultimos ' + (metrics.recentWindowDays ?? 0) + ' dias';

        const topAuthor = metrics.topAuthors && metrics.topAuthors.length ? metrics.topAuthors[0] : null;
        document.getElementById('metricTopAuthor').textContent = topAuthor ? topAuthor.authorName : 'Sin datos';
        document.getElementById('metricTopAuthorDetail').textContent = topAuthor
            ? 'Acumula ' + topAuthor.totalLoans + ' prestamos historicos.'
            : 'Autor con mayor volumen historico de prestamos.';

        renderTopAuthors(metrics.topAuthors || []);
        renderActiveUsers(metrics.activeUsersDetail || []);
        setDashboardMessage('Metricas cargadas correctamente.', 'success');
    }

    async function loadDashboardMetrics() {
        try {
            const response = await fetch(dashboardApi);
            const result = await response.json();
            if (!response.ok) {
                setDashboardMessage(result.error || 'No se pudieron cargar las metricas del dashboard.', 'danger');
                return;
            }
            renderMetrics(result);
        } catch (error) {
            setDashboardMessage('No se pudo conectar con el servicio de metricas.', 'danger');
        }
    }

    loadDashboardMetrics();
</script>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
