package com.eci.aquaport.ejercicio3;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        FabricaDrones fabrica = new FabricaDrones();
        String[] zonas = {"Embalse Norte", "Canal Central", "Laguna Sur", "Punto Ribereño Este", "Laboratorio Hídrico"};
        TipoDrone[] tipos = TipoDrone.values();

        List<DroneAcuatico> flota = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            String id = String.format("AR-%02d", i);
            flota.add(fabrica.crear(tipos[(i - 1) / 5], id, 30 + (i * 37) % 70, zonas[i % zonas.length]));
        }
        flota.get(6).setEstado(EstadoDrone.FALLO);

        AsignadorAutomatico asignador = new AsignadorAutomatico(new ZonaCercanaStrategy(), new ValidadorMision());
        asignador.registrarObservador(new CentroControlObserver());
        asignador.registrarObservador(new TecnicoMantenimientoObserver());

        System.out.println("=== FLOTA V2 ===");
        flota.forEach(System.out::println);

        List<Mision> misiones = List.of(
                new Mision("M-301", flota.get(6).getZona(), TipoCarga.EQUIPO_MEDICION, 1200, Prioridad.ALTA),
                new Mision("M-302", "Embalse Norte", TipoCarga.MUESTRA_AGUA, 250, Prioridad.NORMAL),
                new Mision("M-303", "Laguna Sur", TipoCarga.EQUIPO_MEDICION, 2000, Prioridad.CRITICA));

        System.out.println("\n=== ASIGNACION AUTOMATICA (ZonaCercanaStrategy) ===");
        for (Mision m : misiones) {
            System.out.println(m.id() + " -> " + asignador.asignar(m, flota)
                    .map(DroneAcuatico::toString)
                    .orElse("sin drone"));
        }
    }
}
