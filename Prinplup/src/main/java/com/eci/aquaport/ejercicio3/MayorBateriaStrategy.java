package com.eci.aquaport.ejercicio3;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MayorBateriaStrategy implements EstrategiaSeleccion {

    @Override
    public Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision) {
        return candidatos.stream()
                .max(Comparator.comparingInt(DroneAcuatico::getBateria));
    }
}
