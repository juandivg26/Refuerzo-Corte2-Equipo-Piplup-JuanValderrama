package com.eci.aquaport.ejercicio12;

public record Mision(
        String id,
        String zonaDestino,
        TipoCarga tipoCarga,
        int pesoGramos,
        Prioridad prioridad
) {
}
