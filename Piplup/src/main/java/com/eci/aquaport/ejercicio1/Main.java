package com.eci.aquaport.ejercicio1;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<DroneAcuatico> flota = List.of(
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte"),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45, true, "Canal Central"),
                new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur"),
                new DroneAcuatico("AR-04", "Aqua-Ranger 100", 73, true, "Punto Ribereño Este")
        );

        ConsultorFlota consultor = new ConsultorFlota();

        System.out.println("Disponibles con batería >= 35% ordenados: "
                + consultor.dronesDisponiblesOrdenadosPorBateria(flota));

        System.out.println("IDs disponibles: "
                + consultor.idsDronesDisponibles(flota));

        System.out.println("¿Existe disponible con batería suficiente?: "
                + consultor.existeDroneDisponibleConBateriaSuficiente(flota));

        System.out.println("Cantidad de disponibles: "
                + consultor.contarDronesDisponibles(flota));

        System.out.println("Drone con mayor batería: "
                + consultor.droneConMayorBateria(flota));
    }
}
