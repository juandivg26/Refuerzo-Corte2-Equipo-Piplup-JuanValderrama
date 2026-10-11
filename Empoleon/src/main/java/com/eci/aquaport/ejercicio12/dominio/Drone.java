package com.eci.aquaport.ejercicio12.dominio;

/** Contrato comun de los drones; permite envolverlos con decoradores sin tocar la clase base. */
public interface Drone {

    String id();

    TipoDrone tipo();

    int bateria();

    ZonaHidrica zona();

    EstadoDrone estado();

    /** Recorre un tramo hasta el destino: cambia de zona y consume bateria. */
    void navegar(ZonaHidrica destino);
}
