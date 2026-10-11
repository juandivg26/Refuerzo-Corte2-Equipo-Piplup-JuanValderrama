package com.eci.aquaport.ejercicio3.dominio;

import java.util.Optional;

public class ValidadorCondicionesHidricas extends ValidadorMision {

    public static final double TURBIDEZ_MAXIMA_NTU = 50;
    public static final double NIVEL_MINIMO_METROS = 0.5;

    private final ServicioCondicionesHidricas servicio;

    public ValidadorCondicionesHidricas(ServicioCondicionesHidricas servicio) {
        this.servicio = servicio;
    }

    @Override
    protected Optional<String> revisar(Drone drone, Mision mision) {
        CondicionesHidricas condiciones = servicio.consultar(mision.destino());
        if (condiciones.turbidez().ntu() > TURBIDEZ_MAXIMA_NTU) {
            return Optional.of("Condiciones adversas en " + mision.destino() + ": turbidez "
                    + condiciones.turbidez().ntu() + " NTU");
        }
        if (condiciones.nivelAgua().metros() < NIVEL_MINIMO_METROS) {
            return Optional.of("Nivel de agua insuficiente en " + mision.destino() + ": "
                    + condiciones.nivelAgua().metros() + " m");
        }
        return Optional.empty();
    }
}
