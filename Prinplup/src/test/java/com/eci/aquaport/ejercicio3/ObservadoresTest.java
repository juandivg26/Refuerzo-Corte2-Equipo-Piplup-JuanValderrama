package com.eci.aquaport.ejercicio3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObservadoresTest {

    private final PrintStream salidaOriginal = System.out;
    private final ByteArrayOutputStream salida = new ByteArrayOutputStream();
    private final DroneAcuatico drone = new DroneBuceador("AR-11", 90, "Embalse Norte");
    private final Mision critica = new Mision("M-501", "Embalse Norte", TipoCarga.SENSOR, 2000, Prioridad.CRITICA);

    @BeforeEach
    void capturarSalida() {
        System.setOut(new PrintStream(salida));
    }

    @AfterEach
    void restaurarSalida() {
        System.setOut(salidaOriginal);
    }

    @Test
    @DisplayName("El centro de control registra el drone en FALLO")
    void centroControl_falloDrone() {
        new CentroControlObserver().notificarFalloDrone(drone, critica);

        assertTrue(salida.toString().contains("AR-11 en FALLO durante la mision M-501"));
    }

    @Test
    @DisplayName("El tecnico recibe tipo y zona del drone a revisar")
    void tecnico_falloDrone() {
        new TecnicoMantenimientoObserver().notificarFalloDrone(drone, critica);

        assertTrue(salida.toString().contains("AR-11 (BUCEADOR) en Embalse Norte"));
    }

    @Test
    @DisplayName("El tecnico ignora la alerta de mision sin drone (metodo por defecto)")
    void tecnico_ignoraFalloAsignacion() {
        new TecnicoMantenimientoObserver().notificarFalloAsignacion(critica);

        assertEquals("", salida.toString());
    }

    @Test
    @DisplayName("Mision CRITICA sin drone apto alerta al centro de control")
    void misionCriticaSinDrone_alertaCentroControl() {
        AsignadorAutomatico asignador = new AsignadorAutomatico(new MayorBateriaStrategy(), new ValidadorMision());
        asignador.registrarObservador(new CentroControlObserver());

        asignador.asignar(critica, List.of(drone));

        assertTrue(salida.toString().contains("ALERTA: mision CRITICA M-501 sin drone disponible"));
    }

    @Test
    @DisplayName("Mision NORMAL sin drone apto no genera alerta")
    void misionNormalSinDrone_sinAlerta() {
        AsignadorAutomatico asignador = new AsignadorAutomatico(new MayorBateriaStrategy(), new ValidadorMision());
        asignador.registrarObservador(new CentroControlObserver());
        Mision normal = new Mision("M-502", "Embalse Norte", TipoCarga.SENSOR, 2000, Prioridad.NORMAL);

        asignador.asignar(normal, List.of(drone));

        assertEquals("", salida.toString());
    }
}
