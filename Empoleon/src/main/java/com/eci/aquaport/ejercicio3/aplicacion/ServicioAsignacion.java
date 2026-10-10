package com.eci.aquaport.ejercicio3.aplicacion;

import com.eci.aquaport.ejercicio3.dominio.Drone;
import com.eci.aquaport.ejercicio3.dominio.Mision;
import com.eci.aquaport.ejercicio3.dominio.ServicioCondicionesHidricas;
import com.eci.aquaport.ejercicio3.dominio.ValidadorBateria;
import com.eci.aquaport.ejercicio3.dominio.ValidadorCapacidadCarga;
import com.eci.aquaport.ejercicio3.dominio.ValidadorCondicionesHidricas;
import com.eci.aquaport.ejercicio3.dominio.ValidadorMision;
import com.eci.aquaport.ejercicio3.dominio.ValidadorZonaActiva;
import com.eci.aquaport.ejercicio3.dominio.ZonaHidrica;

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
