package com.eci.aquaport.ejercicio3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignadorAutomaticoTest {

    @Mock EstrategiaSeleccion estrategiaMock;
    @Mock ObservadorMision centroControlMock;
    @Mock ObservadorMision tecnicoMock;

    private AsignadorAutomatico asignador;

    @BeforeEach
    void setUp() {
        asignador = new AsignadorAutomatico(estrategiaMock, new ValidadorMision());
        asignador.registrarObservador(centroControlMock);
        asignador.registrarObservador(tecnicoMock);
    }

    @Test
    @DisplayName("Mision con drone en FALLO notifica a todos los observadores exactamente una vez")
    void droneEnFallo_notificaATodosUnaVez() {
        DroneAcuatico enFallo = new DroneSemisumergido("AR-06", 80, "Laguna Sur");
        enFallo.setEstado(EstadoDrone.FALLO);
        DroneAcuatico alternativo = new DroneSemisumergido("AR-10", 70, "Laguna Sur");
        Mision mision = new Mision("M-301", "Laguna Sur", TipoCarga.SENSOR, 400, Prioridad.ALTA);
        when(estrategiaMock.seleccionar(any(), any()))
                .thenReturn(Optional.of(enFallo), Optional.of(alternativo));

        Optional<DroneAcuatico> asignado = asignador.asignar(mision, List.of(enFallo, alternativo));

        verify(centroControlMock, times(1)).notificarFalloDrone(enFallo, mision);
        verify(tecnicoMock, times(1)).notificarFalloDrone(enFallo, mision);
        assertEquals(alternativo, asignado.orElseThrow());
    }
}
