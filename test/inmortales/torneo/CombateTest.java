package inmortales.torneo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inmortales.Utiles;
import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.ObservadorTorneo;
import inmortales.acontecimientos.TipoAcontecimiento;
import inmortales.combatientes.Combatiente;
import inmortales.combatientes.CultivadorDeLaEspada;
import inmortales.combatientes.HechiceroDelVacio;
import inmortales.combatientes.MonjeDelCuerpoCelestial;
import inmortales.presentacion.CronistaDeLaSecta;
import inmortales.tecnicas.RugidoDeLaBestiaPrimordial;
import inmortales.tecnicas.Tecnica;

class CombateTest {
    private CronistaDeLaSecta cronista;
    private List<ObservadorTorneo> observadores;

    @BeforeEach
    void preparar() {
        cronista = new CronistaDeLaSecta();
        observadores = Collections.<ObservadorTorneo>singletonList(cronista);
    }

    private Pareja jugar(Pareja una, Pareja otra, int limite) {
        return new Combate(1, una, otra, observadores, limite).ejecutar();
    }

    private List<Integer> ids(List<Acontecimiento> acontecimientos, String clave) {
        List<Integer> valores = new ArrayList<>();
        for (Acontecimiento a : acontecimientos) {
            valores.add(a.getDatoInt(clave));
        }
        return valores;
    }

    private List<String> campo(List<Acontecimiento> acontecimientos, String clave) {
        List<String> r = new ArrayList<>();
        for (Acontecimiento a : acontecimientos) r.add(a.getDatoTexto(clave));
        return r;
    }

    @Test
    @DisplayName("Empieza la pareja de menor id y dentro de la pareja actua primero el de menor id")
    void ordenDeTurnos() {
        Pareja p2 = new Pareja(2, Utiles.neutro(1), Utiles.neutro(2));
        Pareja p1 = new Pareja(1, Utiles.neutro(3), Utiles.neutro(4));

        jugar(p2, p1, 1);

        List<Acontecimiento> acciones = Utiles.acciones(cronista.getHistorial());
        assertEquals(Arrays.asList("C3", "C4", "C1", "C2"), campo(acciones, "actor"));
        assertEquals(Arrays.asList("C1", "C1", "C3", "C3"), campo(acciones, "objetivo"));
    }

    @Test
    @DisplayName("En cada asalto cada integrante en pie realiza una accion, con su ronda y asalto")
    void cadaIntegranteUnaAccionPorAsalto() {
        jugar(new Pareja(1, Utiles.neutro(1), Utiles.neutro(2)), new Pareja(2, Utiles.neutro(3), Utiles.neutro(4)), 3);
        List<Acontecimiento> acciones = Utiles.acciones(cronista.getHistorial());
        assertEquals(3 * 4, acciones.size());
        assertEquals(1, acciones.get(0).getRonda());
        assertEquals(1, acciones.get(0).getAsalto());
        assertEquals(3, acciones.get(acciones.size() - 1).getAsalto());
    }

    @Test
    @DisplayName("Un caido no actua ni es elegido como objetivo")
    void caidoNoActuaNiEsObjetivo() {
        Combatiente fragil = new Utiles.CombatienteDePrueba(3, 5, 50);
        Pareja p1 = new Pareja(1, Utiles.neutro(1), Utiles.neutro(2));
        Pareja p2 = new Pareja(2, fragil, Utiles.neutro(4));

        jugar(p1, p2, ReglasCombate.LIMITE_ASALTOS);

        List<Acontecimiento> acciones = Utiles.acciones(cronista.getHistorial());
        int eliminacionesDeC3 = 0;
        for (Acontecimiento e : Utiles.eliminaciones(cronista.getHistorial())) {
            if (e.getDatoInt("idCombatiente") == 3) eliminacionesDeC3++;
        }
        assertEquals(1, eliminacionesDeC3);
        assertFalse(ids(acciones, "idActor").contains(3), "C3 cayo antes de su turno");
        assertEquals(1, Collections.frequency(ids(acciones, "idObjetivo"), 3),
                "solo el golpe que lo derribo lo tuvo como objetivo");
    }

    @Test
    @DisplayName("El combate termina cuando una pareja queda sin integrantes en pie")
    void terminaCuandoUnaParejaCae() {
        Pareja p1 = new Pareja(1, Utiles.neutro(1), Utiles.neutro(2));
        Pareja p2 = new Pareja(2, new Utiles.CombatienteDePrueba(3, 5, 50), Utiles.neutro(4));
        Combate combate = new Combate(1, p1, p2, observadores);

        Pareja ganadora = combate.ejecutar();

        assertSame(p1, ganadora);
        assertFalse(p2.estaEnPie());
        assertTrue(combate.getAsaltosJugados() < ReglasCombate.LIMITE_ASALTOS);
        List<Acontecimiento> h = cronista.getHistorial();
        assertEquals(TipoAcontecimiento.RESULTADO_COMBATE, h.get(h.size() - 1).getTipo());
        assertEquals(TipoAcontecimiento.ELIMINACION, h.get(h.size() - 2).getTipo(),
                "no hay acciones despues de la ultima eliminacion");
    }

    @Test
    @DisplayName("Se comunican inicio, acciones, eliminaciones y resultado con los datos del momento")
    void acontecimientosDelCombate() {
        Pareja p1 = new Pareja(1, Utiles.neutro(1), Utiles.neutro(2));
        Pareja p2 = new Pareja(2, new Utiles.CombatienteDePrueba(3, 5, 50), Utiles.neutro(4));
        jugar(p1, p2, ReglasCombate.LIMITE_ASALTOS);

        List<Acontecimiento> h = cronista.getHistorial();
        assertEquals(TipoAcontecimiento.INICIO_COMBATE, h.get(0).getTipo());
        assertEquals(1, h.get(0).getRonda());
        Acontecimiento r = cronista.getResultados().get(0);
        assertEquals(1, r.getRonda());
        assertEquals(1, r.getDatoInt("idGanadora"));
        assertEquals(2, r.getDatoInt("idPerdedora"));
        assertEquals(p1.getVidaTotal(), r.getDatoInt("vidaGanadora"));
        assertEquals(0, r.getDatoInt("vidaPerdedora"));
        assertEquals(h.size() - 1, h.indexOf(r));
    }

    @Test
    @DisplayName("La eliminacion informa a quien cayo y quien era su companero")
    void eliminacionInformaCompanero() {
        Pareja p1 = new Pareja(1, Utiles.neutro(1), Utiles.neutro(2));
        Pareja p2 = new Pareja(2, new Utiles.CombatienteDePrueba(3, 5, 50), Utiles.neutro(4));
        jugar(p1, p2, 1);
        Acontecimiento eliminacion = Utiles.eliminaciones(cronista.getHistorial()).get(0);
        assertEquals(3, eliminacion.getDatoInt("idCombatiente"));
        assertEquals(2, eliminacion.getDatoInt("idPareja"));
        assertEquals(4, eliminacion.getDatoInt("idCompanero"));
    }

    @Test
    @DisplayName("Al comenzar cada asalto cada combatiente en pie recupera energia sin superar su maximo")
    void energiaPorAsalto() {
        Tecnica rugido = new RugidoDeLaBestiaPrimordial();
        Combatiente a1 = new CultivadorDeLaEspada(1, "A1", null);
        Combatiente a2 = new CultivadorDeLaEspada(2, "A2", null);
        a1.aprenderTecnica(rugido);
        a2.aprenderTecnica(rugido);
        Pareja p1 = new Pareja(1, a1, a2);
        Pareja p2 = new Pareja(2, new MonjeDelCuerpoCelestial(3, "M3", null), new MonjeDelCuerpoCelestial(4, "M4", null));

        jugar(p1, p2, 2);

        assertEquals(5, a1.getEnergiaActual());
        assertEquals(2, a1.getUsosRealizados(rugido));

        assertEquals(0, a2.getUsosRealizados(rugido));
        assertEquals(50, a2.getEnergiaActual(), "recupera hasta el maximo y no lo supera");
    }

    @Test
    @DisplayName("Los usos se conservan entre combates y la vida y la energia se recuperan")
    void entreCombates() {
        Tecnica rugido = new RugidoDeLaBestiaPrimordial();
        Combatiente a1 = new CultivadorDeLaEspada(1, "A1", null);
        a1.aprenderTecnica(rugido);
        Pareja p1 = new Pareja(1, a1, new CultivadorDeLaEspada(2, "A2", null));
        Pareja p2 = new Pareja(2, new MonjeDelCuerpoCelestial(3, "M3", null), new MonjeDelCuerpoCelestial(4, "M4", null));
        jugar(p1, p2, 2);
        assertTrue(a1.getVidaActual() < 100);
        assertEquals(5, a1.getEnergiaActual());

        jugar(p1, new Pareja(3, Utiles.neutro(5), Utiles.neutro(6)), 0);

        assertEquals(100, a1.getVidaActual());
        assertEquals(50, a1.getEnergiaActual());
        assertEquals(3, a1.usosRestantes(rugido), "los 2 usos del primer combate siguen gastados");
    }

    @Test
    @DisplayName("Al comenzar el combate se recuperan tambien los caidos del combate anterior")
    void caidosVuelvenAlSiguienteCombate() {
        Combatiente fragil = new Utiles.CombatienteDePrueba(3, 5, 50);
        Pareja p2 = new Pareja(2, fragil, Utiles.neutro(4));
        jugar(new Pareja(1, Utiles.neutro(1), Utiles.neutro(2)), p2, ReglasCombate.LIMITE_ASALTOS);
        assertEquals(0, fragil.getVidaActual());

        jugar(p2, new Pareja(5, Utiles.neutro(7), Utiles.neutro(8)), 0);

        assertEquals(5, fragil.getVidaActual());
        assertTrue(fragil.estaEnPie());
    }

    @Test
    @DisplayName("Al alcanzar el limite gana la pareja con mas vida total (aunque tenga mayor id)")
    void limiteGanaMasVidaTotal() {
        Pareja p1 = new Pareja(1, new HechiceroDelVacio(1, "H1", null), new HechiceroDelVacio(2, "H2", null));
        Pareja p2 = new Pareja(2, new CultivadorDeLaEspada(3, "C3", null), new CultivadorDeLaEspada(4, "C4", null));

        Pareja ganadora = jugar(p1, p2, 1);

        assertSame(p2, ganadora);
        Acontecimiento r = cronista.getResultados().get(0);
        assertEquals(2, r.getDatoInt("idGanadora"));
        assertEquals(180, r.getDatoInt("vidaGanadora"));
        assertEquals(176, r.getDatoInt("vidaPerdedora"));
        assertEquals(1, r.getDatoInt("asaltos"));
    }

    @Test
    @DisplayName("Al alcanzar el limite, en empate de vida total gana la pareja de menor id")
    void limiteEmpateGanaMenorId() {
        Pareja p5 = new Pareja(5, Utiles.neutro(1), Utiles.neutro(2));
        Pareja p3 = new Pareja(3, Utiles.neutro(3), Utiles.neutro(4));

        Pareja ganadora = jugar(p5, p3, 0);

        assertSame(p3, ganadora);
        assertEquals(200, p3.getVidaTotal());
        assertEquals(200, p5.getVidaTotal());
    }

    @Test
    @DisplayName("El combate no puede jugarse dos veces")
    void noSeJuegaDosVeces() {
        Combate combate = new Combate(1, new Pareja(1, Utiles.neutro(1), Utiles.neutro(2)),
                new Pareja(2, Utiles.neutro(3), Utiles.neutro(4)), observadores, 1);
        combate.ejecutar();
        assertThrows(IllegalStateException.class, combate::ejecutar);
    }

    @Test
    @DisplayName("El combate es determinista: el mismo estado inicial produce el mismo resultado")
    void determinista() {
        List<Acontecimiento> primero = jugarEscenarioCompleto();
        List<Acontecimiento> segundo = jugarEscenarioCompleto();
        assertFalse(primero.isEmpty());
        assertEquals(primero, segundo);
    }

    private List<Acontecimiento> jugarEscenarioCompleto() {
        List<Pareja> parejas = Utiles.parejas(2);
        CronistaDeLaSecta c = new CronistaDeLaSecta();
        new Combate(1, parejas.get(0), parejas.get(1), Collections.<ObservadorTorneo>singletonList(c)).ejecutar();
        return c.getHistorial();
    }

    @Test
    @DisplayName("Todos los observadores reciben los mismos acontecimientos, en el orden en que se registraron")
    void variosObservadores() {
        final List<String> orden = new ArrayList<>();
        ObservadorTorneo primero = new ObservadorTorneo() {
            public void actualizar(Acontecimiento a) { orden.add("1"); }
        };
        ObservadorTorneo segundo = new ObservadorTorneo() {
            public void actualizar(Acontecimiento a) { orden.add("2"); }
        };
        new Combate(1, new Pareja(1, Utiles.neutro(1), Utiles.neutro(2)),
                new Pareja(2, Utiles.neutro(3), Utiles.neutro(4)), Arrays.asList(primero, segundo), 0).ejecutar();
        assertEquals(Arrays.asList("1", "2", "1", "2"), orden);
    }
}
