package com.eci.aquaport.ejercicio3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstrategiaSeleccionTest {

    private final DroneAcuatico enLaguna = new DroneSemisumergido("AR-06", 60, "Laguna Sur");
    private final DroneAcuatico conMasBateria = new DroneSuperficial("AR-01", 95, "Canal Central");
    private final List<DroneAcuatico> candidatos = List.of(enLaguna, conMasBateria);
    private final Mision haciaLaguna = new Mision("M-301", "Laguna Sur", TipoCarga.SENSOR, 200, Prioridad.NORMAL);

    @Test
    @DisplayName("MayorBateriaStrategy elige el candidato con mas bateria")
    void mayorBateria() {
        assertEquals(conMasBateria, new MayorBateriaStrategy().seleccionar(candidatos, haciaLaguna).orElseThrow());
    }

    @Test
    @DisplayName("ZonaCercanaStrategy prefiere el drone que ya esta en la zona destino")
    void zonaCercana() {
        assertEquals(enLaguna, new ZonaCercanaStrategy().seleccionar(candidatos, haciaLaguna).orElseThrow());
    }

    @Test
    @DisplayName("Sin candidatos las estrategias retornan vacio")
    void sinCandidatos() {
        assertTrue(new ZonaCercanaStrategy().seleccionar(List.of(), haciaLaguna).isEmpty());
    }

    @Test
    @DisplayName("ValidadorMision descarta drones ocupados")
    void validadorDescartaOcupados() {
        DroneAcuatico ocupado = new DroneSuperficial("AR-02", 90, "Canal Central");
        ocupado.setEstado(EstadoDrone.RECARGANDO);

        assertFalse(new ValidadorMision().esApto(ocupado, haciaLaguna));
    }
}
