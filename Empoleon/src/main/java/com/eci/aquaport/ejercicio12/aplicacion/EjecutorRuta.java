package com.eci.aquaport.ejercicio12.aplicacion;

import com.eci.aquaport.ejercicio12.dominio.CadenaCustodia;
import com.eci.aquaport.ejercicio12.dominio.Drone;
import com.eci.aquaport.ejercicio12.dominio.EstadoDrone;
import com.eci.aquaport.ejercicio12.dominio.EstadoRuta;
import com.eci.aquaport.ejercicio12.dominio.Mision;
import com.eci.aquaport.ejercicio12.dominio.NotificadorWaypoints;
import com.eci.aquaport.ejercicio12.dominio.ResultadoRuta;
import com.eci.aquaport.ejercicio12.dominio.SolicitudMultiEtapa;
import com.eci.aquaport.ejercicio12.dominio.Tramo;

import java.util.List;
import java.util.Optional;

/**
 * Caso de uso: ejecutar la ruta tramo a tramo. Antes de cada tramo revalida al drone (fallo o bateria critica)
 * y lo reasigna si hace falta; en cada waypoint registra el traspaso de custodia y notifica el avance.
 */
public class EjecutorRuta {

    private final ServicioAsignacion asignacion;
    private final NotificadorWaypoints notificador;

    public EjecutorRuta(ServicioAsignacion asignacion, NotificadorWaypoints notificador) {
        this.asignacion = asignacion;
        this.notificador = notificador;
    }

    public ResultadoRuta ejecutar(SolicitudMultiEtapa solicitud, List<Tramo> ruta, List<Drone> flota) {
        CadenaCustodia custodia = new CadenaCustodia();
        Drone anterior = null;
        for (int i = 0; i < ruta.size(); i++) {
            Optional<Drone> drone = droneOperativo(solicitud, i, ruta, flota);
            if (drone.isEmpty()) {
                custodia.interrumpir("sin drone de reemplazo para el tramo " + (i + 1));
                return custodia.cerrar(EstadoRuta.FALLIDA);
            }
            if (!recorrer(solicitud, ruta.get(i), drone.get(), anterior, custodia)) {
                return custodia.cerrar(EstadoRuta.CUSTODIA_INTERRUMPIDA);
            }
            anterior = drone.get();
        }
        return custodia.cerrar(EstadoRuta.COMPLETADA);
    }

    /** El drone planificado si sigue apto; si no (FALLO o bateria critica), un reemplazo que no este en la ruta. */
    private Optional<Drone> droneOperativo(SolicitudMultiEtapa solicitud, int i, List<Tramo> ruta, List<Drone> flota) {
        Tramo tramo = ruta.get(i);
        Mision mision = solicitud.tramo(i);
        if (asignacion.esApto(tramo.drone(), mision)) {
            return Optional.of(tramo.drone());
        }
        List<Drone> enRuta = ruta.stream().map(Tramo::drone).toList();
        List<Drone> candidatos = flota.stream().filter(d -> !enRuta.contains(d)).toList();
        Optional<Drone> reemplazo = asignacion.asignarDesde(tramo.origen(), mision, candidatos);
        reemplazo.ifPresent(r -> notificador.droneReasignado(solicitud.id(), tramo.drone().id(), r.id()));
        return reemplazo;
    }

    /** false si el drone fallo durante el tramo: la muestra se pierde y la custodia se interrumpe. */
    private boolean recorrer(SolicitudMultiEtapa solicitud, Tramo tramo, Drone drone, Drone anterior, CadenaCustodia custodia) {
        if (drone.zona() != tramo.origen()) {
            drone.navegar(tramo.origen());
        }
        if (anterior != null) {
            custodia.traspaso(tramo.origen(), anterior, drone);
        }
        drone.navegar(tramo.destino());
        if (drone.estado() == EstadoDrone.FALLO) {
            custodia.interrumpir(drone.id() + " fallo entre " + tramo.origen() + " y " + tramo.destino());
            return false;
        }
        notificador.waypointAlcanzado(solicitud.id(), tramo.destino(), drone.id());
        return true;
    }
}
