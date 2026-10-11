package com.eci.aquaport.ejercicio12.dominio;

public enum TipoDrone {
    SUPERFICIAL(500),
    SEMISUMERGIDO(1500),
    BUCEADOR(300);

    private final int capacidadGramos;

    TipoDrone(int capacidadGramos) {
        this.capacidadGramos = capacidadGramos;
    }

    public int capacidadGramos() {
        return capacidadGramos;
    }
}
