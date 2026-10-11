package com.eci.aquaport.ejercicio12;

import com.eci.aquaport.ejercicio12.aplicacion.PlanificadorRuta;
import com.eci.aquaport.ejercicio12.aplicacion.ServicioAsignacion;
import com.eci.aquaport.ejercicio12.dominio.CondicionesHidricas;
import com.eci.aquaport.ejercicio12.dominio.Drone;
import com.eci.aquaport.ejercicio12.dominio.DroneAcuatico;
import com.eci.aquaport.ejercicio12.dominio.NivelAgua;
import com.eci.aquaport.ejercicio12.dominio.RutaNoPlanificableException;
import com.eci.aquaport.ejercicio12.dominio.ServicioCondicionesHidricas;
import com.eci.aquaport.ejercicio12.dominio.SolicitudMultiEtapa;
import com.eci.aquaport.ejercicio12.dominio.TipoDrone;
import com.eci.aquaport.ejercicio12.dominio.Tramo;
import com.eci.aquaport.ejercicio12.dominio.Turbidez;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlanificadorRutaTest {

    private static final CondicionesHidricas AGUA_BUENA = new CondicionesHidricas(new NivelAgua(2.0), new Turbidez(10));

    @Mock ServicioCondicionesHidricas condicionesMock;

    private PlanificadorRuta planificador;

    private final Drone enEmbalse = new DroneAcuatico("AR-01", TipoDrone.SEMISUMERGIDO, 70, EMBALSE_INVESTIGACION);
    private final Drone enCanales = new DroneAcuatico("AR-16", TipoDrone.SEMISUMERGIDO, 60, RED_CANALES);
    private final Drone lejanoConMasBateria = new DroneAcuatico("AR-31", TipoDrone.SEMISUMERGIDO, 99, LAGUNA_RESERVA);

    @BeforeEach
    void setUp() {
        when(condicionesMock.consultar(any())).thenReturn(AGUA_BUENA);
        ServicioAsignacion asignacion = new ServicioAsignacion(ServicioAsignacion.cadenaEstandar(
                Set.of(RED_CANALES, LAB_HIDRICO_CENTRAL, LAGUNA_RESERVA), condicionesMock));
        planificador = new PlanificadorRuta(asignacion);
    }

    private SolicitudMultiEtapa embalseCanalesLab() {
        return new SolicitudMultiEtapa("S-1", 400, List.of(EMBALSE_INVESTIGACION, RED_CANALES, LAB_HIDRICO_CENTRAL));
    }

    @Test
    @DisplayName("Asigna un drone por tramo, prefiriendo el que ya esta en la zona de origen del tramo")
    void unDronePorTramoEnZonaDeOrigen() {
        List<Tramo> ruta = planificador.planificar(embalseCanalesLab(), List.of(lejanoConMasBateria, enEmbalse, enCanales));

        assertEquals(List.of(
                new Tramo(EMBALSE_INVESTIGACION, RED_CANALES, enEmbalse),
                new Tramo(RED_CANALES, LAB_HIDRICO_CENTRAL, enCanales)), ruta);
    }

    @Test
    @DisplayName("Un drone no se reutiliza en dos tramos; si no hay otro en la zona se toma el mejor de fuera")
    void noReutilizaDrones() {
        List<Tramo> ruta = planificador.planificar(embalseCanalesLab(), List.of(enEmbalse, lejanoConMasBateria));

        assertEquals(enEmbalse, ruta.get(0).drone());
        assertEquals(lejanoConMasBateria, ruta.get(1).drone());
    }

    @Test
    @DisplayName("Flujo alterno: zona destino temporalmente inactiva -> la ruta no se puede planificar")
    void zonaDestinoInactiva() {
        SolicitudMultiEtapa haciaEmbalse = new SolicitudMultiEtapa("S-2", 200, List.of(RED_CANALES, EMBALSE_INVESTIGACION));
        List<Drone> flota = List.of(enCanales);

        RutaNoPlanificableException error = assertThrows(RutaNoPlanificableException.class,
                () -> planificador.planificar(haciaEmbalse, flota));
        assertTrue(error.getMessage().contains("RED_CANALES -> EMBALSE_INVESTIGACION"));
    }

    @Test
    @DisplayName("Flujo alterno: condicion hidrica adversa en un tramo especifico -> se rechaza indicando el tramo")
    void condicionAdversaEnTramo() {
        when(condicionesMock.consultar(LAB_HIDRICO_CENTRAL))
                .thenReturn(new CondicionesHidricas(new NivelAgua(2.0), new Turbidez(80)));
        SolicitudMultiEtapa solicitud = embalseCanalesLab();
        List<Drone> flota = List.of(enEmbalse, enCanales);

        RutaNoPlanificableException error = assertThrows(RutaNoPlanificableException.class,
                () -> planificador.planificar(solicitud, flota));
        assertTrue(error.getMessage().contains("RED_CANALES -> LAB_HIDRICO_CENTRAL"));
    }

    @Test
    @DisplayName("Una solicitud necesita al menos origen y destino")
    void solicitudSinTramos() {
        List<com.eci.aquaport.ejercicio12.dominio.ZonaHidrica> soloOrigen = List.of(RED_CANALES);

        assertThrows(IllegalArgumentException.class, () -> new SolicitudMultiEtapa("S-3", 100, soloOrigen));
    }
}
