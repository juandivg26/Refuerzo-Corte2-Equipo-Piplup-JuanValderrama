package com.eci.aquaport.ejercicio3;

public class DroneSemisumergido extends DroneAcuatico {

    public DroneSemisumergido(String id, int bateria, String zona) {
        super(id, bateria, zona);
    }

    @Override
    public TipoDrone getTipo() {
        return TipoDrone.SEMISUMERGIDO;
    }

    @Override
    public int getCapacidadMaximaGramos() {
        return 1500;
    }
}
