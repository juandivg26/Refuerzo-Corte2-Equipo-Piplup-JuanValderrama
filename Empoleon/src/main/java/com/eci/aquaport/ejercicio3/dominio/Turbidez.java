package com.eci.aquaport.ejercicio3.dominio;

/** Turbidez del agua en NTU (unidades nefelometricas). */
public record Turbidez(double ntu) {

    public Turbidez {
        if (Double.isNaN(ntu) || ntu < 0) {
            throw new IllegalArgumentException("La turbidez no puede ser negativa: " + ntu);
        }
    }
}
