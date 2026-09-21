package com.eci.aquaport.ejercicio4;

import java.util.List;

public class ValidadorMision {

    public void validar(Mision nueva, List<Mision> registradas) {
        if (nueva == null) {
            throw new IllegalArgumentException("La mision no puede ser nula.");
        }
        String idDrone = nueva.getDrone().id();
        boolean droneOcupado = registradas.stream()
                .filter(this::estaActiva)
                .anyMatch(m -> m.getDrone().id().equals(idDrone));
        if (droneOcupado) {
            throw new IllegalStateException("El drone " + idDrone
                    + " ya tiene una mision activa. Un drone no puede tener mas de 1 mision activa simultanea.");
        }
    }

    private boolean estaActiva(Mision mision) {
        return mision.getEstado() == EstadoMision.PENDIENTE
                || mision.getEstado() == EstadoMision.EN_TRANSITO;
    }
}