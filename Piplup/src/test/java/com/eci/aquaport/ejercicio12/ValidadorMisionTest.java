package com.eci.aquaport.ejercicio12;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorMisionTest {

    private ValidadorMision v;

    @BeforeEach
    void setUp() {
        v = new ValidadorMision();
    }

    @Test
    @DisplayName("Drone con bateria >= 35% puede ser asignado")
    void droneBateriaSuficiente_puedeAsignarse() {
        DroneAcuatico d = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 85, true, "Embalse Norte");

        boolean resultado = v.tieneBateriaSuficiente(d);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Drone con bateria < 35% NO puede ser asignado")
    void droneBateriaCritica_noAsignable() {
        DroneAcuatico d = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur");

        boolean resultado = v.tieneBateriaSuficiente(d);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Punto de llegada nulo lanza IllegalArgumentException")
    void puntoLlegadaNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> v.validarPuntoLlegada(null));
    }

    @Test
    @DisplayName("Punto de llegada vacio lanza IllegalArgumentException")
    void puntoLlegadaVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> v.validarPuntoLlegada("   "));
    }

    @Test
    @DisplayName("Drone no disponible lanza IllegalArgumentException")
    void droneNoDisponible_lanzaExcepcion() {
        DroneAcuatico d = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 50, false, "Laguna Sur");

        assertThrows(IllegalArgumentException.class, () -> v.validarDroneDisponible(d));
    }

    @Test
    @DisplayName("Zona nula o vacia lanza IllegalArgumentException")
    void zonaInvalida_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> v.validarZona(""));
    }

    @Test
    @DisplayName("Mision valida con drone disponible y sin misiones previas no lanza excepcion")
    void misionValida_noLanzaExcepcion() {
        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Embalse Norte");
        Mision nueva = new Mision.Builder()
                .id("M-001")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hidrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        assertDoesNotThrow(() -> v.validar(nueva, List.of()));
    }

    @Test
    @DisplayName("Drone con mision activa lanza IllegalStateException al validar")
    void droneConMisionActiva_lanzaExcepcion() {
        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Embalse Norte");
        Mision existente = new Mision.Builder()
                .id("M-001")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hidrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();
        Mision nueva = new Mision.Builder()
                .id("M-002")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Canal Central")
                .tipoCarga(TipoCarga.SENSOR)
                .build();

        assertThrows(IllegalStateException.class, () -> v.validar(nueva, List.of(existente)));
    }
}
