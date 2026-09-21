package com.eci.aquaport.ejercicio4;

import java.util.List;
import java.util.Optional;

public class RegistradorMisiones {

    private final RepositorioMisiones repositorio;

    public RegistradorMisiones(RepositorioMisiones repositorio) {
        this.repositorio = repositorio;
    }

    public void registrar(Mision mision) {
        repositorio.guardar(mision);
    }

    public Optional<Mision> buscarPorId(String id) {
        return repositorio.buscarPorId(id);
    }

    public List<Mision> listarMisiones() {
        return repositorio.listarTodas();
    }
}