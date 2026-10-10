package com.eci.aquaport.ejercicio3.dominio;

public class DroneAcuatico implements Drone {

    public static final int CONSUMO_POR_TRAMO = 10;

    private final String id;
    private final TipoDrone tipo;
    private int bateria;
    private ZonaHidrica zona;

    public DroneAcuatico(String id, TipoDrone tipo, int bateria, ZonaHidrica zona) {
        this.id = id;
        this.tipo = tipo;
        this.bateria = bateria;
        this.zona = zona;
    }

    @Override public String id() { return id; }
    @Override public TipoDrone tipo() { return tipo; }
    @Override public int bateria() { return bateria; }
    @Override public ZonaHidrica zona() { return zona; }

    @Override
    public void navegar(ZonaHidrica destino) {
        zona = destino;
        bateria = Math.max(0, bateria - CONSUMO_POR_TRAMO);
    }

    @Override
    public String toString() {
        return id + " (" + tipo + ", " + bateria + "%, " + zona + ")";
    }
}
