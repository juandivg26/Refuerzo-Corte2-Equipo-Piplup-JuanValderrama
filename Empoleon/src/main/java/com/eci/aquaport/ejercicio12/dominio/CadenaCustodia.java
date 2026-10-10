package com.eci.aquaport.ejercicio12.dominio;

import java.util.ArrayList;
import java.util.List;

/** Registro de quien tuvo la muestra en cada momento de la ruta. */
public class CadenaCustodia {

    private final List<String> registros = new ArrayList<>();

    public void traspaso(ZonaHidrica waypoint, Drone entrega, Drone recibe) {
        registros.add("Traspaso en " + waypoint + ": " + entrega.id() + " -> " + recibe.id());
    }

    public void interrumpir(String motivo) {
        registros.add("INTERRUMPIDA: " + motivo);
    }

    public ResultadoRuta cerrar(EstadoRuta estado) {
        return new ResultadoRuta(estado, List.copyOf(registros));
    }
}
