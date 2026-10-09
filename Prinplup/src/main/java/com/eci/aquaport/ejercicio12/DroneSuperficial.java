package com.eci.aquaport.ejercicio12;

public class DroneSuperficial extends DroneAcuatico {

    public DroneSuperficial(String id, int bateria, String zona) {
        super(id, bateria, zona);
    }

    @Override
    public TipoDrone getTipo() {
        return TipoDrone.SUPERFICIAL;
    }

    @Override
    public int getCapacidadMaximaGramos() {
        return 500;
    }
}
