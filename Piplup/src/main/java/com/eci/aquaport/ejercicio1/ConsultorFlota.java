package com.eci.aquaport.ejercicio1;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ConsultorFlota {

    private static final int BATERIA_MINIMA = 35;

    public List<DroneAcuatico> dronesDisponiblesOrdenadosPorBateria(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .filter(d -> d.bateria() >= BATERIA_MINIMA)
                .sorted(Comparator.comparingInt(DroneAcuatico::bateria).reversed())
                .toList();
    }

    public List<String> idsDronesDisponibles(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .map(DroneAcuatico::id)
                .toList();
    }

    public boolean existeDroneDisponibleConBateriaSuficiente(List<DroneAcuatico> flota) {
        return flota.stream()
                .anyMatch(d -> d.disponible() && d.bateria() >= BATERIA_MINIMA);
    }

    public long contarDronesDisponibles(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .count();
    }

    public Optional<DroneAcuatico> droneConMayorBateria(List<DroneAcuatico> flota) {
        return flota.stream()
                .max(Comparator.comparingInt(DroneAcuatico::bateria));
    }
}
