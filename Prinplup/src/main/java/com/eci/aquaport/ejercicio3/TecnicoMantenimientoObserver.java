package com.eci.aquaport.ejercicio3;

public class TecnicoMantenimientoObserver implements ObservadorMision {

    @Override
    public void notificarFalloDrone(DroneAcuatico drone, Mision mision) {
        System.out.println("[TECNICO] Revisar drone " + drone.getId() + " (" + drone.getTipo() + ") en " + drone.getZona());
    }
}
