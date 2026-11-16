package inmortales.combatientes;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inmortales.Utiles;
import inmortales.tecnicas.*;
import inmortales.torneo.Pareja;

class CriterioDeAccionTest {
    private final Tecnica corte = new CorteDeLosMilAmaneceres();
    private final Tecnica rugido = new RugidoDeLaBestiaPrimordial();
    private final Tecnica muro = new MuroDelCuerpoDorado();
    private final Tecnica meditacion = new MeditacionDelInmortal();
    private Combatiente actor;
    private Combatiente companero;
    private Combatiente rival;
    private Pareja pareja;

    @BeforeEach
    void preparar() {
        actor = new CultivadorDeLaEspada(1, "Actor", null);
        companero = new CultivadorDeLaEspada(2, "Companero", null);
        rival = new Utiles.CombatienteDePrueba(20, 5000, 50);
        pareja = new Pareja(1, actor, companero);
        pareja.prepararParaCombate();
    }

    private void aprender(Tecnica... tecnicas) {
        for (Tecnica t : tecnicas) actor.aprenderTecnica(t);
    }

    private void usar(Combatiente c, Tecnica t) {
        c.usarTecnica(t, rival);
        pareja.registrarUsoTecnica(c, t);
    }

    @Test
    @DisplayName("Primero: tecnica de recuperacion si le falta vida (aunque tenga defensiva y ofensiva)")
    void recuperacionPrimero() {
        aprender(corte, muro, meditacion);
        actor.perderVida(10);
        assertSame(meditacion, actor.elegirTecnica(pareja));
    }

    @Test
    @DisplayName("Con la vida completa no usa recuperacion: sigue con la defensiva")
    void sinFaltaDeVidaNoRecupera() {
        aprender(corte, muro, meditacion);
        assertSame(muro, actor.elegirTecnica(pareja));
    }

    @Test
    @DisplayName("Con defensa activa pasa a la ofensiva")
    void conDefensaActivaPasaAOfensiva() {
        aprender(muro, corte);
        actor.activarReduccionDanio(0.30);
        assertSame(corte, actor.elegirTecnica(pareja));
    }

    @Test
    @DisplayName("Tercero: la ofensiva con mas usos restantes")
    void ofensivaConMasUsos() {
        aprender(corte, rugido);
        assertSame(corte, actor.elegirTecnica(pareja));
        usar(actor, corte);
        assertSame(rugido, actor.elegirTecnica(pareja));
    }

    @Test
    @DisplayName("En empate gana la primera segun el orden de la escuela")
    void empateOrdenDeEscuela() {
        aprender(rugido, corte);
        assertSame(rugido, actor.elegirTecnica(pareja));
    }

    @Test
    @DisplayName("Si una tecnica no es posible por energia, se pasa a la siguiente del criterio")
    void saltaLoQueNoPuedePagar() {
        aprender(meditacion, muro, corte);
        actor.perderVida(10);
        actor.gastarEnergia(40);
        assertSame(corte, actor.elegirTecnica(pareja));
    }

    @Test
    @DisplayName("Sin tecnicas, sin energia, sin usos o con la tecnica reservada: ataque basico (null)")
    void ataqueBasicoCuandoNadaEsPosible() {
        assertNull(actor.elegirTecnica(pareja), "sin tecnicas");

        aprender(corte, muro, meditacion);
        actor.gastarEnergia(50);
        assertNull(actor.elegirTecnica(pareja), "sin energia");
    }

    @Test
    @DisplayName("Sin usos restantes: ataque basico")
    void sinUsosAtaqueBasico() {
        aprender(corte);
        for (int i = 0; i < Combatiente.USOS_POR_TECNICA; i++) {
            actor.recuperarEnergia(50);
            usar(actor, corte);
        }
        actor.recuperarEnergia(50);
        assertNull(actor.elegirTecnica(pareja));
    }

    @Test
    @DisplayName("Si el companero la reservo: ataque basico")
    void reglaDelCompaneroAtaqueBasico() {
        aprender(corte);
        companero.aprenderTecnica(corte);
        usar(companero, corte);
        assertNull(actor.elegirTecnica(pareja));
    }

    @Test
    @DisplayName("El criterio es determinista: mismo estado, misma eleccion")
    void determinista() {
        aprender(corte, rugido, muro, meditacion);
        Tecnica primera = actor.elegirTecnica(pareja);
        for (int i = 0; i < 10; i++) assertSame(primera, actor.elegirTecnica(pareja));
    }

    @Test
    @DisplayName("Objetivo: el rival en pie con menos vida actual")
    void objetivoConMenosVida() {
        Combatiente a = Utiles.neutro(10);
        Combatiente b = Utiles.neutro(11);
        b.perderVida(10);
        assertSame(b, actor.elegirObjetivo(List.of(a, b)));
        a.perderVida(30);
        assertSame(a, actor.elegirObjetivo(List.of(a, b)));
    }

    @Test
    @DisplayName("Objetivo: en empate de vida, el de menor identificador")
    void objetivoDesempateMenorId() {
        Combatiente a = Utiles.neutro(10);
        Combatiente b = Utiles.neutro(11);
        assertSame(a, actor.elegirObjetivo(List.of(a, b)));
        assertSame(a, actor.elegirObjetivo(List.of(b, a)), "no depende del orden de la lista");
    }

    @Test
    @DisplayName("Un caido nunca es elegido como objetivo, aunque tenga menos vida")
    void caidoNoEsObjetivo() {
        Combatiente a = Utiles.neutro(10);
        Combatiente b = Utiles.neutro(11);
        a.perderVida(1000);
        assertSame(b, actor.elegirObjetivo(List.of(a, b)));
        b.perderVida(1000);
        assertThrows(IllegalArgumentException.class, () -> actor.elegirObjetivo(List.of(a, b)));
    }
}
