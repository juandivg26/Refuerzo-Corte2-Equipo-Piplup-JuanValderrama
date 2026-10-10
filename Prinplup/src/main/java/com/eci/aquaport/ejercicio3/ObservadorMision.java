package com.eci.aquaport.ejercicio3;

public interface ObservadorMision {

    void notificarFalloDrone(DroneAcuatico drone, Mision mision);

    /** Mision CRITICA que se quedo sin drone. Por defecto se ignora: solo algunos observadores la atienden. */
    default void notificarFalloAsignacion(Mision mision) {
    }
}
