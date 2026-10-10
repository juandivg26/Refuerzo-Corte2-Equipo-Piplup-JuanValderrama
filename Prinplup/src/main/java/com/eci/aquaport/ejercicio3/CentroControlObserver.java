package com.eci.aquaport.ejercicio3;

import java.util.function.Consumer;

public class CentroControlObserver implements ObservadorMision {

    private final Consumer<String> canal;

    public CentroControlObserver(Consumer<String> canal) {
        this.canal = canal;
    }

    @Override
    public void notificarFalloDrone(DroneAcuatico drone, Mision mision) {
        canal.accept("[CENTRO DE CONTROL] Drone " + drone.getId() + " en FALLO durante la mision " + mision.id());
    }

    @Override
    public void notificarFalloAsignacion(Mision mision) {
        canal.accept("[CENTRO DE CONTROL] ALERTA: mision CRITICA " + mision.id() + " sin drone disponible");
    }
}
