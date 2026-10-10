package com.eci.aquaport.ejercicio12.dominio;

/** Profundidad del agua en metros. */
public record NivelAgua(double metros) {

    public NivelAgua {
        if (Double.isNaN(metros) || metros < 0) {
            throw new IllegalArgumentException("El nivel de agua no puede ser negativo: " + metros);
        }
    }
}
