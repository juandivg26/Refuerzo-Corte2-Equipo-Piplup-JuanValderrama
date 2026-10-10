package com.eci.aquaport.ejercicio12.dominio;

/** Puerto: donde se guarda la telemetria de los drones. */
public interface RegistroTelemetria {

    void registrar(String droneId, String evento);
}
