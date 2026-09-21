package com.eci.aquaport.ejercicio4;

public class Main {

    public static void main(String[] args) {
        RepositorioMisiones repositorio = new RepositorioMisionesEnMemoria();
        RegistradorMisiones registrador = new RegistradorMisiones(repositorio);
        ValidadorMision validador = new ValidadorMision();
        NotificadorOperador notificador = new NotificadorOperador();

        DroneAcuatico ar01 = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
        DroneAcuatico ar02 = new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45, true, "Canal Central");

        Mision m1 = new Mision.Builder()
                .id("M-001").drone(ar01)
                .puntoPartida("Embalse Norte").puntoLlegada("Laboratorio Hidrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA).estado(EstadoMision.PENDIENTE)
                .build();

        Mision m2 = new Mision.Builder()
                .id("M-002").drone(ar02)
                .puntoPartida("Canal Central").puntoLlegada("Laguna Sur")
                .tipoCarga(TipoCarga.SENSOR).estado(EstadoMision.PENDIENTE)
                .build();

        Mision m3 = new Mision.Builder()
                .id("M-003").drone(ar01)
                .puntoPartida("Laguna Sur").puntoLlegada("Laboratorio Hidrico")
                .tipoCarga(TipoCarga.PAQUETE_LIGERO).estado(EstadoMision.PENDIENTE)
                .build();

        for (Mision m : new Mision[] {m1, m2, m3}) {
            try {
                validador.validar(m, registrador.listarMisiones());
                registrador.registrar(m);
                notificador.mostrar("Mision registrada: " + m.getId());
            } catch (IllegalStateException e) {
                notificador.mostrar("Mision rechazada (" + m.getId() + "): " + e.getMessage());
            }
        }

        notificador.mostrar("Total de misiones: " + registrador.listarMisiones().size());

        registrador.buscarPorId("M-002")
                .ifPresentOrElse(
                        m -> notificador.mostrar("Encontrada: " + m),
                        () -> notificador.mostrar("No existe la mision M-002."));

        registrador.buscarPorId("M-999")
                .ifPresentOrElse(
                        m -> notificador.mostrar("Encontrada: " + m),
                        () -> notificador.mostrar("No existe la mision M-999."));
    }
}