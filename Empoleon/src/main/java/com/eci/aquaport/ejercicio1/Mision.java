package com.eci.aquaport.ejercicio1;

import java.util.List;

/** Mision multi-etapa: recorre los waypoints en orden; el ultimo es el destino final. */
public record Mision(
        String id,
        EstadoMision estado,
        List<ZonaHidrica> waypoints
) {
    public ZonaHidrica zonaDestino() {
        return waypoints.get(waypoints.size() - 1);
    }
}
