package com.eci.aquaport.ejercicio12.dominio;

import java.util.Optional;

/** Primer eslabon de la cadena: un drone en FALLO no recibe ningun tramo. */
public class ValidadorEstadoOperativo extends ValidadorMision {

    @Override
    protected Optional<String> revisar(Drone drone, Mision mision) {
        if (drone.estado() == EstadoDrone.FALLO) {
            return Optional.of("El drone " + drone.id() + " esta en FALLO");
        }
        return Optional.empty();
    }
}
