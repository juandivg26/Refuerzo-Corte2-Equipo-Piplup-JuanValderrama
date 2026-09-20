package com.eci.aquaport.ejercicio3;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE BUILDER DE MISION (RETO 03) ===\n");

        DroneAcuatico droneDisponible = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
        DroneAcuatico droneOcupado = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur");

        // 1. Caso Válido
        System.out.println("1. Prueba con Caso Valido:");
        try {
            Mision misionValida = new Mision.Builder()
                    .id("M-101")
                    .drone(droneDisponible)
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hidrico")
                    .tipoCarga(TipoCarga.MUESTRA_AGUA)
                    .build();
            System.out.println("Mision creada exitosamente: " + misionValida + "\n");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage() + "\n");
        }

        // 2. Error: Drone no disponible
        System.out.println("2. Prueba de Error: Drone no disponible");
        try {
            new Mision.Builder()
                    .id("M-102")
                    .drone(droneOcupado)
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Laboratorio Hidrico")
                    .tipoCarga(TipoCarga.SENSOR)
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Excepcion capturada esperada: " + e.getMessage() + "\n");
        }

        // 3. Error: Punto de llegada vacío
        System.out.println("3. Prueba de Error: Punto de llegada vacio");
        try {
            new Mision.Builder()
                    .id("M-103")
                    .drone(droneDisponible)
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("   ")
                    .tipoCarga(TipoCarga.PAQUETE_LIGERO)
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Excepcion capturada esperada: " + e.getMessage() + "\n");
        }

        // 4. Error: ID nulo
        System.out.println("4. Prueba de Error: ID nulo");
        try {
            new Mision.Builder()
                    .id(null)
                    .drone(droneDisponible)
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("Canal Central")
                    .tipoCarga(TipoCarga.MUESTRA_AGUA)
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Excepcion capturada esperada: " + e.getMessage() + "\n");
        }
    }
}