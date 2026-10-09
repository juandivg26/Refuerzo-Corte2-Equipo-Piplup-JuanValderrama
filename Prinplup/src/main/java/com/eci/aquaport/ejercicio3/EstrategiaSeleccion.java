package com.eci.aquaport.ejercicio3;

import java.util.List;
import java.util.Optional;

public interface EstrategiaSeleccion {

    /** Elige uno de los candidatos (ya validados) para la mision, o vacio si no hay ninguno. */
    Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision);
}
