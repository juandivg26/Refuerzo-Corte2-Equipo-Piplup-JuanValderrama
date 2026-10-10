package com.eci.aquaport.ejercicio1;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static com.eci.aquaport.ejercicio1.ZonaHidrica.EMBALSE_INVESTIGACION;
import static com.eci.aquaport.ejercicio1.ZonaHidrica.LAB_HIDRICO_CENTRAL;
import static com.eci.aquaport.ejercicio1.ZonaHidrica.LAGUNA_RESERVA;
import static com.eci.aquaport.ejercicio1.ZonaHidrica.RED_CANALES;

public class Main {

    // Tipo de drone que opera en cada zona, en el orden de ZonaHidrica.values()
    private static final TipoDrone[] TIPO_POR_ZONA =
            {TipoDrone.BUCEADOR, TipoDrone.SUPERFICIAL, TipoDrone.SEMISUMERGIDO, TipoDrone.SUPERFICIAL};

    public static void main(String[] args) {
        // Formato de una linea: solo el mensaje, sin fecha ni nivel
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
        Logger log = Logger.getLogger(Main.class.getName());
        List<DroneAcuatico> flota = IntStream.rangeClosed(1, 60).mapToObj(Main::drone).toList();
        List<Mision> misiones = misiones();
        AnalizadorRedHidrica analizador = new AnalizadorRedHidrica();
        log.info(() -> "1) Eficiencia por zona destino: " + analizador.eficienciaPorZona(misiones));
        log.info(() -> "   Carga actual por zona (waypoints EN_TRANSITO): " + analizador.cargaPorZona(misiones));
        log.info(() -> "2) Drone con mejor historial: " + analizador.droneConMejorHistorial(flota).orElseThrow());
        log.info("3) Ruta multi-etapa EMBALSE -> CANALES -> LAB:");
        analizador.planificarRuta(List.of(EMBALSE_INVESTIGACION, RED_CANALES, LAB_HIDRICO_CENTRAL), flota)
                .forEach(t -> log.info(() -> "   " + t.origen() + " -> " + t.destino() + " con " + t.drone().id()));
        log.info(() -> "4) Bateria promedio por zona (Collector propio): "
                + flota.stream().collect(ColectoresFlota.promedioBateriaPorZona()));
    }

    private static List<Mision> misiones() {
        return List.of(
                new Mision("M-501", EstadoMision.ENTREGADA, List.of(EMBALSE_INVESTIGACION, LAB_HIDRICO_CENTRAL)),
                new Mision("M-502", EstadoMision.FALLIDA, List.of(LAGUNA_RESERVA, RED_CANALES, LAB_HIDRICO_CENTRAL)),
                new Mision("M-503", EstadoMision.ENTREGADA, List.of(RED_CANALES, LAB_HIDRICO_CENTRAL)),
                new Mision("M-504", EstadoMision.EN_TRANSITO, List.of(EMBALSE_INVESTIGACION, RED_CANALES, LAB_HIDRICO_CENTRAL)),
                new Mision("M-505", EstadoMision.EN_TRANSITO, List.of(LAGUNA_RESERVA, LAB_HIDRICO_CENTRAL)),
                new Mision("M-506", EstadoMision.ENTREGADA, List.of(LAGUNA_RESERVA, RED_CANALES)));
    }

    /** 15 drones por zona; bateria y entregas variadas pero deterministas. */
    private static DroneAcuatico drone(int i) {
        int zona = (i - 1) / 15;
        return new DroneAcuatico(String.format("AR-%02d", i), TIPO_POR_ZONA[zona], 20 + (i * 37) % 80,
                i % 7 != 0, ZonaHidrica.values()[zona], (i * 13) % 50);
    }
}
