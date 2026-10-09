package com.eci.aquaport.ejercicio3;

public class DroneBuceador extends DroneAcuatico {

    public DroneBuceador(String id, int bateria, String zona) {
        super(id, bateria, zona);
    }

    @Override
    public TipoDrone getTipo() {
        return TipoDrone.BUCEADOR;
    }

    @Override
    public int getCapacidadMaximaGramos() {
        return 300;
    }
}
