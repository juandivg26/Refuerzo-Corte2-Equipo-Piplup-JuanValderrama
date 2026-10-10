package com.eci.aquaport.ejercicio3;

import com.eci.aquaport.ejercicio3.dominio.CondicionesHidricas;
import com.eci.aquaport.ejercicio3.dominio.NivelAgua;
import com.eci.aquaport.ejercicio3.dominio.Turbidez;
import com.eci.aquaport.ejercicio3.infraestructura.AdaptadorAPIHidrica;
import com.eci.aquaport.ejercicio3.infraestructura.ClienteApiHidrica;
import com.eci.aquaport.ejercicio3.infraestructura.ClienteApiHidricaSimulado;
import com.eci.aquaport.ejercicio3.infraestructura.RespuestaApiHidrica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.eci.aquaport.ejercicio3.dominio.ZonaHidrica.LAGUNA_RESERVA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdaptadorAPIHidricaTest {

    @Mock ClienteApiHidrica clienteMock;

    private AdaptadorAPIHidrica adaptador;

    @BeforeEach
    void setUp() {
        adaptador = new AdaptadorAPIHidrica(clienteMock);
    }

    @Test
    @DisplayName("Convierte waterLevel/turbidity a NivelAgua/Turbidez y pide la zona con su codigo")
    void convierteCampos() {
        when(clienteMock.fetchConditions("LAGUNA_RESERVA")).thenReturn(new RespuestaApiHidrica("LAGUNA_RESERVA", 3.4, 64.0));

        CondicionesHidricas condiciones = adaptador.consultar(LAGUNA_RESERVA);

        assertEquals(new CondicionesHidricas(new NivelAgua(3.4), new Turbidez(64.0)), condiciones);
        verify(clienteMock).fetchConditions("LAGUNA_RESERVA");
    }

    @ParameterizedTest
    @CsvSource({"0.0, 0.0", "0.0, 1000.0", "11000.0, 0.0"})
    @DisplayName("Valores limite validos (cero y extremos) se convierten sin perder precision")
    void valoresLimiteValidos(double nivel, double turbidez) {
        when(clienteMock.fetchConditions("LAGUNA_RESERVA")).thenReturn(new RespuestaApiHidrica("LAGUNA_RESERVA", nivel, turbidez));

        CondicionesHidricas condiciones = adaptador.consultar(LAGUNA_RESERVA);

        assertEquals(nivel, condiciones.nivelAgua().metros());
        assertEquals(turbidez, condiciones.turbidez().ntu());
    }

    @ParameterizedTest
    @CsvSource({"-0.01, 5.0", "2.0, -0.01", "NaN, 5.0", "2.0, NaN"})
    @DisplayName("Valores negativos o NaN de la API se rechazan en la frontera")
    void valoresLimiteInvalidos(double nivel, double turbidez) {
        when(clienteMock.fetchConditions("LAGUNA_RESERVA")).thenReturn(new RespuestaApiHidrica("LAGUNA_RESERVA", nivel, turbidez));

        assertThrows(IllegalArgumentException.class, () -> adaptador.consultar(LAGUNA_RESERVA));
    }

    @Test
    @DisplayName("Si la API no responde se lanza un error claro")
    void apiSinRespuesta() {
        when(clienteMock.fetchConditions("LAGUNA_RESERVA")).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> adaptador.consultar(LAGUNA_RESERVA));
    }

    @Test
    @DisplayName("El cliente simulado responde para las 4 zonas")
    void clienteSimuladoCubreLasZonas() {
        AdaptadorAPIHidrica real = new AdaptadorAPIHidrica(new ClienteApiHidricaSimulado());

        assertEquals(64.0, real.consultar(LAGUNA_RESERVA).turbidez().ntu());
    }
}
