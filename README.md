# BiblioSoft MVC

BiblioSoft MVC es una aplicacion web para la gestion de una biblioteca, desarrollada con arquitectura MVC sobre Jakarta EE. El sistema permite administrar autores, categorias, libros, usuarios de biblioteca, prestamos, reservas y cuentas administrativas desde una interfaz web protegida por autenticacion.

## Descripcion General

La aplicacion esta orientada a centralizar las operaciones principales de una biblioteca en un panel administrativo web. Su objetivo es ofrecer una base clara y funcional para registrar informacion, consultar datos, mantener control sobre prestamos y reservas, y visualizar metricas operativas del sistema.

La solucion incluye autenticacion segura con sesiones HTTP y contrasenas protegidas con BCrypt, de modo que solo administradores autorizados puedan ingresar al panel.

## Modulos Principales

- `Autores`: registro, consulta, edicion y eliminacion de autores.
- `Categorias`: clasificacion del catalogo de libros y soporte para filtros avanzados.
- `Libros`: administracion del catalogo, asociacion con autores y categorias, busqueda avanzada e historial del libro.
- `Usuarios`: gestion de usuarios de biblioteca e historial de actividad.
- `Prestamos`: registro, actualizacion, devolucion y seguimiento de prestamos activos e historicos.
- `Reservas`: gestion de reservas activas e historicas de libros prestados o reservados.
- `Dashboard`: tarjetas resumen y rankings con metricas operativas.
- `Administradores`: creacion y mantenimiento de usuarios con acceso al sistema.
- `Autenticacion`: login, cierre de sesion y proteccion de rutas mediante filtro.

## Funcionalidades Implementadas

- CRUD completo de autores, libros, usuarios y administradores.
- Registro de prestamos con validaciones de disponibilidad del libro.
- Devolucion de libros con sincronizacion automatica del estado del ejemplar.
- Registro y atencion de reservas.
- Historial por usuario y por libro con filtros por fecha y estado.
- Busqueda avanzada de libros por:
  - titulo
  - autor
  - ISBN
  - categoria
  - rango de anios
- Dashboard principal con metricas de:
  - libros prestados
  - devoluciones pendientes
  - usuarios activos recientes
  - autores mas consultados

## Arquitectura

El proyecto sigue una estructura MVC clasica:

- `Model`: representa las entidades del dominio como `Author`, `Category`, `Book`, `User`, `Loan`, `Reservation` y `AdminUser`, ademas de modelos de respuesta para historial y dashboard.
- `DAO`: encapsula el acceso a base de datos mediante JDBC y consultas SQL con `PreparedStatement`.
- `Controller`: coordina la logica entre servlets y capa de persistencia.
- `Servlet`: expone endpoints HTTP para cada modulo y devuelve JSON en operaciones AJAX.
- `View`: JSPs para el panel web, dashboard y formularios de gestion.

## Tecnologias Utilizadas

- Java 21
- Jakarta EE 10
- JSP y Servlets
- Maven
- MySQL
- JDBC
- Gson
- jBCrypt
- Bootstrap 5
- Font Awesome
- GlassFish 7

## Seguridad

El acceso al panel esta protegido mediante:

- login de administradores
- sesiones HTTP
- filtro de autenticacion para restringir rutas protegidas
- almacenamiento de contrasenas con hash BCrypt
- exclusion de datos sensibles en sesion

## Base de Datos

La aplicacion utiliza una base de datos MySQL llamada `library`.

Scripts relevantes:

- `create_library_db.sql`: crea la base principal con tablas funcionales, relaciones y datos de prueba.
- `migration.sql`: agrega campos y estructuras necesarias para evolucionar esquemas existentes, incluyendo reservas y categorias.
- `src/main/resources/auth_admin_users.sql`: crea la tabla `admin_users` e inserta un administrador inicial.

Tablas funcionales principales:

- `authors`
- `categories`
- `books`
- `users`
- `loans`
- `reservations`
- `admin_users`

Relaciones destacadas:

- `books.id_author -> authors.id_author`
- `books.id_category -> categories.id_category`
- `loans.id_user -> users.id_user`
- `loans.id_book -> books.id_book`
- `reservations.id_user -> users.id_user`
- `reservations.id_book -> books.id_book`

## Endpoints Principales

Recursos expuestos por la aplicacion:

- `/resources/authors`
- `/resources/categories`
- `/resources/books`
- `/resources/users`
- `/resources/loans`
- `/resources/reservations`
- `/resources/dashboard-metrics`

## Vistas Principales

- `index.jsp`: dashboard principal con metricas y accesos rapidos.
- `view/authors.jsp`: gestion de autores.
- `view/books.jsp`: gestion de libros, categorias, busqueda avanzada e historial del libro.
- `view/users.jsp`: gestion de usuarios e historial del usuario.
- `view/loans.jsp`: gestion de prestamos.
- `view/reservations.jsp`: gestion de reservas.
- `view/admin-users.jsp`: administracion de cuentas del panel.
- `view/login.jsp`: acceso al sistema.

## Ejecucion General

1. Crear la base de datos ejecutando `create_library_db.sql`.
2. Si ya existe una base previa, aplicar `migration.sql`.
3. Ajustar en `ConnectionDB.java` las credenciales de MySQL si es necesario.
4. Compilar y empaquetar el proyecto con Maven o ejecutar desde NetBeans.
5. Desplegar el WAR en GlassFish 7.
6. Ingresar al sistema desde la vista de login.

## Acceso Inicial

Si se usa el script incluido para administradores, el acceso inicial es:

- Usuario: `admin`
- Clave: `Admin12345!`

Se recomienda cambiar o reemplazar este usuario en ambientes reales.

## Estructura General del Proyecto
**Generated:** 24/4/2026
```
├── 📁 .github
│   └── 📁 java-upgrade
│       ├── 📁 20260414000935
│       │   ├── 📁 logs
│       │   ├── 📝 plan.md
│       │   └── 📝 progress.md
│       ├── 📁 hooks
│       │   └── 📁 scripts
│       │       ├── 📄 recordToolUse.ps1
│       │       └── 📄 recordToolUse.sh
│       └── ⚙️ .gitignore
├── 📁 src
│   └── 📁 main
│       ├── 📁 java
│       │   └── 📁 com
│       │       └── 📁 mycompany
│       │           └── 📁 biblio_soft_mvc
│       │               ├── 📁 controller
│       │               │   ├── ☕ AdminUserController.java
│       │               │   ├── ☕ AuthorController.java
│       │               │   ├── ☕ BookController.java
│       │               │   ├── ☕ CategoryController.java
│       │               │   ├── ☕ DashboardController.java
│       │               │   ├── ☕ HistoryController.java
│       │               │   ├── ☕ LoanController.java
│       │               │   ├── ☕ ReservationController.java
│       │               │   └── ☕ UserController.java
│       │               ├── 📁 dao
│       │               │   ├── ☕ AdminUserDAO.java
│       │               │   ├── ☕ AuthorDAO.java
│       │               │   ├── ☕ BookDAO.java
│       │               │   ├── ☕ CategoryDAO.java
│       │               │   ├── ☕ DashboardDAO.java
│       │               │   ├── ☕ LoanDAO.java
│       │               │   ├── ☕ ReservationDAO.java
│       │               │   └── ☕ UserDAO.java
│       │               ├── 📁 database
│       │               │   └── ☕ ConnectionDB.java
│       │               ├── 📁 filter
│       │               │   └── ☕ AuthFilter.java
│       │               ├── 📁 model
│       │               │   ├── ☕ AdminUser.java
│       │               │   ├── ☕ Author.java
│       │               │   ├── ☕ AuthorMetric.java
│       │               │   ├── ☕ Book.java
│       │               │   ├── ☕ BookHistory.java
│       │               │   ├── ☕ BookStatus.java
│       │               │   ├── ☕ Category.java
│       │               │   ├── ☕ DashboardMetrics.java
│       │               │   ├── ☕ Loan.java
│       │               │   ├── ☕ Reservation.java
│       │               │   ├── ☕ ReservationStatus.java
│       │               │   ├── ☕ User.java
│       │               │   ├── ☕ UserActivityMetric.java
│       │               │   └── ☕ UserHistory.java
│       │               ├── 📁 service
│       │               │   └── ☕ AuthService.java
│       │               ├── 📁 servlet
│       │               │   ├── ☕ AdminUserServlet.java
│       │               │   ├── ☕ AuthorServlet.java
│       │               │   ├── ☕ BookServlet.java
│       │               │   ├── ☕ CategoryServlet.java
│       │               │   ├── ☕ DashboardServlet.java
│       │               │   ├── ☕ DbInfoServlet.java
│       │               │   ├── ☕ LoanServlet.java
│       │               │   ├── ☕ LoginServlet.java
│       │               │   ├── ☕ LogoutServlet.java
│       │               │   ├── ☕ ReservationServlet.java
│       │               │   └── ☕ UserServlet.java
│       │               ├── 📁 test
│       │               │   ├── ☕ MainTest.java
│       │               │   └── ☕ ServiceTest.java
│       │               └── 📁 view
│       │                   └── ☕ ViewPaths.java
│       ├── 📁 resources
│       │   ├── 📁 META-INF
│       │   │   └── ⚙️ persistence.xml
│       │   └── 📄 auth_admin_users.sql
│       └── 📁 webapp
│           ├── 📁 WEB-INF
│           │   ├── 📁 jsp
│           │   │   ├── 📄 footer.jsp
│           │   │   └── 📄 header.jsp
│           │   ├── ⚙️ beans.xml
│           │   ├── ⚙️ glassfish-web.xml
│           │   └── ⚙️ web.xml
│           ├── 📁 view
│           │   ├── 📄 admin-users.jsp
│           │   ├── 📄 authors.jsp
│           │   ├── 📄 books.jsp
│           │   ├── 📄 loans.jsp
│           │   ├── 📄 login.jsp
│           │   ├── 📄 reservations.jsp
│           │   └── 📄 users.jsp
│           ├── 🌐 index.html
│           └── 📄 index.jsp
├── ⚙️ .gitignore
├── 📝 README.md
├── 📄 create_library_db.sql
├── 📄 migration.sql
├── ⚙️ nb-configuration.xml
└── ⚙️ pom.xml
```

---
*Generated by FileTree Pro Extension*

## Estado Actual

Actualmente la aplicacion cuenta con:

- panel administrativo funcional
- autenticacion de administradores
- dashboard con metricas operativas
- gestion de administradores desde la propia interfaz
- modulos CRUD para autores, libros y usuarios
- modulo de reservas
- historial de prestamos y reservas por usuario y libro
- busqueda avanzada del catalogo fisico

## Observacion

Este proyecto esta pensado como una base academica y funcional para una biblioteca bajo arquitectura MVC. Puede extenderse facilmente con reportes mas avanzados, notificaciones, auditoria, exportacion de datos, roles adicionales o dashboards con mas indicadores.
