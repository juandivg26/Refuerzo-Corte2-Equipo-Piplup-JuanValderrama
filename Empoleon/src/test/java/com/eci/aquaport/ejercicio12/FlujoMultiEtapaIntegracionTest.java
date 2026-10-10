package com.eci.aquaport.ejercicio12;

import com.eci.aquaport.ejercicio12.aplicacion.EjecutorRuta;
import com.eci.aquaport.ejercicio12.aplicacion.PlanificadorRuta;
import com.eci.aquaport.ejercicio12.aplicacion.ServicioAsignacion;
import com.eci.aquaport.ejercicio12.dominio.Drone;
import com.eci.aquaport.ejercicio12.dominio.DroneAcuatico;
import com.eci.aquaport.ejercicio12.dominio.EstadoRuta;
import com.eci.aquaport.ejercicio12.dominio.NotificadorWaypoints;
import com.eci.aquaport.ejercicio12.dominio.ResultadoRuta;
import com.eci.aquaport.ejercicio12.dominio.RutaNoPlanificableException;
import com.eci.aquaport.ejercicio12.dominio.SolicitudMultiEtapa;
import com.eci.aquaport.ejercicio12.dominio.TipoDrone;
import com.eci.aquaport.ejercicio12.dominio.Tramo;
import com.eci.aquaport.ejercicio12.dominio.ZonaHidrica;
import com.eci.aquaport.ejercicio12.infraestructura.AdaptadorAPIHidrica;
import com.eci.aquaport.ejercicio12.infraestructura.ClienteApiHidricaSimulado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static com.eci.aquaport.ejercicio12.dominio.ZonaHidrica.EMBALSE_INVESTIGACION;
import static com.eci.aquaport.ejercicio12.dominio.ZonaHidrica.LAB_HIDRICO_CENTRAL;
import static com.eci.aquaport.ejercicio12.dominio.ZonaHidrica.LAGUNA_RESERVA;
import static com.eci.aquaport.ejercicio12.dominio.ZonaHidrica.RED_CANALES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;

/**
 * Integracion de las 3 capas con objetos reales: adaptador de la API hidrica (infraestructura),
 * cadena de validacion (dominio), planificador y ejecutor (aplicacion). Solo el notificador es mock.
 */
@ExtendWith(MockitoExtension.class)
class FlujoMultiEtapaIntegracionTest {

    @Mock NotificadorWaypoints notificadorMock;

    private PlanificadorRuta planificador;
    private EjecutorRuta ejecutor;
    private List<Drone> flota;

    @BeforeEach
    void setUp() {
        ServicioAsignacion asignacion = new ServicioAsignacion(ServicioAsignacion.cadenaEstandar(
                Set.of(ZonaHidrica.values()), new AdaptadorAPIHidrica(new ClienteApiHidricaSimulado())));
        planificador = new PlanificadorRuta(asignacion);
        ejecutor = new EjecutorRuta(asignacion, notificadorMock);
        flota = List.of(
                new DroneAcuatico("AR-01", TipoDrone.SEMISUMERGIDO, 85, EMBALSE_INVESTIGACION),
                new DroneAcuatico("AR-16", TipoDrone.SUPERFICIAL, 75, RED_CANALES),
                new DroneAcuatico("AR-31", TipoDrone.SEMISUMERGIDO, 90, LAGUNA_RESERVA));
    }

    @Test
    @DisplayName("Solicitud -> planificacion -> asignacion por tramos -> notificacion de waypoints")
    void flujoCompleto() {
        SolicitudMultiEtapa solicitud = new SolicitudMultiEtapa("S-900", 450,
                List.of(EMBALSE_INVESTIGACION, RED_CANALES, LAB_HIDRICO_CENTRAL));

        List<Tramo> ruta = planificador.planificar(solicitud, flota);
        ResultadoRuta resultado = ejecutor.ejecutar(solicitud, ruta, flota);

        assertEquals(EstadoRuta.COMPLETADA, resultado.estado());
        assertEquals(List.of("AR-01", "AR-16"), ruta.stream().map(t -> t.drone().id()).toList());
        InOrder orden = inOrder(notificadorMock);
        orden.verify(notificadorMock).waypointAlcanzado("S-900", RED_CANALES, "AR-01");
        orden.verify(notificadorMock).waypointAlcanzado("S-900", LAB_HIDRICO_CENTRAL, "AR-16");
    }

    @Test
    @DisplayName("La turbidez real de la Laguna (64 NTU, via adaptador) impide planificar un tramo hacia ella")
    void condicionesRealesDeLaApiBloqueanTramo() {
        SolicitudMultiEtapa haciaLaguna = new SolicitudMultiEtapa("S-901", 200, List.of(RED_CANALES, LAGUNA_RESERVA));

        assertThrows(RutaNoPlanificableException.class, () -> planificador.planificar(haciaLaguna, flota));
    }
}
