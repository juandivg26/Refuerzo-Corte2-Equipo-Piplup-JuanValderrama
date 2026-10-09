package com.eci.aquaport.ejercicio1;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConsultorFlota {

    private static final int BATERIA_MINIMA = 35;

    public Map<TipoDrone, List<DroneAcuatico>> agruparDisponiblesPorTipo(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .collect(Collectors.groupingBy(DroneAcuatico::tipo));
    }

    public Optional<DroneAcuatico> droneOptimoParaZona(List<DroneAcuatico> flota, String zona, TipoDrone tipo) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .filter(d -> d.bateria() >= BATERIA_MINIMA)
                .filter(d -> d.tipo() == tipo)
                .filter(d -> d.zona().equals(zona))
                .max(Comparator.comparingInt(DroneAcuatico::bateria));
    }

    public Map<TipoDrone, Double> promedioBateriaPorTipo(List<DroneAcuatico> flota) {
        return flota.stream()
                .collect(Collectors.groupingBy(DroneAcuatico::tipo,
                        Collectors.averagingInt(DroneAcuatico::bateria)));
    }

    public Map<Boolean, List<Mision>> separarCriticas(List<Mision> misiones) {
        return misiones.stream()
                .collect(Collectors.partitioningBy(m -> m.prioridad() == Prioridad.CRITICA));
    }

    public List<String> zonasCubiertasPorFlotaActiva(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .map(DroneAcuatico::zona)
                .distinct()
                .sorted()
                .toList();
    }
}
