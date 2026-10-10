package com.eci.aquaport.ejercicio12.dominio;

import java.util.List;

/** Solicitud Enterprise: origen, waypoints intermedios y destino final, en orden. */
public record SolicitudMultiEtapa(
        String id,
        int cargaGramos,
        List<ZonaHidrica> paradas
) {
    public SolicitudMultiEtapa {
        if (paradas.size() < 2) {
            throw new IllegalArgumentException("Una solicitud necesita al menos origen y destino");
        }
        paradas = List.copyOf(paradas);
    }

    public int cantidadTramos() {
        return paradas.size() - 1;
    }

    public ZonaHidrica origenTramo(int i) {
        return paradas.get(i);
    }

    /** El tramo i como mision simple, para validarlo con la misma cadena que una mision directa. */
    public Mision tramo(int i) {
        return new Mision(id + "-T" + (i + 1), cargaGramos, paradas.get(i + 1));
    }
}
