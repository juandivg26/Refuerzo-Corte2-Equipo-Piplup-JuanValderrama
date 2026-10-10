package com.eci.aquaport.ejercicio3.infraestructura;

import com.eci.aquaport.ejercicio3.dominio.Drone;
import com.eci.aquaport.ejercicio3.dominio.RegistroTelemetria;
import com.eci.aquaport.ejercicio3.dominio.TipoDrone;
import com.eci.aquaport.ejercicio3.dominio.ZonaHidrica;

/** Decorator: agrega registro de telemetria a cualquier Drone sin modificar su clase. Se puede apilar. */
public class DroneConMonitoreo implements Drone {

    private final Drone drone;
    private final RegistroTelemetria registro;

    public DroneConMonitoreo(Drone drone, RegistroTelemetria registro) {
        this.drone = drone;
        this.registro = registro;
    }

    @Override public String id() { return drone.id(); }
    @Override public TipoDrone tipo() { return drone.tipo(); }
    @Override public int bateria() { return drone.bateria(); }
    @Override public ZonaHidrica zona() { return drone.zona(); }

    @Override
    public void navegar(ZonaHidrica destino) {
        registro.registrar(id(), "Inicio tramo " + zona() + " -> " + destino + " con " + bateria() + "%");
        drone.navegar(destino);
        registro.registrar(id(), "Fin tramo en " + zona() + " con " + bateria() + "%");
    }

    @Override
    public String toString() {
        return drone.toString();
    }
}
