package inmortales.torneo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ReglasCombateTest {

    @Test
    void lasReglasPorDefectoUsanLosValoresDefinidos() {
        ReglasCombate reglas = ReglasCombate.porDefecto();

        assertEquals(ReglasCombate.LIMITE_ASALTOS, reglas.limiteAsaltos());
        assertEquals(ReglasCombate.ENERGIA_POR_ASALTO, reglas.energiaPorAsalto());
    }

    @Test
    void sePuedenDefinirOtrosValores() {
        ReglasCombate reglas = new ReglasCombate(10, 3);

        assertEquals(10, reglas.limiteAsaltos());
        assertEquals(3, reglas.energiaPorAsalto());
    }

    @Test
    void elLimiteDeAsaltosDebeSerAlMenosUno() {
        assertThrows(IllegalArgumentException.class, () -> new ReglasCombate(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new ReglasCombate(-3, 5));
    }

    @Test
    void laEnergiaPorAsaltoNoPuedeSerNegativa() {
        assertThrows(IllegalArgumentException.class, () -> new ReglasCombate(10, -1));
    }

    @Test
    void laEnergiaPorAsaltoPuedeSerCero() {
        assertEquals(0, new ReglasCombate(10, 0).energiaPorAsalto());
    }
}
