package com.eci.aquaport.ejercicio4;

import java.util.List;
import java.util.Optional;

public interface RepositorioMisiones {
    void guardar(Mision mision);
    Optional<Mision> buscarPorId(String id);
    List<Mision> listarTodas();
}