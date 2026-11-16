package inmortales.acontecimientos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import inmortales.tecnicas.TipoTecnica;

class AcontecimientosTest {

    @Test
    void cadaAcontecimientoGuardaLosDatosDelMomento() {
        InicioCombate inicio = new InicioCombate(2, 3, "Pareja 3", 4, "Pareja 4");
        assertEquals(2, inicio.ronda());
        assertEquals(3, inicio.idPareja1());
        assertEquals("Pareja 4", inicio.pareja2());

        Eliminacion eliminacion = new Eliminacion(1, 5, 7, "Kael", 2);
        assertEquals(7, eliminacion.idCombatiente());
        assertEquals(5, eliminacion.asalto());

        Proclamacion proclamacion = new Proclamacion(1, "Pareja 1", "Aurelio", "Nebula");
        assertEquals("Aurelio", proclamacion.primerSanto());
        assertEquals("Nebula", proclamacion.segundoSanto());
    }

    @Test
    void laDescripcionIncluyeLosNombresYLosNumerosRelevantes() {
        assertTrue(new InicioCombate(1, 1, "Pareja 1", 2, "Pareja 2").describir().contains("Pareja 1 vs Pareja 2"));
        assertTrue(new Eliminacion(1, 4, 7, "Kael", 2).describir().contains("Kael"));
        assertTrue(new Eliminacion(1, 4, 7, "Kael", 2).describir().contains("asalto 4"));
        assertTrue(new Proclamacion(1, "Pareja 1", "Aurelio", "Nebula").describir().contains("Aurelio"));
        assertTrue(new Proclamacion(1, "Pareja 1", "Aurelio", "Nebula").describir().contains("Nebula"));
    }

    @Test
    void elResultadoDelCombateIndicaLaVidaTotalDeCadaPareja() {
        ResultadoCombate r = new ResultadoCombate(1, 3, "Pareja 3", 1, "Pareja 1", 0, 3, "Pareja 3", 55, 12, false);
        assertEquals(0, r.vidaDe(1));
        assertEquals(55, r.vidaDe(3));
        assertThrows(IllegalArgumentException.class, () -> r.vidaDe(9));
        String texto = r.describir();
        assertTrue(texto.contains("Pareja 3"));
        assertTrue(texto.contains("55"));
        assertFalse(texto.contains("limite"));
    }

    @Test
    void unResultadoPorLimiteDeAsaltosLoIndica() {
        ResultadoCombate r = new ResultadoCombate(1, 1, "Pareja 1", 1, "Pareja 1", 40, 2, "Pareja 2", 30, 25, true);
        assertTrue(r.porLimiteDeAsaltos());
        assertEquals(25, r.asaltos());
    }

    @Test
    void laAccionDistingueAtaqueBasicoDeTecnica() {
        AccionRealizada basico = new AccionRealizada(1, 1, 1, "Aurelio", "Ataque basico", true,
                TipoTecnica.OFENSIVA, 2, "Kael", 10, 90, 40);
        AccionRealizada tecnica = new AccionRealizada(1, 1, 1, "Aurelio", "Corte", false,
                TipoTecnica.OFENSIVA, 2, "Kael", 20, 70, 30);
        assertTrue(basico.ataqueBasico());
        assertFalse(tecnica.ataqueBasico());
        assertTrue(tecnica.describir().contains("Corte"));
        assertTrue(tecnica.describir().contains("20"));
    }

    @Test
    void soloLasAccionesSonDetalleParaElHeraldo() {
        assertTrue(new AccionRealizada(1, 1, 1, "A", "x", true, TipoTecnica.OFENSIVA, 2, "B", 1, 1, 1).esDetalle());
        assertFalse(new InicioCombate(1, 1, "P1", 2, "P2").esDetalle());
        assertFalse(new Eliminacion(1, 1, 1, "A", 1).esDetalle());
        assertFalse(new Proclamacion(1, "P1", "A", "B").esDetalle());
    }

    @Test
    void dosAcontecimientosConLosMismosDatosSonIguales() {
        assertEquals(new Eliminacion(1, 2, 3, "Kael", 4), new Eliminacion(1, 2, 3, "Kael", 4));
    }
}
