package com.eci.aquaport.ejercicio12;

public abstract class DroneAcuatico {

    private final String id;
    private final int bateria;
    private final String zona;
    private EstadoDrone estado = EstadoDrone.DISPONIBLE;

    protected DroneAcuatico(String id, int bateria, String zona) {
        this.id = id;
        this.bateria = bateria;
        this.zona = zona;
    }

    public abstract TipoDrone getTipo();

    public abstract int getCapacidadMaximaGramos();

    public boolean puedeCargar(int pesoGramos) {
        return pesoGramos <= getCapacidadMaximaGramos();
    }

    public String getId() { return id; }
    public int getBateria() { return bateria; }
    public String getZona() { return zona; }
    public EstadoDrone getEstado() { return estado; }

    public void setEstado(EstadoDrone estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return id + " (" + getTipo() + ", " + bateria + "%, " + zona + ", " + estado + ")";
    }
}
