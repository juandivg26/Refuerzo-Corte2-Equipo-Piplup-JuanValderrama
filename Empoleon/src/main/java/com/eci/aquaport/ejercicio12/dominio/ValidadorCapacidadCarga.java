package com.eci.aquaport.ejercicio12.dominio;

import java.util.Optional;

public class ValidadorCapacidadCarga extends ValidadorMision {

    @Override
    protected Optional<String> revisar(Drone drone, Mision mision) {
        int capacidad = drone.tipo().capacidadGramos();
        if (mision.cargaGramos() > capacidad) {
            return Optional.of("Carga de " + mision.cargaGramos() + " g supera la capacidad de "
                    + capacidad + " g del " + drone.tipo());
        }
        return Optional.empty();
    }
}
