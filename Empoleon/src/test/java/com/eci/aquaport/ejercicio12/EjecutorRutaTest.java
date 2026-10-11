package com.eci.aquaport.ejercicio12;

import com.eci.aquaport.ejercicio12.aplicacion.EjecutorRuta;
import com.eci.aquaport.ejercicio12.aplicacion.ServicioAsignacion;
import com.eci.aquaport.ejercicio12.dominio.CondicionesHidricas;
import com.eci.aquaport.ejercicio12.dominio.Drone;
import com.eci.aquaport.ejercicio12.dominio.DroneAcuatico;
import com.eci.aquaport.ejercicio12.dominio.EstadoRuta;
import com.eci.aquaport.ejercicio12.dominio.NivelAgua;
import com.eci.aquaport.ejercicio12.dominio.NotificadorWaypoints;
import com.eci.aquaport.ejercicio12.dominio.ResultadoRuta;
import com.eci.aquaport.ejercicio12.dominio.ServicioCondicionesHidricas;
import com.eci.aquaport.ejercicio12.dominio.SolicitudMultiEtapa;
import com.eci.aquaport.ejercicio12.dominio.TipoDrone;
import com.eci.aquaport.ejercicio12.dominio.Tramo;
import com.eci.aquaport.ejercicio12.dominio.Turbidez;
import com.eci.aquaport.ejercicio12.dominio.ZonaHidrica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Set;

import static com.eci.aquaport.ejercicio12.dominio.ZonaHidrica.EMBALSE_INVESTIGACION;
import static com.eci.aquaport.ejercicio12.dominio.ZonaHidrica.LAB_HIDRICO_CENTRAL;
import static com.eci.aquaport.ejercicio12.dominio.ZonaHidrica.LAGUNA_RESERVA;
import static com.eci.aquaport.ejercicio12.dominio.ZonaHidrica.RED_CANALES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EjecutorRutaTest {

    @Mock ServicioCondicionesHidricas condicionesMock;
    @Mock NotificadorWaypoints notificadorMock;

    private EjecutorRuta ejecutor;
    private final SolicitudMultiEtapa solicitud =
            new SolicitudMultiEtapa("S-1", 400, List.of(EMBALSE_INVESTIGACION, RED_CANALES, LAB_HIDRICO_CENTRAL));

    private final DroneAcuatico tramo1 = new DroneAcuatico("AR-01", TipoDrone.SEMISUMERGIDO, 70, EMBALSE_INVESTIGACION);
    private final DroneAcuatico tramo2 = new DroneAcuatico("AR-16", TipoDrone.SEMISUMERGIDO, 60, RED_CANALES);
    private final DroneAcuatico reserva = new DroneAcuatico("AR-17", TipoDrone.SEMISUMERGIDO, 80, RED_CANALES);

    @BeforeEach
    void setUp() {
        when(condicionesMock.consultar(any())).thenReturn(new CondicionesHidricas(new NivelAgua(2.0), new Turbidez(10)));
        ServicioAsignacion asignacion = new ServicioAsignacion(ServicioAsignacion.cadenaEstandar(
                Set.of(ZonaHidrica.values()), condicionesMock));
        ejecutor = new EjecutorRuta(asignacion, notificadorMock);
    }

    private List<Tramo> ruta() {
        return List.of(new Tramo(EMBALSE_INVESTIGACION, RED_CANALES, tramo1), new Tramo(RED_CANALES, LAB_HIDRICO_CENTRAL, tramo2));
    }

    @Test
    @DisplayName("Camino feliz: recorre los tramos, notifica cada waypoint en orden y registra el traspaso de custodia")
    void rutaCompleta() {
        ResultadoRuta resultado = ejecutor.ejecutar(solicitud, ruta(), List.of(tramo1, tramo2));

        assertEquals(EstadoRuta.COMPLETADA, resultado.estado());
        InOrder orden = inOrder(notificadorMock);
        orden.verify(notificadorMock).waypointAlcanzado("S-1", RED_CANALES, "AR-01");
        orden.verify(notificadorMock).waypointAlcanzado("S-1", LAB_HIDRICO_CENTRAL, "AR-16");
        assertTrue(resultado.custodia().contains("Traspaso en RED_CANALES: AR-01 -> AR-16"));
    }

    @Test
    @DisplayName("Flujo alterno: fallo de drone en un waypoint intermedio -> reasignacion automatica")
    void falloEnWaypointReasigna() {
        tramo2.reportarFallo();

        ResultadoRuta resultado = ejecutor.ejecutar(solicitud, ruta(), List.of(tramo1, tramo2, reserva));

        assertEquals(EstadoRuta.COMPLETADA, resultado.estado());
        verify(notificadorMock).droneReasignado("S-1", "AR-16", "AR-17");
        verify(notificadorMock).waypointAlcanzado("S-1", LAB_HIDRICO_CENTRAL, "AR-17");
    }

    @Test
    @DisplayName("Flujo alterno: drone con bateria critica en mitad de ruta -> se reasigna su tramo")
    void bateriaCriticaEnRutaReasigna() {
        DroneAcuatico justo = new DroneAcuatico("AR-18", TipoDrone.SEMISUMERGIDO, 40, RED_CANALES);
        justo.navegar(RED_CANALES);
        List<Tramo> ruta = List.of(new Tramo(EMBALSE_INVESTIGACION, RED_CANALES, tramo1), new Tramo(RED_CANALES, LAB_HIDRICO_CENTRAL, justo));

        ResultadoRuta resultado = ejecutor.ejecutar(solicitud, ruta, List.of(tramo1, justo, reserva));

        assertEquals(EstadoRuta.COMPLETADA, resultado.estado());
        verify(notificadorMock).droneReasignado("S-1", "AR-18", "AR-17");
    }

    @Test
    @DisplayName("Flujo alterno: cadena de custodia interrumpida si el drone falla durante el tramo con la muestra")
    void custodiaInterrumpida() {
        DroneAcuatico fallaEnCamino = new DroneAcuatico("AR-02", TipoDrone.SEMISUMERGIDO, 70, EMBALSE_INVESTIGACION) {
            @Override
            public void navegar(ZonaHidrica destino) {
                super.navegar(destino);
                reportarFallo();
            }
        };
        List<Tramo> ruta = List.of(new Tramo(EMBALSE_INVESTIGACION, RED_CANALES, fallaEnCamino), new Tramo(RED_CANALES, LAB_HIDRICO_CENTRAL, tramo2));

        ResultadoRuta resultado = ejecutor.ejecutar(solicitud, ruta, List.of(fallaEnCamino, tramo2));

        assertEquals(EstadoRuta.CUSTODIA_INTERRUMPIDA, resultado.estado());
        verify(notificadorMock, never()).waypointAlcanzado(anyString(), any(), anyString());
        assertTrue(resultado.custodia().stream().anyMatch(r -> r.startsWith("INTERRUMPIDA")));
    }

    @Test
    @DisplayName("Sin drone de reemplazo la ruta queda FALLIDA")
    void sinReemplazoFalla() {
        tramo2.reportarFallo();

        ResultadoRuta resultado = ejecutor.ejecutar(solicitud, ruta(), List.of(tramo1, tramo2));

        assertEquals(EstadoRuta.FALLIDA, resultado.estado());
    }

    @Test
    @DisplayName("Un reemplazo de otra zona primero se desplaza al waypoint para recibir la muestra")
    void reemplazoSeDesplazaAlWaypoint() {
        tramo2.reportarFallo();
        Drone lejano = new DroneAcuatico("AR-31", TipoDrone.SEMISUMERGIDO, 90, LAGUNA_RESERVA);

        ResultadoRuta resultado = ejecutor.ejecutar(solicitud, ruta(), List.of(tramo1, tramo2, lejano));

        assertEquals(EstadoRuta.COMPLETADA, resultado.estado());
        assertTrue(resultado.custodia().contains("Traspaso en RED_CANALES: AR-01 -> AR-31"));
    }
}
