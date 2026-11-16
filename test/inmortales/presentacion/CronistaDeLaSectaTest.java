package inmortales.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.Eliminacion;
import inmortales.acontecimientos.InicioCombate;
import inmortales.acontecimientos.ResultadoCombate;

class CronistaDeLaSectaTest {

    private List<Integer> ids(List<EntradaTabla> tabla) {
        return tabla.stream().map(EntradaTabla::idPareja).toList();
    }

    @Test
    void registraLosAcontecimientosEnElOrdenEnQueLlegan() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        Acontecimiento inicio = new InicioCombate(1, 1, "P1", 2, "P2");
        Acontecimiento eliminacion = new Eliminacion(1, 3, 2, "Kael", 2);

        cronista.notificar(inicio);
        cronista.notificar(eliminacion);

        assertEquals(List.of(inicio, eliminacion), cronista.getHistorial());
    }

    @Test
    void elHistorialNoSePuedeModificarDesdeAfuera() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        assertThrows(UnsupportedOperationException.class,
                () -> cronista.getHistorial().add(new InicioCombate(1, 1, "P1", 2, "P2")));
    }

    @Test
    void consultaElHistorialPorClaseDeAcontecimiento() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        cronista.notificar(new InicioCombate(1, 1, "P1", 2, "P2"));
        cronista.notificar(new Eliminacion(1, 3, 2, "Kael", 2));
        cronista.notificar(new Eliminacion(1, 4, 5, "Lian", 2));

        assertEquals(2, cronista.consultar(Eliminacion.class).size());
        assertEquals(1, cronista.consultar(InicioCombate.class).size());
        assertTrue(cronista.consultar(ResultadoCombate.class).isEmpty());
    }

    @Test
    void sinResultadosLaTablaEstaVacia() {
        assertTrue(new CronistaDeLaSecta().tablaDePosiciones().isEmpty());
    }

    @Test
    void laTablaOrdenaPorUltimaRondaLuegoPorVidaFinalYLuegoPorIdentificador() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        cronista.notificar(new ResultadoCombate(1, 1, "P1", 1, "P1", 50, 2, "P2", 0, 5, false));
        cronista.notificar(new ResultadoCombate(1, 4, "P4", 3, "P3", 30, 4, "P4", 30, 25, true));
        cronista.notificar(new ResultadoCombate(2, 1, "P1", 1, "P1", 10, 4, "P4", 0, 9, false));

        // Ronda 2: P1 (10 de vida) y P4 (0). Ronda 1: P3 (30) y P2 (0).
        assertEquals(List.of(1, 4, 3, 2), ids(cronista.tablaDePosiciones()));
    }

    @Test
    void conIgualRondaYVidaDesempataElMenorIdentificador() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        cronista.notificar(new ResultadoCombate(1, 5, "P5", 7, "P7", 40, 5, "P5", 40, 25, true));

        assertEquals(List.of(5, 7), ids(cronista.tablaDePosiciones()));
    }

    @Test
    void cadaParejaApareceUnaSolaVezConSuUltimaRondaJugada() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        cronista.notificar(new ResultadoCombate(1, 1, "P1", 1, "P1", 60, 2, "P2", 0, 5, false));
        cronista.notificar(new ResultadoCombate(2, 1, "P1", 1, "P1", 25, 3, "P3", 0, 8, false));

        List<EntradaTabla> tabla = cronista.tablaDePosiciones();
        assertEquals(3, tabla.size());
        EntradaTabla p1 = tabla.get(0);
        assertEquals(1, p1.idPareja());
        assertEquals(2, p1.ultimaRonda());
        assertEquals(25, p1.vidaFinal());
    }

    @Test
    void laTablaCoincideConElOrdenDeEntradaTabla() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        cronista.notificar(new ResultadoCombate(1, 2, "P2", 1, "P1", 0, 2, "P2", 70, 6, false));
        cronista.notificar(new ResultadoCombate(1, 3, "P3", 3, "P3", 20, 4, "P4", 0, 7, false));

        List<EntradaTabla> tabla = cronista.tablaDePosiciones();
        List<EntradaTabla> ordenada = new ArrayList<>(tabla);
        ordenada.sort(EntradaTabla.ORDEN);
        assertEquals(ordenada, tabla);
    }
}
