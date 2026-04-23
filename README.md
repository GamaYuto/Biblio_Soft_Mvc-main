# BiblioSoft MVC

BiblioSoft MVC es una aplicacion web para la gestion de una biblioteca, desarrollada con arquitectura MVC sobre Jakarta EE. El sistema permite administrar autores, libros, usuarios de biblioteca, prestamos y cuentas administrativas desde una interfaz web protegida por autenticacion.

## Descripcion General

La aplicacion esta orientada a centralizar las operaciones principales de una biblioteca en un panel administrativo web. Su objetivo es ofrecer una base clara y funcional para registrar informacion, consultar datos y mantener control sobre prestamos y accesos al sistema.

La solucion incluye autenticacion segura con sesiones HTTP y contrasenas protegidas con BCrypt, de modo que solo administradores autorizados puedan ingresar al panel.

## Modulos Principales

- `Autores`: registro, consulta, edicion y eliminacion de autores.
- `Libros`: administracion del catalogo de libros y asociacion con autores.
- `Usuarios`: gestion de usuarios de biblioteca.
- `Prestamos`: registro y seguimiento de prestamos de libros.
- `Administradores`: creacion y mantenimiento de usuarios con acceso al sistema.
- `Autenticacion`: login, cierre de sesion y proteccion de rutas mediante filtro.

## Arquitectura

El proyecto sigue una estructura MVC clasica:

- `Model`: representa las entidades del dominio como `Author`, `Book`, `User`, `Loan` y `AdminUser`.
- `DAO`: encapsula el acceso a base de datos mediante JDBC y consultas SQL con `PreparedStatement`.
- `Controller`: coordina la logica entre servlets y capa de persistencia.
- `Servlet`: expone endpoints HTTP para cada modulo y devuelve JSON en operaciones AJAX.
- `View`: JSPs para el panel web y formularios de gestion.

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

- `create_library_db.sql`: crea la base principal con tablas funcionales del sistema.
- `src/main/resources/auth_admin_users.sql`: crea la tabla `admin_users` e inserta un administrador inicial.

Tablas funcionales principales:

- `authors`
- `books`
- `users`
- `loans`
- `admin_users`

## Ejecucion General

1. Crear la base de datos ejecutando `create_library_db.sql`.
2. Crear la tabla de administradores ejecutando `src/main/resources/auth_admin_users.sql`.
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

```text
src/main/java/com/mycompany/biblio_soft_mvc/
  controller/
  dao/
  database/
  filter/
  model/
  servlet/
  service/
  view/

src/main/webapp/
  WEB-INF/
  view/
  index.jsp
```

## Estado Actual

Actualmente la aplicacion cuenta con:

- panel administrativo funcional
- autenticacion de administradores
- gestion de administradores desde la propia interfaz
- modulos CRUD para la operacion base de la biblioteca

## Observacion

Este proyecto esta pensado como una base academica y funcional para una biblioteca bajo arquitectura MVC. Puede extenderse facilmente con reportes, roles, auditoria, busquedas avanzadas o mejoras visuales.
