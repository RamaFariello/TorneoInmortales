package inmortales.presentacion;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inmortales.acontecimientos.Acontecimiento;

class CronistaTest {
    private Acontecimiento resultado(int ronda, int ganadora, int vidaGanadora, int perdedora, int vidaPerdedora) {
        return Acontecimiento.resultadoCombate(ronda, 5, ganadora, "P" + ganadora, vidaGanadora,
                perdedora, "P" + perdedora, vidaPerdedora);
    }

    private List<Integer> ids(List<RegistroPosicion> tabla) {
        List<Integer> r = new ArrayList<>();
        for (RegistroPosicion p : tabla) r.add(p.getIdPareja());
        return r;
    }

    @Test
    @DisplayName("El Cronista registra los acontecimientos en orden y permite consultar el historial")
    void historial() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        Acontecimiento inicio = Acontecimiento.inicioCombate(1, 1, "P1", 2, "P2");
        Acontecimiento fin = resultado(1, 1, 120, 2, 0);
        cronista.actualizar(inicio);
        cronista.actualizar(fin);
        assertEquals(Arrays.asList(inicio, fin), cronista.getHistorial());
        assertEquals(Arrays.asList(fin), cronista.getResultados());
        assertThrows(UnsupportedOperationException.class, () -> cronista.getHistorial().clear());
    }

    @Test
    @DisplayName("Tabla: ordenada por ultima ronda jugada y, en empate, por vida total del ultimo combate")
    void tablaPorRondaYVida() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        cronista.actualizar(resultado(1, 1, 120, 2, 30));
        cronista.actualizar(resultado(1, 3, 150, 4, 40));
        cronista.actualizar(resultado(2, 3, 100, 1, 50));

        assertEquals(Arrays.asList(3, 1, 4, 2), ids(cronista.generarTablaPosiciones()));
    }

    @Test
    @DisplayName("Tabla: si coinciden ronda y vida total, desempata el menor identificador")
    void tablaDesempateIdentificador() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        cronista.actualizar(resultado(1, 1, 120, 2, 30));
        cronista.actualizar(resultado(1, 3, 150, 4, 30));
        cronista.actualizar(resultado(2, 3, 100, 1, 100));

        assertEquals(Arrays.asList(1, 3, 2, 4), ids(cronista.generarTablaPosiciones()));
    }

    @Test
    @DisplayName("Tabla: el desempate por identificador no depende del orden en que llegaron los resultados")
    void tablaDesempateIndependienteDelOrdenDeLlegada() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        cronista.actualizar(resultado(1, 4, 50, 3, 50));
        assertEquals(Arrays.asList(3, 4), ids(cronista.generarTablaPosiciones()));
    }

    @Test
    @DisplayName("Tabla: cada pareja figura con el resultado de su ULTIMO combate")
    void tablaUsaUltimoCombate() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        cronista.actualizar(resultado(1, 1, 190, 2, 0));
        cronista.actualizar(resultado(2, 3, 80, 1, 10));
        RegistroPosicion p1 = null;
        for (RegistroPosicion r : cronista.generarTablaPosiciones()) if (r.getIdPareja() == 1) p1 = r;
        assertNotNull(p1);
        assertEquals(2, p1.getUltimaRonda());
        assertEquals(10, p1.getVidaTotalFinal());
        assertEquals(3, cronista.generarTablaPosiciones().size());
    }
}
