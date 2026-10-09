package com.eci.aquaport.ejercicio3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FabricaDronesTest {

    private final FabricaDrones fabrica = new FabricaDrones();

    @ParameterizedTest
    @CsvSource({"SUPERFICIAL, 500", "SEMISUMERGIDO, 1500", "BUCEADOR, 300"})
    @DisplayName("La fabrica crea el tipo pedido con su capacidad de carga")
    void creaTipoConCapacidad(TipoDrone tipo, int capacidad) {
        DroneAcuatico drone = fabrica.crear(tipo, "AR-01", 80, "Laguna Sur");

        assertEquals(tipo, drone.getTipo());
        assertEquals(capacidad, drone.getCapacidadMaximaGramos());
    }

    @ParameterizedTest
    @CsvSource({"SUPERFICIAL", "SEMISUMERGIDO", "BUCEADOR"})
    @DisplayName("Todo drone nuevo nace DISPONIBLE")
    void droneNaceDisponible(TipoDrone tipo) {
        assertEquals(EstadoDrone.DISPONIBLE, fabrica.crear(tipo, "AR-02", 50, "Canal Central").getEstado());
    }

    @ParameterizedTest
    @CsvSource({"300, true", "301, false"})
    @DisplayName("El DroneBuceador no carga mas de 300 g")
    void buceadorLimiteDeCarga(int peso, boolean esperado) {
        DroneAcuatico buceador = fabrica.crear(TipoDrone.BUCEADOR, "AR-11", 90, "Embalse Norte");

        assertEquals(esperado, buceador.puedeCargar(peso));
    }
}
