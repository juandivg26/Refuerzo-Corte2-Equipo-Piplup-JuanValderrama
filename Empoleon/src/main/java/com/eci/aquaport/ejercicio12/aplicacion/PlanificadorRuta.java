package com.eci.aquaport.ejercicio12.aplicacion;

import com.eci.aquaport.ejercicio12.dominio.Drone;
import com.eci.aquaport.ejercicio12.dominio.Mision;
import com.eci.aquaport.ejercicio12.dominio.RutaNoPlanificableException;
import com.eci.aquaport.ejercicio12.dominio.SolicitudMultiEtapa;
import com.eci.aquaport.ejercicio12.dominio.Tramo;
import com.eci.aquaport.ejercicio12.dominio.ZonaHidrica;

import java.util.ArrayList;
import java.util.List;

/** Caso de uso: planificar la ruta multi-etapa asignando un drone distinto a cada tramo. */
public class PlanificadorRuta {

    private final ServicioAsignacion asignacion;

    public PlanificadorRuta(ServicioAsignacion asignacion) {
        this.asignacion = asignacion;
    }

    public List<Tramo> planificar(SolicitudMultiEtapa solicitud, List<Drone> flota) {
        List<Drone> libres = new ArrayList<>(flota);
        List<Tramo> ruta = new ArrayList<>();
        for (int i = 0; i < solicitud.cantidadTramos(); i++) {
            ZonaHidrica origen = solicitud.origenTramo(i);
            Mision tramo = solicitud.tramo(i);
            Drone drone = asignacion.asignarDesde(origen, tramo, libres)
                    .orElseThrow(() -> new RutaNoPlanificableException("Sin drone apto para el tramo " + origen
                            + " -> " + tramo.destino() + " de la solicitud " + solicitud.id()));
            libres.remove(drone);
            ruta.add(new Tramo(origen, tramo.destino(), drone));
        }
        return ruta;
    }
}
