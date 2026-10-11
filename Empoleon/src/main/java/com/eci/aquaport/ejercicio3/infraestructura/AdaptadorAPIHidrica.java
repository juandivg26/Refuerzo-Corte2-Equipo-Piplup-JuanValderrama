package com.eci.aquaport.ejercicio3.infraestructura;

import com.eci.aquaport.ejercicio3.dominio.CondicionesHidricas;
import com.eci.aquaport.ejercicio3.dominio.NivelAgua;
import com.eci.aquaport.ejercicio3.dominio.ServicioCondicionesHidricas;
import com.eci.aquaport.ejercicio3.dominio.Turbidez;
import com.eci.aquaport.ejercicio3.dominio.ZonaHidrica;

/**
 * Adapter: traduce la API externa (waterLevel, turbidity) al modelo del dominio (NivelAgua, Turbidez).
 * El dominio solo conoce ServicioCondicionesHidricas.
 */
public class AdaptadorAPIHidrica implements ServicioCondicionesHidricas {

    private final ClienteApiHidrica cliente;

    public AdaptadorAPIHidrica(ClienteApiHidrica cliente) {
        this.cliente = cliente;
    }

    @Override
    public CondicionesHidricas consultar(ZonaHidrica zona) {
        RespuestaApiHidrica respuesta = cliente.fetchConditions(zona.name());
        if (respuesta == null) {
            throw new IllegalStateException("La API hidrica no respondio para la zona " + zona);
        }
        return new CondicionesHidricas(new NivelAgua(respuesta.waterLevel()), new Turbidez(respuesta.turbidity()));
    }
}
