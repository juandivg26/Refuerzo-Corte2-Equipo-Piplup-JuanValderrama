package com.eci.aquaport.ejercicio3;

import com.eci.aquaport.ejercicio3.aplicacion.ServicioAsignacion;
import com.eci.aquaport.ejercicio3.dominio.CondicionesHidricas;
import com.eci.aquaport.ejercicio3.dominio.Drone;
import com.eci.aquaport.ejercicio3.dominio.DroneAcuatico;
import com.eci.aquaport.ejercicio3.dominio.Mision;
import com.eci.aquaport.ejercicio3.dominio.NivelAgua;
import com.eci.aquaport.ejercicio3.dominio.ServicioCondicionesHidricas;
import com.eci.aquaport.ejercicio3.dominio.TipoDrone;
import com.eci.aquaport.ejercicio3.dominio.Turbidez;
import com.eci.aquaport.ejercicio3.dominio.ValidadorBateria;
import com.eci.aquaport.ejercicio3.dominio.ValidadorMision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.eci.aquaport.ejercicio3.dominio.ZonaHidrica.EMBALSE_INVESTIGACION;
import static com.eci.aquaport.ejercicio3.dominio.ZonaHidrica.LAB_HIDRICO_CENTRAL;
import static com.eci.aquaport.ejercicio3.dominio.ZonaHidrica.LAGUNA_RESERVA;
import static com.eci.aquaport.ejercicio3.dominio.ZonaHidrica.RED_CANALES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadenaValidacionTest {

    @Mock ServicioCondicionesHidricas condicionesMock;

    private ValidadorMision cadena;
    private final Mision haciaLab = new Mision("M-1", 250, LAB_HIDRICO_CENTRAL);
    private final Drone buceador = new DroneAcuatico("AR-01", TipoDrone.BUCEADOR, 90, EMBALSE_INVESTIGACION);

    @BeforeEach
    void setUp() {
        cadena = ServicioAsignacion.cadenaEstandar(Set.of(EMBALSE_INVESTIGACION, LAB_HIDRICO_CENTRAL), condicionesMock);
    }

    private void aguaEnBuenEstado() {
        when(condicionesMock.consultar(any())).thenReturn(new CondicionesHidricas(new NivelAgua(2.0), new Turbidez(10)));
    }

    @Test
    @DisplayName("La cadena se detiene en el primer validador que falla: el siguiente nunca se ejecuta")
    void cadenaSeDetieneEnPrimerFallo() {
        ValidadorMision bateria = new ValidadorBateria();
        ValidadorMision siguienteMock = mock(ValidadorMision.class);
        bateria.enlazar(siguienteMock);
        Drone sinBateria = new DroneAcuatico("AR-31", TipoDrone.SEMISUMERGIDO, 20, LAGUNA_RESERVA);

        Optional<String> rechazo = bateria.validar(sinBateria, haciaLab);

        assertTrue(rechazo.orElseThrow().startsWith("Bateria insuficiente"));
        verify(siguienteMock, never()).validar(any(), any());
    }

    @Test
    @DisplayName("Si un eslabon aprueba, la cadena delega en el siguiente")
    void cadenaDelegaSiAprueba() {
        ValidadorMision bateria = new ValidadorBateria();
        ValidadorMision siguienteMock = mock(ValidadorMision.class);
        bateria.enlazar(siguienteMock);
        when(siguienteMock.validar(buceador, haciaLab)).thenReturn(Optional.empty());

        assertTrue(bateria.validar(buceador, haciaLab).isEmpty());
        verify(siguienteMock).validar(buceador, haciaLab);
    }

    @Test
    @DisplayName("Carga mayor a la capacidad del tipo se rechaza antes de consultar la API hidrica")
    void capacidadExcedida() {
        Mision pesada = new Mision("M-2", 301, LAB_HIDRICO_CENTRAL);

        assertEquals("Carga de 301 g supera la capacidad de 300 g del BUCEADOR",
                cadena.validar(buceador, pesada).orElseThrow());
        verify(condicionesMock, never()).consultar(any());
    }

    @Test
    @DisplayName("Zona destino inactiva se rechaza")
    void zonaInactiva() {
        Mision haciaCanales = new Mision("M-3", 100, RED_CANALES);

        assertEquals("La zona destino RED_CANALES esta inactiva", cadena.validar(buceador, haciaCanales).orElseThrow());
    }

    @Test
    @DisplayName("Turbidez por encima del limite es condicion adversa")
    void condicionesAdversas() {
        when(condicionesMock.consultar(LAB_HIDRICO_CENTRAL))
                .thenReturn(new CondicionesHidricas(new NivelAgua(2.0), new Turbidez(50.1)));

        assertTrue(cadena.validar(buceador, haciaLab).orElseThrow().startsWith("Condiciones adversas"));
    }

    @Test
    @DisplayName("Nivel de agua por debajo del minimo se rechaza")
    void nivelInsuficiente() {
        when(condicionesMock.consultar(LAB_HIDRICO_CENTRAL))
                .thenReturn(new CondicionesHidricas(new NivelAgua(0.49), new Turbidez(10)));

        assertTrue(cadena.validar(buceador, haciaLab).orElseThrow().startsWith("Nivel de agua insuficiente"));
    }

    @Test
    @DisplayName("Un drone que cumple todo pasa la cadena completa")
    void dronePasaTodaLaCadena() {
        aguaEnBuenEstado();

        assertTrue(cadena.validar(buceador, haciaLab).isEmpty());
    }

    @Test
    @DisplayName("El servicio asigna el drone de mayor bateria que pasa la cadena")
    void servicioAsignaMayorBateriaValido() {
        aguaEnBuenEstado();
        Drone superficial = new DroneAcuatico("AR-16", TipoDrone.SUPERFICIAL, 95, RED_CANALES);
        Mision mediana = new Mision("M-4", 400, LAB_HIDRICO_CENTRAL);

        assertEquals(superficial, new ServicioAsignacion(cadena).asignar(mediana, List.of(buceador, superficial)).orElseThrow());
    }
}
