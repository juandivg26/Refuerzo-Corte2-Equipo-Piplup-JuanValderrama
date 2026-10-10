package com.eci.aquaport.ejercicio12.infraestructura;

import java.util.Map;

/** shortcut: valores fijos por zona en lugar de HTTP real; reemplazar por un cliente HTTP cuando exista la API. */
public class ClienteApiHidricaSimulado implements ClienteApiHidrica {

    private static final Map<String, RespuestaApiHidrica> DATOS = Map.of(
            "EMBALSE_INVESTIGACION", new RespuestaApiHidrica("EMBALSE_INVESTIGACION", 12.0, 8.5),
            "RED_CANALES", new RespuestaApiHidrica("RED_CANALES", 1.2, 22.0),
            "LAGUNA_RESERVA", new RespuestaApiHidrica("LAGUNA_RESERVA", 3.4, 64.0),
            "LAB_HIDRICO_CENTRAL", new RespuestaApiHidrica("LAB_HIDRICO_CENTRAL", 0.8, 5.0));

    @Override
    public RespuestaApiHidrica fetchConditions(String zoneCode) {
        return DATOS.get(zoneCode);
    }
}
