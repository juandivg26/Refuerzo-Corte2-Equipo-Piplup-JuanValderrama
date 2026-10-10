package com.eci.aquaport.ejercicio12.dominio;

import java.util.List;

/** Como termino la ruta y el registro completo de la cadena de custodia de la muestra. */
public record ResultadoRuta(
        EstadoRuta estado,
        List<String> custodia
) {
}
