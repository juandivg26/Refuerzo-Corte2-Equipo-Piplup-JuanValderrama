package com.eci.aquaport.ejercicio3.infraestructura;

/** Respuesta tal como la entrega la API externa: JSON con campos en ingles. */
public record RespuestaApiHidrica(
        String zoneCode,
        double waterLevel,
        double turbidity
) {
}
