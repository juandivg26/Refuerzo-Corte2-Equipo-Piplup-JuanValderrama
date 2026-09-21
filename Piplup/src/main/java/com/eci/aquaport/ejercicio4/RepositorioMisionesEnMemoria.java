package com.eci.aquaport.ejercicio4;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioMisionesEnMemoria implements RepositorioMisiones {

    private final Map<String, Mision> misiones = new LinkedHashMap<>();

    @Override
    public void guardar(Mision mision) {
        misiones.put(mision.getId(), mision);
    }

    @Override
    public Optional<Mision> buscarPorId(String id) {
        return Optional.ofNullable(misiones.get(id));
    }

    @Override
    public List<Mision> listarTodas() {
        return List.copyOf(misiones.values());
    }
}