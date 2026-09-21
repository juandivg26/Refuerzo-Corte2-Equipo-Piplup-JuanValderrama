package com.eci.aquaport.ejercicio4;

public record DroneAcuatico(
    String id,
    String modelo,
    int bateria,
    boolean disponible,
    String zona
) {}