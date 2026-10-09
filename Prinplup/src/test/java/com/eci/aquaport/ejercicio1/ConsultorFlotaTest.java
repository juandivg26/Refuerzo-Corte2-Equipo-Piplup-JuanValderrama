package com.eci.aquaport.ejercicio1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsultorFlotaTest {

    private ConsultorFlota consultor;
    private List<DroneAcuatico> flota;

    @BeforeEach
    void setUp() {
        consultor = new ConsultorFlota();
        flota = List.of(
                new DroneAcuatico("AR-01", TipoDrone.SUPERFICIAL, 92, true, "Canal Central"),
                new DroneAcuatico("AR-02", TipoDrone.SUPERFICIAL, 40, false, "Laguna Sur"),
                new DroneAcuatico("AR-11", TipoDrone.BUCEADOR, 79, true, "Embalse Norte"),
                new DroneAcuatico("AR-13", TipoDrone.BUCEADOR, 64, true, "Embalse Norte"),
                new DroneAcuatico("AR-14", TipoDrone.BUCEADOR, 30, true, "Embalse Norte")
        );
    }

    @Test
    @DisplayName("Agrupa solo los drones disponibles por tipo")
    void agruparDisponiblesPorTipo() {
        Map<TipoDrone, List<DroneAcuatico>> porTipo = consultor.agruparDisponiblesPorTipo(flota);

        assertEquals(1, porTipo.get(TipoDrone.SUPERFICIAL).size());
    }

    @Test
    @DisplayName("Drone optimo es el de mayor bateria del tipo y zona pedidos")
    void droneOptimoParaZona() {
        Optional<DroneAcuatico> optimo = consultor.droneOptimoParaZona(flota, "Embalse Norte", TipoDrone.BUCEADOR);

        assertEquals("AR-11", optimo.orElseThrow().id());
    }

    @Test
    @DisplayName("Sin drones aptos en la zona retorna Optional vacio")
    void droneOptimoSinCandidatos() {
        assertTrue(consultor.droneOptimoParaZona(flota, "Laguna Sur", TipoDrone.SUPERFICIAL).isEmpty());
    }

    @Test
    @DisplayName("Promedio de bateria se calcula por tipo")
    void promedioBateriaPorTipo() {
        assertEquals(66.0, consultor.promedioBateriaPorTipo(flota).get(TipoDrone.SUPERFICIAL));
    }

    @Test
    @DisplayName("partitioningBy separa las misiones CRITICAS de las demas")
    void separarCriticas() {
        List<Mision> misiones = List.of(
                new Mision("M-1", "Embalse Norte", Prioridad.CRITICA),
                new Mision("M-2", "Laguna Sur", Prioridad.NORMAL),
                new Mision("M-3", "Canal Central", Prioridad.ALTA));

        Map<Boolean, List<Mision>> separadas = consultor.separarCriticas(misiones);

        assertEquals(List.of("M-1"), separadas.get(true).stream().map(Mision::id).toList());
    }

    @Test
    @DisplayName("Zonas unicas y ordenadas de la flota activa")
    void zonasCubiertasPorFlotaActiva() {
        assertEquals(List.of("Canal Central", "Embalse Norte"), consultor.zonasCubiertasPorFlotaActiva(flota));
    }
}
