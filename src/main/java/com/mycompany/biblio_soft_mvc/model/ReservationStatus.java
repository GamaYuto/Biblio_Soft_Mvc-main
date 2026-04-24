package com.mycompany.biblio_soft_mvc.model;

/**
 * Estados permitidos para una reserva de libro fisico.
 */
public enum ReservationStatus {
    ACTIVA,
    ATENDIDA,
    CANCELADA;

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            ReservationStatus.valueOf(value.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static String normalize(String value) {
        return isValid(value) ? value.trim().toUpperCase() : ACTIVA.name();
    }
}
