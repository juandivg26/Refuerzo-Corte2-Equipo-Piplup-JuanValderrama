package com.eci.aquaport.ejercicio12;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AsignadorAutomatico {

    private final EstrategiaSeleccion estrategia;
    private final ValidadorMision validador;
    private final List<ObservadorMision> observadores = new ArrayList<>();

    public AsignadorAutomatico(EstrategiaSeleccion estrategia, ValidadorMision validador) {
        this.estrategia = estrategia;
        this.validador = validador;
    }

    public void registrarObservador(ObservadorMision observador) {
        observadores.add(observador);
    }

    public Optional<DroneAcuatico> asignar(Mision mision, List<DroneAcuatico> flota) {
        List<DroneAcuatico> candidatos = new ArrayList<>(flota.stream()
                .filter(d -> validador.esApto(d, mision))
                .toList());
        Optional<DroneAcuatico> elegido = estrategia.seleccionar(candidatos, mision);
        while (elegido.isPresent() && elegido.get().getEstado() == EstadoDrone.FALLO) {
            DroneAcuatico enFallo = elegido.get();
            observadores.forEach(o -> o.notificarFalloDrone(enFallo, mision));
            candidatos.remove(enFallo);
            elegido = estrategia.seleccionar(candidatos, mision);
        }
        elegido.ifPresentOrElse(d -> d.setEstado(EstadoDrone.EN_MISION), () -> avisarSinDrone(mision));
        return elegido;
    }

    private void avisarSinDrone(Mision mision) {
        if (mision.prioridad() == Prioridad.CRITICA) {
            observadores.forEach(o -> o.notificarFalloAsignacion(mision));
        }
    }
}
