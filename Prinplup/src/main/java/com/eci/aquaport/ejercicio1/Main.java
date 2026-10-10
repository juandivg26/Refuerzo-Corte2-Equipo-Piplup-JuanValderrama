package com.eci.aquaport.ejercicio1;

import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static final String EMBALSE_NORTE = "Embalse Norte";
    private static final String CANAL_CENTRAL = "Canal Central";
    private static final String LAGUNA_SUR = "Laguna Sur";
    private static final String RIBERA_ESTE = "Punto Ribereño Este";
    private static final String LAB_HIDRICO = "Laboratorio Hídrico";

    public static void main(String[] args) {
        List<DroneAcuatico> flota = List.of(
                new DroneAcuatico("AR-01", TipoDrone.SUPERFICIAL, 92, true, CANAL_CENTRAL),
                new DroneAcuatico("AR-02", TipoDrone.SUPERFICIAL, 45, true, CANAL_CENTRAL),
                new DroneAcuatico("AR-03", TipoDrone.SUPERFICIAL, 18, false, LAGUNA_SUR),
                new DroneAcuatico("AR-04", TipoDrone.SUPERFICIAL, 73, true, RIBERA_ESTE),
                new DroneAcuatico("AR-05", TipoDrone.SUPERFICIAL, 60, true, LAB_HIDRICO),
                new DroneAcuatico("AR-06", TipoDrone.SEMISUMERGIDO, 88, true, LAGUNA_SUR),
                new DroneAcuatico("AR-07", TipoDrone.SEMISUMERGIDO, 34, true, LAGUNA_SUR),
                new DroneAcuatico("AR-08", TipoDrone.SEMISUMERGIDO, 67, false, CANAL_CENTRAL),
                new DroneAcuatico("AR-09", TipoDrone.SEMISUMERGIDO, 51, true, EMBALSE_NORTE),
                new DroneAcuatico("AR-10", TipoDrone.SEMISUMERGIDO, 95, true, LAGUNA_SUR),
                new DroneAcuatico("AR-11", TipoDrone.BUCEADOR, 79, true, EMBALSE_NORTE),
                new DroneAcuatico("AR-12", TipoDrone.BUCEADOR, 22, false, EMBALSE_NORTE),
                new DroneAcuatico("AR-13", TipoDrone.BUCEADOR, 64, true, EMBALSE_NORTE),
                new DroneAcuatico("AR-14", TipoDrone.BUCEADOR, 40, true, LAGUNA_SUR),
                new DroneAcuatico("AR-15", TipoDrone.BUCEADOR, 85, false, EMBALSE_NORTE)
        );

        List<Mision> misiones = List.of(
                new Mision("M-201", EMBALSE_NORTE, Prioridad.CRITICA),
                new Mision("M-202", LAGUNA_SUR, Prioridad.NORMAL),
                new Mision("M-203", CANAL_CENTRAL, Prioridad.ALTA),
                new Mision("M-204", LAB_HIDRICO, Prioridad.CRITICA),
                new Mision("M-205", RIBERA_ESTE, Prioridad.BAJA)
        );

        ConsultorFlota consultor = new ConsultorFlota();

        // Formato de una linea: solo el mensaje, sin fecha ni nivel
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
        Logger log = Logger.getLogger(Main.class.getName());

        log.info(() -> "1) Disponibles por tipo: " + consultor.agruparDisponiblesPorTipo(flota));
        log.info(() -> "2) Drone optimo para Embalse Norte (BUCEADOR): "
                + consultor.droneOptimoParaZona(flota, EMBALSE_NORTE, TipoDrone.BUCEADOR));
        log.info(() -> "3) Promedio de bateria por tipo: " + consultor.promedioBateriaPorTipo(flota));
        log.info(() -> "4) Misiones criticas (true) vs demas (false): " + consultor.separarCriticas(misiones));
        log.info(() -> "5) Zonas cubiertas por la flota activa: " + consultor.zonasCubiertasPorFlotaActiva(flota));
    }
}
