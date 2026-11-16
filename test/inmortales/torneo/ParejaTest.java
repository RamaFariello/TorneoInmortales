package inmortales.torneo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inmortales.Utiles;
import inmortales.combatientes.Combatiente;
import inmortales.combatientes.CultivadorDeLaEspada;
import inmortales.excepciones.TecnicaNoDisponibleException;
import inmortales.tecnicas.*;

class ParejaTest {
    private final Tecnica corte = new CorteDeLosMilAmaneceres();
    private final Tecnica meditacion = new MeditacionDelInmortal();
    private Combatiente a;
    private Combatiente b;
    private Combatiente objetivo;
    private Pareja pareja;

    @BeforeEach
    void preparar() {
        a = new CultivadorDeLaEspada(1, "A", null);
        b = new CultivadorDeLaEspada(2, "B", null);
        objetivo = new Utiles.CombatienteDePrueba(20, 5000, 50);
        for (Combatiente c : Arrays.asList(a, b)) {
            c.aprenderTecnica(corte);
            c.aprenderTecnica(meditacion);
        }
        pareja = new Pareja(1, a, b);
        pareja.prepararParaCombate();
    }

    private void usar(Combatiente c, Tecnica t, Combatiente objetivoDeLaTecnica) {
        c.usarTecnica(t, objetivoDeLaTecnica);
        pareja.registrarUsoTecnica(c, t);
    }

    @Test
    @DisplayName("La pareja tiene identificador y sus integrantes quedan ordenados por id")
    void integrantesOrdenados() {
        Pareja invertida = new Pareja(7, b, a);
        assertEquals(7, invertida.getId());
        assertEquals(Arrays.asList(a, b), invertida.getIntegrantes());
    }

    @Test
    @DisplayName("Una pareja necesita dos combatientes distintos")
    void dosDistintos() {
        assertThrows(IllegalArgumentException.class, () -> new Pareja(1, a, a));
        assertThrows(IllegalArgumentException.class, () -> new Pareja(1, a, new CultivadorDeLaEspada(1, "otro", null)));
        assertThrows(IllegalArgumentException.class, () -> new Pareja(1, a, null));
    }

    @Test
    @DisplayName("Dos parejas son la misma si tienen el mismo identificador")
    void igualdadPorId() {
        assertEquals(new Pareja(3, a, b), new Pareja(3, Utiles.neutro(8), Utiles.neutro(9)));
        assertNotEquals(new Pareja(3, a, b), new Pareja(4, a, b));
    }

    @Test
    @DisplayName("La vida total suma la vida actual de los integrantes; un caido suma cero")
    void vidaTotal() {
        assertEquals(200, pareja.getVidaTotal());
        b.perderVida(1000);
        assertEquals(100, pareja.getVidaTotal());
    }

    @Test
    @DisplayName("Una pareja esta en pie mientras alguno de sus integrantes siga en pie")
    void enPie() {
        a.perderVida(1000);
        assertTrue(pareja.estaEnPie());
        assertEquals(Collections.singletonList(b), pareja.getIntegrantesEnPie());
        b.perderVida(1000);
        assertFalse(pareja.estaEnPie());
        assertTrue(pareja.getIntegrantesEnPie().isEmpty());
    }

    @Test
    @DisplayName("Se puede consultar al companero de un integrante")
    void companero() {
        assertSame(b, pareja.getCompanero(a));
        assertSame(a, pareja.getCompanero(b));
    }

    @Test
    @DisplayName("El companero no puede usar una tecnica mientras el primero conserve usos de ella")
    void companeroBloqueado() {
        usar(a, corte, objetivo);

        assertFalse(pareja.puedeUsarTecnica(b, corte));
        assertThrows(TecnicaNoDisponibleException.class, () -> pareja.registrarUsoTecnica(b, corte));
        assertEquals(0, b.getUsosRealizados(corte));
        assertTrue(pareja.puedeUsarTecnica(a, corte), "el primero sigue pudiendo usarla");
        assertTrue(pareja.puedeUsarTecnica(b, meditacion), "otras tecnicas no se bloquean");
    }

    @Test
    @DisplayName("La regla vale aunque cada uno tenga su propia instancia de la misma tecnica")
    void reglaConInstanciasDistintas() {
        Combatiente c = new CultivadorDeLaEspada(3, "C", null);
        Combatiente d = new CultivadorDeLaEspada(4, "D", null);
        c.aprenderTecnica(new CorteDeLosMilAmaneceres());
        d.aprenderTecnica(new CorteDeLosMilAmaneceres());
        Pareja otra = new Pareja(2, c, d);
        otra.prepararParaCombate();
        Tecnica deC = new CorteDeLosMilAmaneceres();
        c.usarTecnica(deC, objetivo);
        otra.registrarUsoTecnica(c, deC);
        assertFalse(otra.puedeUsarTecnica(d, new CorteDeLosMilAmaneceres()));
    }

    @Test
    @DisplayName("El companero puede usarla si el primero agota sus usos")
    void companeroLibreSiAgotaUsos() {
        usar(a, meditacion, a);
        assertFalse(pareja.puedeUsarTecnica(b, meditacion));
        for (int i = 1; i < Combatiente.USOS_POR_TECNICA; i++) {
            a.recuperarEnergia(50);
            usar(a, meditacion, a);
        }
        assertEquals(0, a.usosRestantes(meditacion));
        assertTrue(pareja.puedeUsarTecnica(b, meditacion));
    }

    @Test
    @DisplayName("El companero puede usarla si el primero queda fuera de combate")
    void companeroLibreSiCae() {
        usar(a, corte, objetivo);
        assertFalse(pareja.puedeUsarTecnica(b, corte));
        a.perderVida(1000);
        assertTrue(pareja.puedeUsarTecnica(b, corte));
    }

    @Test
    @DisplayName("La restriccion termina con el combate: en el siguiente cualquiera puede usarla")
    void restriccionTerminaConElCombate() {
        usar(a, corte, objetivo);
        assertFalse(pareja.puedeUsarTecnica(b, corte));

        pareja.prepararParaCombate();

        assertTrue(pareja.puedeUsarTecnica(b, corte));
        assertTrue(pareja.puedeUsarTecnica(a, corte));
        assertEquals(4, a.usosRestantes(corte), "pero los usos de A se conservan");
    }

    @Test
    @DisplayName("reiniciarRestricciones borra el registro de la pareja")
    void reiniciarRestricciones() {
        usar(a, corte, objetivo);
        pareja.reiniciarRestricciones();
        assertTrue(pareja.puedeUsarTecnica(b, corte));
    }

    @Test
    @DisplayName("El ataque basico no cuenta para la regla del companero")
    void ataqueBasicoNoReserva() {
        a.atacarBasico(objetivo);
        assertTrue(pareja.puedeUsarTecnica(b, corte));
        assertTrue(pareja.puedeUsarTecnica(a, corte));
    }
}
