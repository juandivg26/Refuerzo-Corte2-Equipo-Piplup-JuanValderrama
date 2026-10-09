package com.eci.aquaport.ejercicio3;

public class FabricaDrones {

    public DroneAcuatico crear(TipoDrone tipo, String id, int bateria, String zona) {
        return switch (tipo) {
            case SUPERFICIAL -> new DroneSuperficial(id, bateria, zona);
            case SEMISUMERGIDO -> new DroneSemisumergido(id, bateria, zona);
            case BUCEADOR -> new DroneBuceador(id, bateria, zona);
        };
    }
}
