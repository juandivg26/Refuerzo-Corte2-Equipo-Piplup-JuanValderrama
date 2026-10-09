package com.eci.aquaport.ejercicio12;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ModeloDronesTest {

    @Test
    @DisplayName("Cada subclase reporta su tipo (Liskov: todas se usan como DroneAcuatico)")
    void cadaSubclaseReportaSuTipo() {
        List<DroneAcuatico> drones = List.of(
                new DroneSuperficial("AR-01", 80, "Canal Central"),
                new DroneSemisumergido("AR-06", 80, "Laguna Sur"),
                new DroneBuceador("AR-11", 80, "Embalse Norte"));

        assertEquals(List.of(TipoDrone.SUPERFICIAL, TipoDrone.SEMISUMERGIDO, TipoDrone.BUCEADOR),
                drones.stream().map(DroneAcuatico::getTipo).toList());
    }

    @Test
    @DisplayName("toString muestra id, tipo, bateria, zona y estado")
    void toStringLegible() {
        assertEquals("AR-11 (BUCEADOR, 80%, Embalse Norte, DISPONIBLE)",
                new DroneBuceador("AR-11", 80, "Embalse Norte").toString());
    }

    @Test
    @DisplayName("Drone RECARGANDO con bateria y capacidad suficientes no es apto")
    void droneOcupado_noEsApto() {
        DroneAcuatico recargando = new DroneSemisumergido("AR-08", 90, "Laguna Sur");
        recargando.setEstado(EstadoDrone.RECARGANDO);
        Mision mision = new Mision("M-602", "Laguna Sur", TipoCarga.SENSOR, 100, Prioridad.NORMAL);

        assertFalse(new ValidadorMision().esApto(recargando, mision));
    }

    @Test
    @DisplayName("Un observador que solo atiende fallos de drone ignora la alerta de asignacion")
    void observadorPorDefectoIgnoraFalloAsignacion() {
        ObservadorMision soloFallos = (drone, mision) -> { };
        Mision mision = new Mision("M-601", "Laguna Sur", TipoCarga.SENSOR, 100, Prioridad.CRITICA);

        assertDoesNotThrow(() -> soloFallos.notificarFalloAsignacion(mision));
    }
}
