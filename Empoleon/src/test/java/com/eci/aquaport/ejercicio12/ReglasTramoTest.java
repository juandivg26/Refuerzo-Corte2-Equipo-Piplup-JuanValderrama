package com.eci.aquaport.ejercicio12;

import com.eci.aquaport.ejercicio12.aplicacion.ServicioAsignacion;
import com.eci.aquaport.ejercicio12.dominio.CondicionesHidricas;
import com.eci.aquaport.ejercicio12.dominio.Drone;
import com.eci.aquaport.ejercicio12.dominio.DroneAcuatico;
import com.eci.aquaport.ejercicio12.dominio.Mision;
import com.eci.aquaport.ejercicio12.dominio.NivelAgua;
import com.eci.aquaport.ejercicio12.dominio.ServicioCondicionesHidricas;
import com.eci.aquaport.ejercicio12.dominio.TipoDrone;
import com.eci.aquaport.ejercicio12.dominio.Turbidez;
import com.eci.aquaport.ejercicio12.dominio.ValidadorMision;
import com.eci.aquaport.ejercicio12.dominio.ZonaHidrica;
import com.eci.aquaport.ejercicio12.infraestructura.AdaptadorAPIHidrica;
import com.eci.aquaport.ejercicio12.infraestructura.ClienteApiHidrica;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static com.eci.aquaport.ejercicio12.dominio.ZonaHidrica.LAB_HIDRICO_CENTRAL;
import static com.eci.aquaport.ejercicio12.dominio.ZonaHidrica.RED_CANALES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** Reglas de la cadena aplicadas a un tramo: las ramas de rechazo que la ruta multi-etapa debe respetar. */
@ExtendWith(MockitoExtension.class)
class ReglasTramoTest {

    @Mock ServicioCondicionesHidricas condicionesMock;
    @Mock ClienteApiHidrica clienteMock;

    private final Mision tramoLigero = new Mision("S-1-T1", 200, LAB_HIDRICO_CENTRAL);

    private ValidadorMision cadena() {
        return ServicioAsignacion.cadenaEstandar(Set.of(ZonaHidrica.values()), condicionesMock);
    }

    @Test
    @DisplayName("Un buceador no puede llevar un tramo de 301 g aunque tenga bateria")
    void capacidadDelTipoEnTramo() {
        Drone buceador = new DroneAcuatico("AR-02", TipoDrone.BUCEADOR, 95, RED_CANALES);
        Mision pesado = new Mision("S-1-T2", 301, LAB_HIDRICO_CENTRAL);

        assertEquals("Carga de 301 g supera la capacidad de 300 g del BUCEADOR",
                cadena().validar(buceador, pesado).orElseThrow());
    }

    @Test
    @DisplayName("Un waypoint con nivel de agua bajo el minimo bloquea el tramo")
    void nivelBajoEnWaypoint() {
        when(condicionesMock.consultar(any())).thenReturn(new CondicionesHidricas(new NivelAgua(0.3), new Turbidez(5)));
        Drone drone = new DroneAcuatico("AR-16", TipoDrone.SUPERFICIAL, 80, RED_CANALES);

        assertTrue(cadena().validar(drone, tramoLigero).orElseThrow().startsWith("Nivel de agua insuficiente"));
    }

    @Test
    @DisplayName("Lecturas negativas o NaN del sensor no se aceptan como condiciones del dominio")
    void lecturasInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> new NivelAgua(-1));
        assertThrows(IllegalArgumentException.class, () -> new NivelAgua(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new Turbidez(-1));
        assertThrows(IllegalArgumentException.class, () -> new Turbidez(Double.NaN));
    }

    @Test
    @DisplayName("Si la API hidrica no responde, el tramo no se valida con datos inventados")
    void apiCaida() {
        when(clienteMock.fetchConditions("RED_CANALES")).thenReturn(null);
        AdaptadorAPIHidrica adaptador = new AdaptadorAPIHidrica(clienteMock);

        assertThrows(IllegalStateException.class, () -> adaptador.consultar(RED_CANALES));
    }
}
