package com.eci.aquaport.ejercicio12;

import java.util.List;
import java.util.Set;

public class ValidadorMision {

    private static final int BATERIA_MINIMA = 35;
    private static final Set<String> ZONAS_VALIDAS = Set.of(
            "Embalse Norte", "Canal Central", "Laguna Sur", "Punto Ribereño Este", "Laboratorio Hídrico");

    public void validar(Mision nueva, List<Mision> registradas) {
        if (nueva == null) {
            throw new IllegalArgumentException("La mision no puede ser nula.");
        }
        validarDroneDisponible(nueva.getDrone());
        validarPuntoLlegada(nueva.getPuntoLlegada());
        validarZona(nueva.getPuntoLlegada());
        if (!tieneBateriaSuficiente(nueva.getDrone())) {
            throw new IllegalStateException("El drone " + nueva.getDrone().id()
                    + " no tiene bateria suficiente (minimo " + BATERIA_MINIMA + "%).");
        }

        String idDrone = nueva.getDrone().id();
        boolean droneOcupado = registradas.stream()
                .filter(this::estaActiva)
                .anyMatch(m -> m.getDrone().id().equals(idDrone));
        if (droneOcupado) {
            throw new IllegalStateException("El drone " + idDrone
                    + " ya tiene una mision activa. Un drone no puede tener mas de 1 mision activa simultanea.");
        }
    }

    public boolean tieneBateriaSuficiente(DroneAcuatico drone) {
        return drone.bateria() >= BATERIA_MINIMA;
    }

    public void validarPuntoLlegada(String puntoLlegada) {
        if (puntoLlegada == null || puntoLlegada.isBlank()) {
            throw new IllegalArgumentException("El punto de llegada no puede ser nulo ni vacio.");
        }
    }

    public void validarDroneDisponible(DroneAcuatico drone) {
        if (drone == null || !drone.disponible()) {
            throw new IllegalArgumentException("El drone debe existir y estar disponible.");
        }
    }

    public void validarZona(String zona) {
        if (zona == null || !ZONAS_VALIDAS.contains(zona)) {
            throw new IllegalArgumentException("La zona \"" + zona + "\" no es una de las zonas fijas del campus.");
        }
    }

    private boolean estaActiva(Mision mision) {
        return mision.getEstado() == EstadoMision.PENDIENTE
                || mision.getEstado() == EstadoMision.EN_TRANSITO;
    }
}
