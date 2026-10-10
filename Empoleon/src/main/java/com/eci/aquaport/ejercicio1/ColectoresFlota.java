package com.eci.aquaport.ejercicio1;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class ColectoresFlota {

    private ColectoresFlota() {
    }

    /**
     * Agrupa por zona y calcula la bateria promedio en una sola pasada.
     * El acumulador guarda [suma, cantidad] por zona; el combiner los suma, asi que sirve en stream paralelo.
     */
    public static Collector<DroneAcuatico, Map<ZonaHidrica, int[]>, Map<ZonaHidrica, Double>> promedioBateriaPorZona() {
        return Collector.of(
                () -> new EnumMap<>(ZonaHidrica.class),
                (acumulado, drone) -> sumar(acumulado, drone.zona(), drone.bateria(), 1),
                (izquierda, derecha) -> {
                    derecha.forEach((zona, total) -> sumar(izquierda, zona, total[0], total[1]));
                    return izquierda;
                },
                acumulado -> acumulado.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                e -> (double) e.getValue()[0] / e.getValue()[1])));
    }

    private static void sumar(Map<ZonaHidrica, int[]> acumulado, ZonaHidrica zona, int bateria, int cantidad) {
        int[] total = acumulado.computeIfAbsent(zona, z -> new int[2]);
        total[0] += bateria;
        total[1] += cantidad;
    }
}
