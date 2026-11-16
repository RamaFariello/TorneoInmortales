package inmortales.presentacion;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inmortales.acontecimientos.Acontecimiento;

class HeraldoTest {
    private String texto(ByteArrayOutputStream bytes) {
        return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
    }

    private HeraldoImperial heraldo(ByteArrayOutputStream bytes, boolean detalle) {
        return new HeraldoImperial(new PrintStream(bytes, true, StandardCharsets.UTF_8), detalle);
    }

    @Test
    @DisplayName("El Heraldo presenta los acontecimientos y puede omitir el detalle de las acciones")
    void presentaYFiltraDetalle() {
        ByteArrayOutputStream conDetalle = new ByteArrayOutputStream();
        ByteArrayOutputStream sinDetalle = new ByteArrayOutputStream();
        Acontecimiento accion = Acontecimiento.accion(1, "Aurelio", "Ataque básico", false, 3, "Kael",
                "causa 12 de daño", 88).ubicarEn(1, 1);
        Acontecimiento proclamacion = Acontecimiento.proclamacion(2, 2, "Kael", "Darian");

        for (HeraldoImperial h : Arrays.asList(heraldo(conDetalle, true), heraldo(sinDetalle, false))) {
            h.actualizar(accion);
            h.actualizar(proclamacion);
        }

        assertTrue(texto(conDetalle).contains("Aurelio"));
        assertTrue(texto(conDetalle).contains("Kael y Darian"));
        assertFalse(texto(sinDetalle).contains("Aurelio"));
        assertTrue(texto(sinDetalle).contains("Kael y Darian"));
    }

    @Test
    @DisplayName("El Heraldo presenta la tabla de posiciones que arma el Cronista")
    void presentaTabla() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        heraldo(bytes, true).mostrarTablaPosiciones(Arrays.asList(
                new RegistroPosicion(3, "Pareja 3", 2, 100), new RegistroPosicion(1, "Pareja 1", 1, 0)));
        String salida = texto(bytes);
        assertTrue(salida.indexOf("Pareja 3") < salida.indexOf("Pareja 1"));
    }

    @Test
    @DisplayName("El Heraldo anuncia textos libres (lo que muestra el main)")
    void anuncia() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        heraldo(bytes, true).anunciar("Hola");
        assertTrue(texto(bytes).contains("Hola"));
    }
}
