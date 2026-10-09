package com.eci.aquaport.ejercicio3;

public class CentroControlObserver implements ObservadorMision {

    @Override
    public void notificarFalloDrone(DroneAcuatico drone, Mision mision) {
        System.out.println("[CENTRO DE CONTROL] Drone " + drone.getId() + " en FALLO durante la mision " + mision.id());
    }

    @Override
    public void notificarFalloAsignacion(Mision mision) {
        System.out.println("[CENTRO DE CONTROL] ALERTA: mision CRITICA " + mision.id() + " sin drone disponible");
    }
}
