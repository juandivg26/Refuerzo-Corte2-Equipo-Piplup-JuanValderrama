package com.eci.aquaport.ejercicio3.dominio;

import java.util.Optional;

public class ValidadorBateria extends ValidadorMision {

    public static final int BATERIA_MINIMA = 35;

    @Override
    protected Optional<String> revisar(Drone drone, Mision mision) {
        if (drone.bateria() < BATERIA_MINIMA) {
            return Optional.of("Bateria insuficiente: " + drone.bateria() + "% (minimo " + BATERIA_MINIMA + "%)");
        }
        return Optional.empty();
    }
}
