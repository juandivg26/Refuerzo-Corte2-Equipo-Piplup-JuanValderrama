package com.eci.aquaport.ejercicio3;

import java.util.List;
import java.util.Optional;

public class AsignadorAutomatico {

    private final EstrategiaSeleccion estrategia;
    private final ValidadorMision validador;

    public AsignadorAutomatico(EstrategiaSeleccion estrategia, ValidadorMision validador) {
        this.estrategia = estrategia;
        this.validador = validador;
    }

    public Optional<DroneAcuatico> asignar(Mision mision, List<DroneAcuatico> flota) {
        List<DroneAcuatico> candidatos = flota.stream()
                .filter(d -> validador.esApto(d, mision))
                .toList();
        Optional<DroneAcuatico> elegido = estrategia.seleccionar(candidatos, mision);
        elegido.ifPresent(d -> d.setEstado(EstadoDrone.EN_MISION));
        return elegido;
    }
}
