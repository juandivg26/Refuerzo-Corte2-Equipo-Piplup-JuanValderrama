package com.eci.aquaport.ejercicio3;

import com.eci.aquaport.ejercicio3.dominio.Drone;
import com.eci.aquaport.ejercicio3.dominio.DroneAcuatico;
import com.eci.aquaport.ejercicio3.dominio.RegistroTelemetria;
import com.eci.aquaport.ejercicio3.dominio.TipoDrone;
import com.eci.aquaport.ejercicio3.infraestructura.DroneConMonitoreo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.eci.aquaport.ejercicio3.dominio.ZonaHidrica.EMBALSE_INVESTIGACION;
import static com.eci.aquaport.ejercicio3.dominio.ZonaHidrica.LAB_HIDRICO_CENTRAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DroneConMonitoreoTest {

    @Mock RegistroTelemetria registroMock;

    @Test
    @DisplayName("El decorator registra inicio y fin del tramo, en ese orden")
    void registraTelemetria() {
        Drone monitoreado = new DroneConMonitoreo(
                new DroneAcuatico("AR-01", TipoDrone.BUCEADOR, 90, EMBALSE_INVESTIGACION), registroMock);

        monitoreado.navegar(LAB_HIDRICO_CENTRAL);

        InOrder orden = inOrder(registroMock);
        orden.verify(registroMock).registrar("AR-01", "Inicio tramo EMBALSE_INVESTIGACION -> LAB_HIDRICO_CENTRAL con 90%");
        orden.verify(registroMock).registrar("AR-01", "Fin tramo en LAB_HIDRICO_CENTRAL con 80%");
    }

    @Test
    @DisplayName("El decorator no altera el comportamiento: mismo estado que el drone sin decorar")
    void noAlteraComportamiento() {
        Drone simple = new DroneAcuatico("AR-01", TipoDrone.BUCEADOR, 90, EMBALSE_INVESTIGACION);
        Drone decorado = new DroneConMonitoreo(
                new DroneAcuatico("AR-01", TipoDrone.BUCEADOR, 90, EMBALSE_INVESTIGACION), registroMock);

        simple.navegar(LAB_HIDRICO_CENTRAL);
        decorado.navegar(LAB_HIDRICO_CENTRAL);

        assertEquals(simple.toString(), decorado.toString());
        assertEquals(simple.tipo(), decorado.tipo());
    }

    @Test
    @DisplayName("Los decoradores se pueden apilar: cada capa registra su propia telemetria")
    void decoradoresApilables() {
        Drone base = new DroneAcuatico("AR-02", TipoDrone.SUPERFICIAL, 50, EMBALSE_INVESTIGACION);
        Drone apilado = new DroneConMonitoreo(new DroneConMonitoreo(base, registroMock), registroMock);

        apilado.navegar(LAB_HIDRICO_CENTRAL);

        verify(registroMock, times(4))
                .registrar(eq("AR-02"), anyString());
        assertEquals(40, apilado.bateria());
    }
}
