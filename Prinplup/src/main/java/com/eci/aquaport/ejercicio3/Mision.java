package com.eci.aquaport.ejercicio3;

public record Mision(
        String id,
        String zonaDestino,
        TipoCarga tipoCarga,
        int pesoGramos,
        Prioridad prioridad
) {
}
