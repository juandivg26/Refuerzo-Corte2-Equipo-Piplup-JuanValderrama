package com.eci.aquaport.ejercicio12.dominio;

/** Puerto: a quien se avisa del avance de una ruta multi-etapa. */
public interface NotificadorWaypoints {

    void waypointAlcanzado(String solicitudId, ZonaHidrica zona, String droneId);

    void droneReasignado(String solicitudId, String droneSaliente, String droneEntrante);
}
