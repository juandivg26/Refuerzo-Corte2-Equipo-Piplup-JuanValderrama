package com.eci.aquaport.ejercicio3;

import com.eci.aquaport.ejercicio3.aplicacion.ServicioAsignacion;
import com.eci.aquaport.ejercicio3.dominio.Drone;
import com.eci.aquaport.ejercicio3.dominio.DroneAcuatico;
import com.eci.aquaport.ejercicio3.dominio.Mision;
import com.eci.aquaport.ejercicio3.dominio.RegistroTelemetria;
import com.eci.aquaport.ejercicio3.dominio.TipoDrone;
import com.eci.aquaport.ejercicio3.dominio.ValidadorMision;
import com.eci.aquaport.ejercicio3.infraestructura.AdaptadorAPIHidrica;
import com.eci.aquaport.ejercicio3.infraestructura.ClienteApiHidricaSimulado;
import com.eci.aquaport.ejercicio3.infraestructura.DroneConMonitoreo;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.eci.aquaport.ejercicio3.dominio.ZonaHidrica.EMBALSE_INVESTIGACION;
import static com.eci.aquaport.ejercicio3.dominio.ZonaHidrica.LAB_HIDRICO_CENTRAL;
import static com.eci.aquaport.ejercicio3.dominio.ZonaHidrica.LAGUNA_RESERVA;
import static com.eci.aquaport.ejercicio3.dominio.ZonaHidrica.RED_CANALES;

/** Raiz de composicion: unico lugar donde se crean las implementaciones de infraestructura. */
public class Main {

    private static final Logger LOG = configurarLogger();
    private static final RegistroTelemetria TELEMETRIA =
            (id, evento) -> LOG.info(() -> "   [TELEMETRIA " + id + "] " + evento);

    public static void main(String[] args) {
        ValidadorMision cadena = ServicioAsignacion.cadenaEstandar(
                Set.of(EMBALSE_INVESTIGACION, LAGUNA_RESERVA, LAB_HIDRICO_CENTRAL),
                new AdaptadorAPIHidrica(new ClienteApiHidricaSimulado()));
        ServicioAsignacion servicio = new ServicioAsignacion(cadena);
        List<Drone> flota = List.of(
                new DroneAcuatico("AR-01", TipoDrone.BUCEADOR, 90, EMBALSE_INVESTIGACION),
                new DroneAcuatico("AR-16", TipoDrone.SUPERFICIAL, 75, RED_CANALES),
                new DroneAcuatico("AR-31", TipoDrone.SEMISUMERGIDO, 30, LAGUNA_RESERVA));
        List.of(new Mision("M-601", 250, LAB_HIDRICO_CENTRAL), new Mision("M-602", 400, LAGUNA_RESERVA),
                        new Mision("M-603", 200, RED_CANALES), new Mision("M-604", 2000, EMBALSE_INVESTIGACION))
                .forEach(m -> procesar(m, servicio, cadena, flota));
    }

    private static void procesar(Mision m, ServicioAsignacion servicio, ValidadorMision cadena, List<Drone> flota) {
        servicio.asignar(m, flota).ifPresentOrElse(
                d -> {
                    LOG.info(() -> m.id() + " asignada a " + d.id());
                    new DroneConMonitoreo(d, TELEMETRIA).navegar(m.destino());
                },
                () -> {
                    LOG.info(() -> m.id() + " rechazada; motivo por drone:");
                    flota.forEach(d -> LOG.info(() -> "   " + d.id() + ": " + cadena.validar(d, m).orElse("apto")));
                });
    }

    private static Logger configurarLogger() {
        // Formato de una linea: solo el mensaje, sin fecha ni nivel
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
        return Logger.getLogger(Main.class.getName());
    }
}
