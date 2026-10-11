package com.eci.aquaport.ejercicio12.aplicacion;

import com.eci.aquaport.ejercicio12.dominio.Drone;
import com.eci.aquaport.ejercicio12.dominio.Mision;
import com.eci.aquaport.ejercicio12.dominio.ServicioCondicionesHidricas;
import com.eci.aquaport.ejercicio12.dominio.ValidadorBateria;
import com.eci.aquaport.ejercicio12.dominio.ValidadorCapacidadCarga;
import com.eci.aquaport.ejercicio12.dominio.ValidadorCondicionesHidricas;
import com.eci.aquaport.ejercicio12.dominio.ValidadorEstadoOperativo;
import com.eci.aquaport.ejercicio12.dominio.ValidadorMision;
import com.eci.aquaport.ejercicio12.dominio.ValidadorZonaActiva;
import com.eci.aquaport.ejercicio12.dominio.ZonaHidrica;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Caso de uso: asignar a la mision el drone de mayor bateria que pase toda la cadena de validacion. */
public class ServicioAsignacion {

    private final ValidadorMision cadena;

    public ServicioAsignacion(ValidadorMision cadena) {
        this.cadena = cadena;
    }

    /** Orden de la cadena Enterprise: estado -> bateria -> capacidad -> zona activa -> condiciones hidricas. */
    public static ValidadorMision cadenaEstandar(Set<ZonaHidrica> zonasActivas, ServicioCondicionesHidricas condiciones) {
        ValidadorMision inicio = new ValidadorEstadoOperativo();
        inicio.enlazar(new ValidadorBateria())
                .enlazar(new ValidadorCapacidadCarga())
                .enlazar(new ValidadorZonaActiva(zonasActivas))
                .enlazar(new ValidadorCondicionesHidricas(condiciones));
        return inicio;
    }

    public boolean esApto(Drone drone, Mision mision) {
        return cadena.validar(drone, mision).isEmpty();
    }

    public Optional<Drone> asignar(Mision mision, List<Drone> flota) {
        return flota.stream()
                .sorted(Comparator.comparingInt(Drone::bateria).reversed())
                .filter(d -> esApto(d, mision))
                .findFirst();
    }

    /** Primero los drones que ya estan en la zona de origen del tramo; si ninguno sirve, el mejor del resto. */
    public Optional<Drone> asignarDesde(ZonaHidrica origen, Mision mision, List<Drone> candidatos) {
        Map<Boolean, List<Drone>> porZona = candidatos.stream()
                .collect(Collectors.partitioningBy(d -> d.zona() == origen));
        return asignar(mision, porZona.get(true)).or(() -> asignar(mision, porZona.get(false)));
    }
}
