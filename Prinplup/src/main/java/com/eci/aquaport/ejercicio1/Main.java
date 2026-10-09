package com.eci.aquaport.ejercicio1;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<DroneAcuatico> flota = List.of(
                new DroneAcuatico("AR-01", TipoDrone.SUPERFICIAL, 92, true, "Canal Central"),
                new DroneAcuatico("AR-02", TipoDrone.SUPERFICIAL, 45, true, "Canal Central"),
                new DroneAcuatico("AR-03", TipoDrone.SUPERFICIAL, 18, false, "Laguna Sur"),
                new DroneAcuatico("AR-04", TipoDrone.SUPERFICIAL, 73, true, "Punto Ribereño Este"),
                new DroneAcuatico("AR-05", TipoDrone.SUPERFICIAL, 60, true, "Laboratorio Hídrico"),
                new DroneAcuatico("AR-06", TipoDrone.SEMISUMERGIDO, 88, true, "Laguna Sur"),
                new DroneAcuatico("AR-07", TipoDrone.SEMISUMERGIDO, 34, true, "Laguna Sur"),
                new DroneAcuatico("AR-08", TipoDrone.SEMISUMERGIDO, 67, false, "Canal Central"),
                new DroneAcuatico("AR-09", TipoDrone.SEMISUMERGIDO, 51, true, "Embalse Norte"),
                new DroneAcuatico("AR-10", TipoDrone.SEMISUMERGIDO, 95, true, "Laguna Sur"),
                new DroneAcuatico("AR-11", TipoDrone.BUCEADOR, 79, true, "Embalse Norte"),
                new DroneAcuatico("AR-12", TipoDrone.BUCEADOR, 22, false, "Embalse Norte"),
                new DroneAcuatico("AR-13", TipoDrone.BUCEADOR, 64, true, "Embalse Norte"),
                new DroneAcuatico("AR-14", TipoDrone.BUCEADOR, 40, true, "Laguna Sur"),
                new DroneAcuatico("AR-15", TipoDrone.BUCEADOR, 85, false, "Embalse Norte")
        );

        List<Mision> misiones = List.of(
                new Mision("M-201", "Embalse Norte", Prioridad.CRITICA),
                new Mision("M-202", "Laguna Sur", Prioridad.NORMAL),
                new Mision("M-203", "Canal Central", Prioridad.ALTA),
                new Mision("M-204", "Laboratorio Hídrico", Prioridad.CRITICA),
                new Mision("M-205", "Punto Ribereño Este", Prioridad.BAJA)
        );

        ConsultorFlota consultor = new ConsultorFlota();

        System.out.println("1) Disponibles por tipo: " + consultor.agruparDisponiblesPorTipo(flota));
        System.out.println("2) Drone optimo para Embalse Norte (BUCEADOR): "
                + consultor.droneOptimoParaZona(flota, "Embalse Norte", TipoDrone.BUCEADOR));
        System.out.println("3) Promedio de bateria por tipo: " + consultor.promedioBateriaPorTipo(flota));
        System.out.println("4) Misiones criticas (true) vs demas (false): " + consultor.separarCriticas(misiones));
        System.out.println("5) Zonas cubiertas por la flota activa: " + consultor.zonasCubiertasPorFlotaActiva(flota));
    }
}
