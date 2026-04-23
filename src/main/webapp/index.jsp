<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="container-fluid">
    <div class="row">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h2 class="card-title">Dashboard</h2>
                    <p class="card-text">Bienvenido al panel de control de BiblioSoft. Usa el menu de la izquierda para gestionar autores, libros, usuarios, administradores y prestamos.</p>
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
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
