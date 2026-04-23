# Biblio_Soft_Mvc

## Configuración de la base de datos local

Este proyecto usa MySQL para el almacenamiento de datos.

Para crear la base de datos y las tablas necesarias, ejecute el script `create_library_db.sql` en su servidor MySQL local.

### Pasos

1. Abra su cliente MySQL o MySQL Workbench.
2. Conéctese a su servidor local.
3. Ejecute el contenido de `create_library_db.sql`.

El script crea la base de datos `library` y las tablas:
- `authors`
- `users`
- `books`
- `loans`

### Notas

- La conexión desde la aplicación utiliza `jdbc:mysql://localhost:3306/library`.
- Asegúrese de que el usuario y contraseña de MySQL coincidan con la configuración del proyecto.

