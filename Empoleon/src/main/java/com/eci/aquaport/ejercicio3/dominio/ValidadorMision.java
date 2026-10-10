package com.eci.aquaport.ejercicio3.dominio;

import java.util.Optional;

/**
 * Eslabon de la cadena de validacion (Chain of Responsibility).
 * Cada eslabon revisa una sola regla; si falla, la cadena se detiene con el motivo.
 */
public abstract class ValidadorMision {

    private ValidadorMision siguiente;

    /** Enlaza el siguiente eslabon y lo retorna para encadenar: a.enlazar(b).enlazar(c). */
    public ValidadorMision enlazar(ValidadorMision siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    /** Vacio si el drone pasa toda la cadena; si no, el motivo del primer eslabon que fallo. */
    public Optional<String> validar(Drone drone, Mision mision) {
        Optional<String> rechazo = revisar(drone, mision);
        if (rechazo.isPresent() || siguiente == null) {
            return rechazo;
        }
        return siguiente.validar(drone, mision);
    }

    protected abstract Optional<String> revisar(Drone drone, Mision mision);
}
