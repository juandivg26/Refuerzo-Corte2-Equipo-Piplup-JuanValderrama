package com.eci.aquaport.ejercicio12.dominio;

import java.util.Optional;
import java.util.Set;

public class ValidadorZonaActiva extends ValidadorMision {

    private final Set<ZonaHidrica> zonasActivas;

    public ValidadorZonaActiva(Set<ZonaHidrica> zonasActivas) {
        this.zonasActivas = Set.copyOf(zonasActivas);
    }

    @Override
    protected Optional<String> revisar(Drone drone, Mision mision) {
        if (!zonasActivas.contains(mision.destino())) {
            return Optional.of("La zona destino " + mision.destino() + " esta inactiva");
        }
        return Optional.empty();
    }
}
