package com.eci.aquaport.ejercicio3.infraestructura;

/** Cliente HTTP de la API externa de condiciones hidricas (su interfaz, no la nuestra). */
public interface ClienteApiHidrica {

    RespuestaApiHidrica fetchConditions(String zoneCode);
}
