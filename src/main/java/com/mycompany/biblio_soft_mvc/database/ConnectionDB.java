package com.mycompany.biblio_soft_mvc.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proporciona la conexión JDBC a la base de datos MySQL.
 * Contiene la URL de conexión, credenciales y el método para abrir la conexión.
 */
public class ConnectionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/library?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Root0612ñ";

    /**
     * Crea una conexión a la base de datos.
     * @return Connection activa si la conexión fue exitosa, o null si ocurrió un error.
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            // Cargar explicitamente el driver MySQL para garantizar compatibilidad.
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("Conexión exitosa a la base de datos.");
            System.out.println("JDBC URL: " + metaData.getURL());
            System.out.println("Nombre de usuario: " + metaData.getUserName());
            System.out.println("Base de datos actual: " + conn.getCatalog());
            System.out.println("Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el driver de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de conexión SQL: " + e.getMessage());
        }
        return conn;
    }
}
