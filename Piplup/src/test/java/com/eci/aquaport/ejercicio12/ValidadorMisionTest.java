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
    @DisplayName("Zona vacia lanza IllegalArgumentException")
    void zonaVacia_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> v.validarZona(""));
    }

    @Test
    @DisplayName("Zona que no es una de las 5 zonas fijas lanza IllegalArgumentException (caso edge)")
    void zonaNoRegistrada_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> v.validarZona("Rio Bogota"));
    }

    @Test
    @DisplayName("Zona fija del campus es valida")
    void zonaFija_esValida() {
        assertDoesNotThrow(() -> v.validarZona("Laguna Sur"));
    }

    @Test
    @DisplayName("Drone nulo lanza IllegalArgumentException")
    void droneNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> v.validarDroneDisponible(null));
    }

    @Test
    @DisplayName("Mision nula lanza IllegalArgumentException al validar")
    void misionNula_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> v.validar(null, List.of()));
    }

    @Test
    @DisplayName("Mision con zona de destino invalida lanza IllegalArgumentException al validar")
    void misionZonaDestinoInvalida_lanzaExcepcion() {
        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Embalse Norte");
        Mision nueva = new Mision.Builder()
                .id("M-003")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Rio Bogota")
                .tipoCarga(TipoCarga.SENSOR)
                .build();

        assertThrows(IllegalArgumentException.class, () -> v.validar(nueva, List.of()));
    }

    @Test
    @DisplayName("Mision con drone sin bateria suficiente lanza IllegalStateException al validar")
    void misionDroneSinBateria_lanzaExcepcion() {
        DroneAcuatico drone = new DroneAcuatico("AR-02", "Aqua-Ranger 100", 20, true, "Canal Central");
        Mision nueva = new Mision.Builder()
                .id("M-004")
                .drone(drone)
                .puntoPartida("Canal Central")
                .puntoLlegada("Laguna Sur")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        assertThrows(IllegalStateException.class, () -> v.validar(nueva, List.of()));
    }

    @Test
    @DisplayName("Drone con mision ENTREGADA puede recibir una nueva mision")
    void droneConMisionEntregada_puedeAsignarse() {
        DroneAcuatico drone = new DroneAcuatico("AR-04", "Aqua-Ranger 100", 73, true, "Punto Ribereño Este");
        Mision entregada = new Mision.Builder()
                .id("M-005")
                .drone(drone)
                .puntoPartida("Punto Ribereño Este")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.SENSOR)
                .estado(EstadoMision.ENTREGADA)
                .build();
        Mision nueva = new Mision.Builder()
                .id("M-006")
                .drone(drone)
                .puntoPartida("Laboratorio Hídrico")
                .puntoLlegada("Canal Central")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        assertDoesNotThrow(() -> v.validar(nueva, List.of(entregada)));
    }

    @Test
    @DisplayName("Mision valida con drone disponible y sin misiones previas no lanza excepcion")
    void misionValida_noLanzaExcepcion() {
        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Embalse Norte");
        Mision nueva = new Mision.Builder()
                .id("M-001")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
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
                .puntoLlegada("Laboratorio Hídrico")
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
