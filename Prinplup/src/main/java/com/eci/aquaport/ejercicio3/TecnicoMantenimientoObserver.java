package com.eci.aquaport.ejercicio3;

import java.util.function.Consumer;

public class TecnicoMantenimientoObserver implements ObservadorMision {

    private final Consumer<String> canal;

    public TecnicoMantenimientoObserver(Consumer<String> canal) {
        this.canal = canal;
    }

    @Override
    public void notificarFalloDrone(DroneAcuatico drone, Mision mision) {
        canal.accept("[TECNICO] Revisar drone " + drone.getId() + " (" + drone.getTipo() + ") en " + drone.getZona());
    }
}
