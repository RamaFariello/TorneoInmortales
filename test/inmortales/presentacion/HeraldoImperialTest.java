package inmortales.presentacion;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inmortales.acontecimientos.AccionRealizada;
import inmortales.acontecimientos.Eliminacion;
import inmortales.acontecimientos.InicioCombate;
import inmortales.tecnicas.TipoTecnica;

/**
 * Esta es la unica prueba que lee la pantalla, porque prueba al Heraldo mismo.
 * Las pruebas de combate y de torneo no leen nunca la salida: usan el estado de los objetos
 * o el historial del Cronista.
 */
class HeraldoImperialTest {
    private final PrintStream salidaOriginal = System.out;
    private ByteArrayOutputStream capturada;

    @BeforeEach
    void capturarPantalla() {
        capturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturada, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restaurarPantalla() {
        System.setOut(salidaOriginal);
    }

    private String pantalla() {
        return capturada.toString(StandardCharsets.UTF_8);
    }

    private AccionRealizada accion() {
        return new AccionRealizada(1, 1, 1, "Zeta", "Ataque", true, TipoTecnica.OFENSIVA, 2, "Omega", 10, 90, 40);
    }

    @Test
    void presentaLosAcontecimientosPorPantalla() {
        HeraldoImperial heraldo = new HeraldoImperial();

        heraldo.notificar(new InicioCombate(1, 1, "Pareja 1", 2, "Pareja 2"));
        heraldo.notificar(new Eliminacion(1, 3, 2, "Kael", 2));

        assertTrue(pantalla().contains("Pareja 1 vs Pareja 2"));
        assertTrue(pantalla().contains("Kael"));
    }

    @Test
    void elHeraldoDetalladoMuestraLasAcciones() {
        new HeraldoImperial(true).notificar(accion());

        assertTrue(pantalla().contains("Zeta"));
    }

    @Test
    void elHeraldoResumidoOmiteLasAccionesPeroNoLoPrincipal() {
        HeraldoImperial heraldo = new HeraldoImperial(false);

        heraldo.notificar(accion());
        heraldo.notificar(new Eliminacion(1, 3, 2, "Kael", 2));

        assertFalse(pantalla().contains("Zeta"));
        assertTrue(pantalla().contains("Kael"));
    }

    @Test
    void anunciaUnMensajeLibre() {
        new HeraldoImperial().anunciar("Mensaje del main");

        assertTrue(pantalla().contains("Mensaje del main"));
    }

    @Test
    void presentaLaTablaEnElOrdenRecibido() {
        List<EntradaTabla> tabla = List.of(
                new EntradaTabla(2, "Campeones", 2, 80),
                new EntradaTabla(1, "Subcampeones", 2, 0),
                new EntradaTabla(3, "Eliminados", 1, 30));

        new HeraldoImperial().presentarTabla(tabla);

        String texto = pantalla();
        assertTrue(texto.contains("TABLA DE POSICIONES"));
        int primero = texto.indexOf("Campeones");
        int segundo = texto.indexOf("Subcampeones");
        int tercero = texto.indexOf("Eliminados");
        assertTrue(primero >= 0 && primero < segundo && segundo < tercero);
    }
}
