package inmortales.combatientes;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inmortales.Utiles;
import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.TipoAcontecimiento;
import inmortales.equipamiento.*;
import inmortales.excepciones.EquipamientoInvalidoException;
import inmortales.excepciones.TecnicaNoDisponibleException;
import inmortales.tecnicas.*;
import inmortales.torneo.Pareja;

class CombatienteTest {
    private int danioDe(Combatiente atacante, Tecnica tecnica, Combatiente objetivo) {
        int antes = objetivo.getVidaActual();
        atacante.usarTecnica(tecnica, objetivo);
        return antes - objetivo.getVidaActual();
    }

    @Test
    @DisplayName("Una misma tecnica daña distinto segun el tipo del ejecutor")
    void mismaTecnicaDaniaDistintoSegunTipo() {
        Tecnica corte = new CorteDeLosMilAmaneceres();
        Combatiente cultivador = new CultivadorDeLaEspada(1, "Cul", null);
        Combatiente monje = new MonjeDelCuerpoCelestial(2, "Mon", null);
        Combatiente hechicero = new HechiceroDelVacio(3, "Hec", null);
        for (Combatiente c : new Combatiente[] { cultivador, monje, hechicero }) c.aprenderTecnica(corte);

        assertEquals(25, danioDe(cultivador, corte, new HechiceroDelVacio(10, "o1", null)),
                "el Cultivador potencia el daño (+25%)");
        assertEquals(20, danioDe(monje, corte, new HechiceroDelVacio(11, "o2", null)));
        assertEquals(20, danioDe(hechicero, corte, new HechiceroDelVacio(12, "o3", null)));
    }

    @Test
    @DisplayName("El Monje reduce el daño que recibe")
    void monjeReduceDanioRecibido() {
        Combatiente monje = new MonjeDelCuerpoCelestial(1, "Mon", null);
        Combatiente hechicero = new HechiceroDelVacio(2, "Hec", null);
        assertEquals(16, monje.recibirDanio(20));
        assertEquals(20, hechicero.recibirDanio(20));
    }

    @Test
    @DisplayName("El Hechicero gasta menos energia al ejecutar tecnicas")
    void hechiceroGastaMenosEnergia() {
        Tecnica corte = new CorteDeLosMilAmaneceres();
        Combatiente hechicero = new HechiceroDelVacio(1, "Hec", null);
        Combatiente cultivador = new CultivadorDeLaEspada(2, "Cul", null);
        hechicero.aprenderTecnica(corte);
        cultivador.aprenderTecnica(corte);
        hechicero.usarTecnica(corte, new MonjeDelCuerpoCelestial(10, "o", null));
        cultivador.usarTecnica(corte, new MonjeDelCuerpoCelestial(11, "o", null));
        assertEquals(50 - 8, hechicero.getEnergiaActual());
        assertEquals(50 - 10, cultivador.getEnergiaActual());
    }

    @Test
    @DisplayName("Se puede agregar un tipo nuevo sin tocar tecnicas ni torneo")
    void tipoNuevoSinModificarTecnicas() {
        Combatiente berserker = new Combatiente(9, "Berserker", null) {
            @Override public String getTipo() { return "Berserker"; }
            @Override protected int calcularDanioCausado(int danioBase) { return danioBase * 2; }
        };
        Tecnica corte = new CorteDeLosMilAmaneceres();
        berserker.aprenderTecnica(corte);
        assertEquals(40, danioDe(berserker, corte, new HechiceroDelVacio(1, "o", null)));
    }

    @Test
    @DisplayName("Dos combatientes son el mismo si tienen el mismo id, sin importar el nombre")
    void igualdadPorId() {
        assertEquals(new CultivadorDeLaEspada(1, "A", null), new MonjeDelCuerpoCelestial(1, "B", null));
        assertEquals(new CultivadorDeLaEspada(1, "A", null).hashCode(),
                new MonjeDelCuerpoCelestial(1, "B", null).hashCode());
        assertNotEquals(new CultivadorDeLaEspada(1, "A", null), new CultivadorDeLaEspada(2, "A", null));
    }

    @Test
    @DisplayName("Nombre nulo o vacio se rechaza con IllegalArgumentException (no NPE)")
    void validacionDeNombre() {
        assertThrows(IllegalArgumentException.class, () -> new CultivadorDeLaEspada(1, null, null));
        assertThrows(IllegalArgumentException.class, () -> new CultivadorDeLaEspada(1, "  ", null));
    }

    @Test
    @DisplayName("No se ejecuta una tecnica sin energia suficiente")
    void sinEnergiaNoEjecuta() {
        Tecnica rugido = new RugidoDeLaBestiaPrimordial();
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.aprenderTecnica(rugido);
        Combatiente objetivo = new HechiceroDelVacio(2, "o", null);
        c.usarTecnica(rugido, objetivo);
        c.usarTecnica(rugido, objetivo);
        assertEquals(0, c.getEnergiaActual());

        assertFalse(c.puedeUsar(rugido));
        assertThrows(TecnicaNoDisponibleException.class, () -> c.usarTecnica(rugido, objetivo));
        assertEquals(2, c.getUsosRealizados(rugido), "el intento fallido no consume usos");
    }

    @Test
    @DisplayName("No se ejecuta una tecnica sin usos restantes aunque haya energia")
    void sinUsosNoEjecuta() {
        Tecnica meditacion = new MeditacionDelInmortal();
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.aprenderTecnica(meditacion);
        for (int i = 0; i < Combatiente.USOS_POR_TECNICA; i++) {
            c.recuperarEnergia(50);
            c.usarTecnica(meditacion, c);
        }
        c.recuperarEnergia(50);
        assertEquals(50, c.getEnergiaActual());
        assertEquals(0, c.usosRestantes(meditacion));
        assertFalse(c.puedeUsar(meditacion));
        assertThrows(TecnicaNoDisponibleException.class, () -> c.usarTecnica(meditacion, c));
        assertEquals(50, c.getEnergiaActual(), "el intento fallido no cobra energia");
    }

    @Test
    @DisplayName("No se puede usar una tecnica que el combatiente no conoce")
    void tecnicaDesconocida() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        assertThrows(TecnicaNoDisponibleException.class,
                () -> c.usarTecnica(new CorteDeLosMilAmaneceres(), new HechiceroDelVacio(2, "o", null)));
    }

    @Test
    @DisplayName("Los usos se conservan entre combates; vida y energia se recuperan al maximo")
    void usosSeConservanVidaYEnergiaSeRecuperan() {
        Tecnica corte = new CorteDeLosMilAmaneceres();
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.aprenderTecnica(corte);
        c.usarTecnica(corte, new HechiceroDelVacio(2, "o", null));
        c.perderVida(30);
        assertEquals(40, c.getEnergiaActual());
        assertEquals(70, c.getVidaActual());

        c.prepararParaCombate();

        assertEquals(100, c.getVidaActual());
        assertEquals(50, c.getEnergiaActual());
        assertEquals(4, c.usosRestantes(corte), "los usos NO se reinician entre combates");
    }

    @Test
    @DisplayName("Al comenzar cada combate se recuperan tambien los caidos")
    void caidosSeRecuperanAlIniciarCombate() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.perderVida(500);
        assertFalse(c.estaEnPie());
        c.prepararParaCombate();
        assertTrue(c.estaEnPie());
        assertEquals(100, c.getVidaActual());
    }

    @Test
    @DisplayName("El ataque basico no cuesta energia, no consume usos y no tiene limite")
    void ataqueBasico() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        Combatiente objetivo = new Utiles.CombatienteDePrueba(2, 1000, 50);
        for (int i = 0; i < 8; i++) c.atacarBasico(objetivo);
        assertEquals(50, c.getEnergiaActual());
        assertEquals(1000 - 8 * 12, objetivo.getVidaActual());
    }

    @Test
    @DisplayName("Un caido no actua: ni tecnicas ni ataque basico; y no recibe mas daño")
    void caidoNoActua() {
        Tecnica corte = new CorteDeLosMilAmaneceres();
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.aprenderTecnica(corte);
        c.perderVida(1000);
        Combatiente rival = Utiles.neutro(2);
        assertThrows(TecnicaNoDisponibleException.class, () -> c.usarTecnica(corte, rival));
        assertThrows(IllegalStateException.class, () -> c.atacarBasico(rival));
        assertEquals(0, c.recibirDanio(50));
        assertEquals(0, c.curar(50), "un caido no se cura");
    }

    @Test
    @DisplayName("ejecutarTurno informa la accion como acontecimiento (ataque basico sin tecnicas)")
    void ejecutarTurnoAtaqueBasico() {
        Combatiente a = new CultivadorDeLaEspada(1, "Aurelio", null);
        Combatiente b = new HechiceroDelVacio(2, "Nébula", null);
        Pareja propia = new Pareja(1, a, Utiles.neutro(3));
        Acontecimiento accion = a.ejecutarTurno(List.of(b), propia);

        assertEquals(TipoAcontecimiento.ACCION, accion.getTipo());
        assertEquals("Aurelio", accion.getDatoTexto("actor"));
        assertEquals("Ataque básico", accion.getDatoTexto("accion"));
        assertEquals(Boolean.FALSE, accion.getDato("esTecnica"));
        assertEquals("Nébula", accion.getDatoTexto("objetivo"));
        assertEquals(88, accion.getDatoInt("vidaObjetivo"));
        assertEquals(88, b.getVidaActual());
    }

    @Test
    @DisplayName("ejecutarTurno usa una tecnica y la registra en la pareja")
    void ejecutarTurnoConTecnica() {
        Tecnica corte = new CorteDeLosMilAmaneceres();
        Combatiente a = new CultivadorDeLaEspada(1, "Aurelio", null);
        a.aprenderTecnica(corte);
        Combatiente rival = Utiles.neutro(5);
        Pareja propia = new Pareja(1, a, Utiles.neutro(3));

        Acontecimiento accion = a.ejecutarTurno(List.of(rival), propia);

        assertEquals(Boolean.TRUE, accion.getDato("esTecnica"));
        assertEquals("Corte de los Mil Amaneceres", accion.getDatoTexto("accion"));
        assertEquals(75, rival.getVidaActual());
        assertEquals(4, a.usosRestantes(corte));
        assertFalse(propia.puedeUsarTecnica(propia.getIntegrantes().get(1), corte), "quedo reservada");
    }

    @Test
    @DisplayName("Equipar un cuarto objeto se rechaza sin cambios")
    void cuartoObjetoRechazado() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.equipar(new EspadaCelestial());
        c.equipar(new ArmaduraDorada());
        c.equipar(new AnilloEspiritual());
        assertThrows(EquipamientoInvalidoException.class, () -> c.equipar(new SelloDelFenix()));
        assertEquals(3, c.getEquipamientos().size());
        assertFalse(c.getEquipamientos().containsKey(CategoriaEquipamiento.SELLO));
        assertEquals(100, c.getVidaMaxima(), "el sello rechazado no suma vida maxima");
    }

    @Test
    @DisplayName("Equipar una categoria repetida se rechaza sin cambios")
    void categoriaRepetidaRechazada() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        Equipamiento original = new EspadaCelestial();
        c.equipar(original);
        assertThrows(EquipamientoInvalidoException.class, () -> c.equipar(new EspadaCelestial()));
        assertEquals(1, c.getEquipamientos().size());
        assertSame(original, c.getEquipamientos().get(CategoriaEquipamiento.ARMA));
    }

    @Test
    @DisplayName("Un combatiente puede llevar hasta tres equipamientos y los puede consultar")
    void hastaTresYConsultables() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.equipar(new EspadaCelestial());
        c.equipar(new ArmaduraDorada());
        c.equipar(new SelloDelFenix());
        assertEquals(3, c.getEquipamientos().size());
        assertEquals("Espada Celestial", c.getEquipamientos().get(CategoriaEquipamiento.ARMA).getNombre());
    }

    @Test
    @DisplayName("Lo consultado no permite saltarse las reglas (vista de solo lectura)")
    void equipamientosSoloLectura() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        assertThrows(UnsupportedOperationException.class,
                () -> c.getEquipamientos().put(CategoriaEquipamiento.ARMA, new EspadaCelestial()));
    }

    @Test
    @DisplayName("Los efectos del equipamiento se acumulan")
    void efectosSeAcumulan() {
        Combatiente cultivador = new CultivadorDeLaEspada(1, "Cul", null);
        cultivador.equipar(new EspadaCelestial());
        Combatiente objetivo = new Utiles.CombatienteDePrueba(5, 1000, 50);
        cultivador.atacar(objetivo, 20);
        assertEquals(1000 - 31, objetivo.getVidaActual());

        Combatiente monje = new MonjeDelCuerpoCelestial(2, "Mon", null);
        monje.equipar(new ArmaduraDorada());
        monje.equipar(new SelloDelFenix());
        monje.equipar(new AnilloEspiritual());
        assertEquals(12, monje.recibirDanio(20));
        assertEquals(130, monje.getVidaMaxima());
        assertEquals(55, monje.getEnergiaMaxima());
    }

    @Test
    @DisplayName("Equipar un objeto que sube un maximo no sube el valor actual")
    void equiparNoSubeValorActual() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.equipar(new SelloDelFenix());
        c.equipar(new AnilloEspiritual());
        assertEquals(130, c.getVidaMaxima());
        assertEquals(100, c.getVidaActual());
        assertEquals(55, c.getEnergiaMaxima());
        assertEquals(50, c.getEnergiaActual());
    }

    @Test
    @DisplayName("Al quitar un equipamiento que sube la vida maxima, la vida actual se recorta")
    void quitarSelloRecortaVida() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.equipar(new SelloDelFenix());
        c.prepararParaCombate();
        c.finalizarCombate();
        assertEquals(130, c.getVidaActual());

        c.desequipar(CategoriaEquipamiento.SELLO);

        assertEquals(100, c.getVidaMaxima());
        assertEquals(100, c.getVidaActual());
    }

    @Test
    @DisplayName("Al quitar un equipamiento que sube la energia maxima, la energia actual se recorta")
    void quitarAnilloRecortaEnergia() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.equipar(new AnilloEspiritual());
        c.prepararParaCombate();
        c.finalizarCombate();
        assertEquals(55, c.getEnergiaActual());
        c.desequipar(CategoriaEquipamiento.ANILLO);
        assertEquals(50, c.getEnergiaActual());
    }

    @Test
    @DisplayName("Quitar un equipamiento que no se lleva se rechaza")
    void quitarInexistente() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        assertThrows(EquipamientoInvalidoException.class, () -> c.desequipar(CategoriaEquipamiento.ARMA));
    }

    @Test
    @DisplayName("No se puede cambiar el equipamiento durante el combate, pero si entre combates")
    void equipoSoloFueraDeCombate() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.equipar(new EspadaCelestial());
        c.prepararParaCombate();
        assertThrows(EquipamientoInvalidoException.class, () -> c.equipar(new ArmaduraDorada()));
        assertThrows(EquipamientoInvalidoException.class, () -> c.desequipar(CategoriaEquipamiento.ARMA));
        c.finalizarCombate();
        c.equipar(new ArmaduraDorada());
        assertEquals(2, c.getEquipamientos().size());
    }

    @Test
    @DisplayName("La armadura nunca convierte un golpe en curacion (daño recibido minimo 1)")
    void armaduraNoCura() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        c.equipar(new ArmaduraDorada());
        assertEquals(1, c.recibirDanio(3));
        assertEquals(99, c.getVidaActual());
    }

    @Test
    @DisplayName("La reduccion de daño activa (Muro) rige hasta el fin del combate")
    void defensaActiva() {
        Combatiente c = new HechiceroDelVacio(1, "Hec", null);
        c.activarReduccionDanio(0.30);
        assertTrue(c.tieneDefensaActiva());
        assertEquals(14, c.recibirDanio(20));
        c.prepararParaCombate();
        assertFalse(c.tieneDefensaActiva(), "termina con el combate");
    }
}
