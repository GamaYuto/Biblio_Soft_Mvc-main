package com.mycompany.biblio_soft_mvc.model;

/**
 * Estados operativos permitidos para un libro fisico.
 */
public enum BookStatus {
    DISPONIBLE,
    PRESTADO,
    RESERVADO,
    PERDIDO,
    MANTENIMIENTO;

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            BookStatus.valueOf(value.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static String normalize(String value) {
        return isValid(value) ? value.trim().toUpperCase() : DISPONIBLE.name();
    }
}
