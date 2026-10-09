package com.eci.aquaport.ejercicio12;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignadorAutomaticoTest {

    @Mock EstrategiaSeleccion estrategiaMock;
    @Mock ObservadorMision observadorMock;

    private AsignadorAutomatico asignador;

    @BeforeEach
    void setUp() {
        asignador = new AsignadorAutomatico(estrategiaMock, new ValidadorMision());
        asignador.registrarObservador(observadorMock);
    }

    private Mision mision(Prioridad prioridad, int pesoGramos) {
        return new Mision("M-401", "Laguna Sur", TipoCarga.MUESTRA_AGUA, pesoGramos, prioridad);
    }

    @Test
    @DisplayName("Happy path: asigna el drone elegido por la estrategia y lo pone EN_MISION")
    void asignacionExitosa() {
        // ARRANGE
        DroneAcuatico drone = new DroneSemisumergido("AR-06", 80, "Laguna Sur");
        Mision mision = mision(Prioridad.NORMAL, 400);
        when(estrategiaMock.seleccionar(List.of(drone), mision)).thenReturn(Optional.of(drone));

        // ACT
        Optional<DroneAcuatico> asignado = asignador.asignar(mision, List.of(drone));

        // ASSERT
        assertEquals(EstadoDrone.EN_MISION, asignado.orElseThrow().getEstado());
    }

    @Test
    @DisplayName("Mision CRITICA sin drones disponibles notifica a todos los observadores")
    void misionCritica_sinDrones_notificaObservadores() {
        Mision mision = mision(Prioridad.CRITICA, 200);
        when(estrategiaMock.seleccionar(any(), any())).thenReturn(Optional.empty());

        asignador.asignar(mision, List.of());

        verify(observadorMock, times(1)).notificarFalloAsignacion(mision);
    }

    @Test
    @DisplayName("Mision NORMAL sin drones no dispara la alerta de mision critica")
    void misionNormal_sinDrones_noNotifica() {
        Mision mision = mision(Prioridad.NORMAL, 200);
        when(estrategiaMock.seleccionar(any(), any())).thenReturn(Optional.empty());

        asignador.asignar(mision, List.of());

        verify(observadorMock, never()).notificarFalloAsignacion(any());
    }

    @Test
    @DisplayName("Drone en FALLO durante la asignacion: notifica y busca un alternativo")
    void droneEnFallo_notificaYBuscaAlternativo() {
        DroneAcuatico enFallo = new DroneSemisumergido("AR-07", 90, "Laguna Sur");
        enFallo.setEstado(EstadoDrone.FALLO);
        DroneAcuatico alternativo = new DroneSemisumergido("AR-10", 60, "Laguna Sur");
        Mision mision = mision(Prioridad.ALTA, 400);
        when(estrategiaMock.seleccionar(any(), eq(mision)))
                .thenReturn(Optional.of(enFallo), Optional.of(alternativo));

        Optional<DroneAcuatico> asignado = asignador.asignar(mision, List.of(enFallo, alternativo));

        verify(observadorMock, times(1)).notificarFalloDrone(enFallo, mision);
        assertEquals(alternativo, asignado.orElseThrow());
    }

    @Test
    @DisplayName("Lista de drones vacia retorna Optional vacio")
    void flotaVacia_retornaVacio() {
        Mision mision = mision(Prioridad.BAJA, 100);
        when(estrategiaMock.seleccionar(List.of(), mision)).thenReturn(Optional.empty());

        assertTrue(asignador.asignar(mision, List.of()).isEmpty());
    }

    @Test
    @DisplayName("Bateria exactamente en el umbral minimo (35%) es candidata")
    void bateriaEnUmbral_esCandidata() {
        DroneAcuatico enUmbral = new DroneSuperficial("AR-02", ValidadorMision.BATERIA_MINIMA, "Laguna Sur");
        Mision mision = mision(Prioridad.NORMAL, 100);
        when(estrategiaMock.seleccionar(List.of(enUmbral), mision)).thenReturn(Optional.of(enUmbral));

        assertEquals(enUmbral, asignador.asignar(mision, List.of(enUmbral)).orElseThrow());
    }

    @Test
    @DisplayName("Bateria un punto debajo del umbral (34%) no llega a la estrategia")
    void bateriaBajoUmbral_noEsCandidata() {
        DroneAcuatico bajo = new DroneSuperficial("AR-03", ValidadorMision.BATERIA_MINIMA - 1, "Laguna Sur");
        Mision mision = mision(Prioridad.NORMAL, 100);
        when(estrategiaMock.seleccionar(List.of(), mision)).thenReturn(Optional.empty());

        assertTrue(asignador.asignar(mision, List.of(bajo)).isEmpty());
    }

    @Test
    @DisplayName("El DroneBuceador no se asigna para cargas mayores a 300 g")
    void buceador_cargaMayorA300_noEsCandidato() {
        DroneAcuatico buceador = new DroneBuceador("AR-11", 95, "Laguna Sur");
        Mision mision = mision(Prioridad.NORMAL, 301);
        when(estrategiaMock.seleccionar(List.of(), mision)).thenReturn(Optional.empty());

        assertTrue(asignador.asignar(mision, List.of(buceador)).isEmpty());
    }
}
