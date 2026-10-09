package com.eci.aquaport.ejercicio3;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ZonaCercanaStrategy implements EstrategiaSeleccion {

    /** Prefiere drones que ya estan en la zona destino; entre ellos, el de mayor bateria. */
    @Override
    public Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision) {
        return candidatos.stream()
                .max(Comparator.comparing((DroneAcuatico d) -> d.getZona().equals(mision.zonaDestino()))
                        .thenComparingInt(DroneAcuatico::getBateria));
    }
}
