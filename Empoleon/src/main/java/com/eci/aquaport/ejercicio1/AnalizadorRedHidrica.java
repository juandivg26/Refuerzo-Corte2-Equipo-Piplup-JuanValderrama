package com.eci.aquaport.ejercicio1;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnalizadorRedHidrica {

    private static final int BATERIA_MINIMA = 35;

    /** Misiones ENTREGADAS / misiones totales, por zona destino. */
    public Map<ZonaHidrica, Double> eficienciaPorZona(List<Mision> misiones) {
        return misiones.stream()
                .collect(Collectors.groupingBy(Mision::zonaDestino,
                        Collectors.averagingDouble(m -> m.estado() == EstadoMision.ENTREGADA ? 1 : 0)));
    }

    /** Cuantos waypoints de misiones EN_TRANSITO pasan por cada zona (carga de trabajo actual). */
    public Map<ZonaHidrica, Long> cargaPorZona(List<Mision> misiones) {
        return misiones.stream()
                .filter(m -> m.estado() == EstadoMision.EN_TRANSITO)
                .flatMap(m -> m.waypoints().stream())
                .collect(Collectors.groupingBy(z -> z, Collectors.counting()));
    }

    public Optional<DroneAcuatico> droneConMejorHistorial(List<DroneAcuatico> flota) {
        return flota.stream()
                .max(Comparator.comparingInt(DroneAcuatico::entregasExitosas));
    }

    /** Un drone por tramo: el disponible mas cercano al origen del tramo (misma zona primero, luego mas bateria). */
    public List<Tramo> planificarRuta(List<ZonaHidrica> paradas, List<DroneAcuatico> flota) {
        return IntStream.range(0, paradas.size() - 1)
                .mapToObj(i -> new Tramo(paradas.get(i), paradas.get(i + 1), droneMasCercano(paradas.get(i), flota)))
                .toList();
    }

    private DroneAcuatico droneMasCercano(ZonaHidrica origen, List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .filter(d -> d.bateria() >= BATERIA_MINIMA)
                .max(Comparator.comparing((DroneAcuatico d) -> d.zona() == origen)
                        .thenComparingInt(DroneAcuatico::bateria))
                .orElseThrow(() -> new IllegalStateException("No hay drone disponible para el tramo desde " + origen));
    }
}
