package com.eci.aquaport.ejercicio1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.eci.aquaport.ejercicio1.ZonaHidrica.EMBALSE_INVESTIGACION;
import static com.eci.aquaport.ejercicio1.ZonaHidrica.LAB_HIDRICO_CENTRAL;
import static com.eci.aquaport.ejercicio1.ZonaHidrica.LAGUNA_RESERVA;
import static com.eci.aquaport.ejercicio1.ZonaHidrica.RED_CANALES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnalizadorRedHidricaTest {

    private final AnalizadorRedHidrica analizador = new AnalizadorRedHidrica();

    private final DroneAcuatico buceador = new DroneAcuatico("AR-01", TipoDrone.BUCEADOR, 90, true, EMBALSE_INVESTIGACION, 12);
    private final DroneAcuatico superficial = new DroneAcuatico("AR-16", TipoDrone.SUPERFICIAL, 60, true, RED_CANALES, 30);
    private final DroneAcuatico canalesAlto = new DroneAcuatico("AR-17", TipoDrone.SUPERFICIAL, 95, false, RED_CANALES, 5);
    private final DroneAcuatico sinBateria = new DroneAcuatico("AR-31", TipoDrone.SEMISUMERGIDO, 20, true, LAGUNA_RESERVA, 8);
    private final List<DroneAcuatico> flota = List.of(buceador, superficial, canalesAlto, sinBateria);

    @Test
    @DisplayName("Eficiencia = misiones entregadas / misiones totales por zona destino")
    void eficienciaPorZona() {
        List<Mision> misiones = List.of(
                new Mision("M-1", EstadoMision.ENTREGADA, List.of(EMBALSE_INVESTIGACION, LAB_HIDRICO_CENTRAL)),
                new Mision("M-2", EstadoMision.FALLIDA, List.of(RED_CANALES, LAB_HIDRICO_CENTRAL)),
                new Mision("M-3", EstadoMision.ENTREGADA, List.of(LAGUNA_RESERVA, RED_CANALES)));

        Map<ZonaHidrica, Double> eficiencia = analizador.eficienciaPorZona(misiones);

        assertEquals(Map.of(LAB_HIDRICO_CENTRAL, 0.5, RED_CANALES, 1.0), eficiencia);
    }

    @Test
    @DisplayName("flatMap cuenta los waypoints de las misiones EN_TRANSITO por zona")
    void cargaPorZona() {
        List<Mision> misiones = List.of(
                new Mision("M-1", EstadoMision.EN_TRANSITO, List.of(EMBALSE_INVESTIGACION, RED_CANALES, LAB_HIDRICO_CENTRAL)),
                new Mision("M-2", EstadoMision.EN_TRANSITO, List.of(RED_CANALES, LAB_HIDRICO_CENTRAL)),
                new Mision("M-3", EstadoMision.ENTREGADA, List.of(LAGUNA_RESERVA, RED_CANALES)));

        assertEquals(Map.of(EMBALSE_INVESTIGACION, 1L, RED_CANALES, 2L, LAB_HIDRICO_CENTRAL, 2L),
                analizador.cargaPorZona(misiones));
    }

    @Test
    @DisplayName("El mejor historial es el drone con mas entregas exitosas")
    void droneConMejorHistorial() {
        assertEquals(superficial, analizador.droneConMejorHistorial(flota).orElseThrow());
    }

    @Test
    @DisplayName("Cada tramo recibe el drone disponible de su zona de origen; si no hay, el de mas bateria")
    void planificarRutaMultiEtapa() {
        List<Tramo> ruta = analizador.planificarRuta(List.of(EMBALSE_INVESTIGACION, RED_CANALES, LAB_HIDRICO_CENTRAL), flota);

        assertEquals(List.of(
                new Tramo(EMBALSE_INVESTIGACION, RED_CANALES, buceador),
                new Tramo(RED_CANALES, LAB_HIDRICO_CENTRAL, superficial)), ruta);
    }

    @Test
    @DisplayName("Sin drones aptos la planificacion falla indicando el tramo")
    void planificarRutaSinDrones() {
        List<ZonaHidrica> paradas = List.of(LAGUNA_RESERVA, LAB_HIDRICO_CENTRAL);
        List<DroneAcuatico> soloSinBateria = List.of(sinBateria);

        assertThrows(IllegalStateException.class, () -> analizador.planificarRuta(paradas, soloSinBateria));
    }

    @Test
    @DisplayName("El Collector propio calcula la bateria promedio por zona en una pasada")
    void collectorPromedioPorZona() {
        assertEquals(Map.of(EMBALSE_INVESTIGACION, 90.0, RED_CANALES, 77.5, LAGUNA_RESERVA, 20.0),
                flota.stream().collect(ColectoresFlota.promedioBateriaPorZona()));
    }

    @Test
    @DisplayName("El Collector da el mismo resultado en stream paralelo (el combiner es correcto)")
    void collectorEnParalelo() {
        List<DroneAcuatico> grande = IntStream.range(0, 10_000)
                .mapToObj(i -> new DroneAcuatico("D" + i, TipoDrone.BUCEADOR, i % 101, true,
                        ZonaHidrica.values()[i % 4], 0))
                .toList();

        assertEquals(grande.stream().collect(ColectoresFlota.promedioBateriaPorZona()),
                grande.parallelStream().collect(ColectoresFlota.promedioBateriaPorZona()));
    }
}
