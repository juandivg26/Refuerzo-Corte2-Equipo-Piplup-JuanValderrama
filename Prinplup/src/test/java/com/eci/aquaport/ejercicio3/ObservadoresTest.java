package com.eci.aquaport.ejercicio3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObservadoresTest {

    private final List<String> mensajes = new ArrayList<>();
    private final DroneAcuatico drone = new DroneBuceador("AR-11", 90, "Embalse Norte");
    private final Mision critica = new Mision("M-501", "Embalse Norte", TipoCarga.SENSOR, 2000, Prioridad.CRITICA);

    @Test
    @DisplayName("El centro de control registra el drone en FALLO")
    void centroControl_falloDrone() {
        new CentroControlObserver(mensajes::add).notificarFalloDrone(drone, critica);

        assertEquals(List.of("[CENTRO DE CONTROL] Drone AR-11 en FALLO durante la mision M-501"), mensajes);
    }

    @Test
    @DisplayName("El tecnico recibe tipo y zona del drone a revisar")
    void tecnico_falloDrone() {
        new TecnicoMantenimientoObserver(mensajes::add).notificarFalloDrone(drone, critica);

        assertEquals(List.of("[TECNICO] Revisar drone AR-11 (BUCEADOR) en Embalse Norte"), mensajes);
    }

    @Test
    @DisplayName("El tecnico ignora la alerta de mision sin drone (metodo por defecto)")
    void tecnico_ignoraFalloAsignacion() {
        new TecnicoMantenimientoObserver(mensajes::add).notificarFalloAsignacion(critica);

        assertTrue(mensajes.isEmpty());
    }

    @Test
    @DisplayName("Mision CRITICA sin drone apto alerta al centro de control")
    void misionCriticaSinDrone_alertaCentroControl() {
        AsignadorAutomatico asignador = new AsignadorAutomatico(new MayorBateriaStrategy(), new ValidadorMision());
        asignador.registrarObservador(new CentroControlObserver(mensajes::add));

        asignador.asignar(critica, List.of(drone));

        assertEquals(List.of("[CENTRO DE CONTROL] ALERTA: mision CRITICA M-501 sin drone disponible"), mensajes);
    }

    @Test
    @DisplayName("Mision NORMAL sin drone apto no genera alerta")
    void misionNormalSinDrone_sinAlerta() {
        AsignadorAutomatico asignador = new AsignadorAutomatico(new MayorBateriaStrategy(), new ValidadorMision());
        asignador.registrarObservador(new CentroControlObserver(mensajes::add));
        Mision normal = new Mision("M-502", "Embalse Norte", TipoCarga.SENSOR, 2000, Prioridad.NORMAL);

        asignador.asignar(normal, List.of(drone));

        assertTrue(mensajes.isEmpty());
    }
}
