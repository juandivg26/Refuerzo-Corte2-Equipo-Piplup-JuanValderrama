package com.eci.aquaport.ejercicio3;

import java.util.EnumSet;
import java.util.Set;

public class ValidadorMision {

    public static final int BATERIA_MINIMA = 35;

    // FALLO no se descarta aqui: el asignador debe detectarlo y avisar a los observadores.
    private static final Set<EstadoDrone> OCUPADOS = EnumSet.of(
            EstadoDrone.EN_MISION, EstadoDrone.RECARGANDO, EstadoDrone.MANTENIMIENTO, EstadoDrone.SUMERGIDO);

    public boolean esApto(DroneAcuatico drone, Mision mision) {
        return drone.getBateria() >= BATERIA_MINIMA
                && drone.puedeCargar(mision.pesoGramos())
                && !OCUPADOS.contains(drone.getEstado());
    }
}
