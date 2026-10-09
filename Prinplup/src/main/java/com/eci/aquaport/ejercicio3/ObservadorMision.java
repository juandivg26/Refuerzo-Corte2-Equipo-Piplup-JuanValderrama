package com.eci.aquaport.ejercicio3;

public interface ObservadorMision {

    void notificarFalloDrone(DroneAcuatico drone, Mision mision);

    /** Mision CRITICA que se quedo sin drone. Por defecto no hace nada: no todo observador la atiende. */
    default void notificarFalloAsignacion(Mision mision) {
    }
}
