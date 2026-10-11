package com.eci.aquaport.ejercicio12.dominio;

/** Puerto: el dominio pide condiciones del agua sin saber que detras hay una API externa. */
public interface ServicioCondicionesHidricas {

    CondicionesHidricas consultar(ZonaHidrica zona);
}
