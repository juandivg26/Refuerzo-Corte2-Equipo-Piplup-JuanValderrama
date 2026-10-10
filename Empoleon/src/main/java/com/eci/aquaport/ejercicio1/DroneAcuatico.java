package com.eci.aquaport.ejercicio1;

public record DroneAcuatico(
        String id,
        TipoDrone tipo,
        int bateria,
        boolean disponible,
        ZonaHidrica zona,
        int entregasExitosas
) {
}
