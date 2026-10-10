package com.eci.aquaport.ejercicio12.aplicacion;

import com.eci.aquaport.ejercicio12.dominio.Drone;
import com.eci.aquaport.ejercicio12.dominio.Mision;
import com.eci.aquaport.ejercicio12.dominio.ServicioCondicionesHidricas;
import com.eci.aquaport.ejercicio12.dominio.ValidadorBateria;
import com.eci.aquaport.ejercicio12.dominio.ValidadorCapacidadCarga;
import com.eci.aquaport.ejercicio12.dominio.ValidadorCondicionesHidricas;
import com.eci.aquaport.ejercicio12.dominio.ValidadorMision;
import com.eci.aquaport.ejercicio12.dominio.ValidadorZonaActiva;
import com.eci.aquaport.ejercicio12.dominio.ZonaHidrica;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Caso de uso: asignar a la mision el drone de mayor bateria que pase toda la cadena de validacion. */
public class ServicioAsignacion {

    private final ValidadorMision cadena;

    public ServicioAsignacion(ValidadorMision cadena) {
        this.cadena = cadena;
    }

    /** Orden de la cadena Enterprise: bateria -> capacidad -> zona activa -> condiciones hidricas. */
    public static ValidadorMision cadenaEstandar(Set<ZonaHidrica> zonasActivas, ServicioCondicionesHidricas condiciones) {
        ValidadorMision inicio = new ValidadorBateria();
        inicio.enlazar(new ValidadorCapacidadCarga())
                .enlazar(new ValidadorZonaActiva(zonasActivas))
                .enlazar(new ValidadorCondicionesHidricas(condiciones));
        return inicio;
    }

    public Optional<Drone> asignar(Mision mision, List<Drone> flota) {
        return flota.stream()
                .sorted(Comparator.comparingInt(Drone::bateria).reversed())
                .filter(d -> cadena.validar(d, mision).isEmpty())
                .findFirst();
    }
}
