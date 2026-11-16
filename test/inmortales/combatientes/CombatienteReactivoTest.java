package inmortales.combatientes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inmortales.Utiles;
import inmortales.acontecimientos.Acontecimiento;

class CombatienteReactivoTest {
    private CombatienteReactivo reactivo;
    private Combatiente rival;

    @BeforeEach
    void preparar() {
        reactivo = new CombatienteReactivo(1, "Furia", null);
        rival = new Utiles.CombatienteDePrueba(9, 1000, 50);
    }

    private Acontecimiento caeCompanero(int idCompaneroDelCaido) {
        return Acontecimiento.eliminacion(1, 3, 2, "Compañero", 1, idCompaneroDelCaido).ubicarEn(1, 3);
    }

    @Test
    @DisplayName("Sin acontecimientos previos ataca normal")
    void ataqueNormal() {
        assertFalse(reactivo.tieneAtaquePotenciado());
        reactivo.atacarBasico(rival);
        assertEquals(1000 - 10, rival.getVidaActual());
    }

    @Test
    @DisplayName("Se activa cuando cae su companero: su proximo ataque hace +50%")
    void sePotenciaCuandoCaeElCompanero() {
        reactivo.actualizar(caeCompanero(1));
        assertTrue(reactivo.tieneAtaquePotenciado());
        reactivo.atacarBasico(rival);
        assertEquals(1000 - 15, rival.getVidaActual());
    }

    @Test
    @DisplayName("Termina al usarse: solo potencia UN ataque")
    void seConsumeConElAtaque() {
        reactivo.actualizar(caeCompanero(1));
        reactivo.atacarBasico(rival);
        assertFalse(reactivo.tieneAtaquePotenciado());
        reactivo.atacarBasico(rival);
        assertEquals(1000 - 15 - 10, rival.getVidaActual());
    }

    @Test
    @DisplayName("No reacciona ante la eliminacion de otros combatientes")
    void ignoraOtrasEliminaciones() {
        reactivo.actualizar(caeCompanero(7));
        assertFalse(reactivo.tieneAtaquePotenciado());
    }

    @Test
    @DisplayName("No reacciona ante otros tipos de acontecimiento")
    void ignoraOtrosTipos() {
        reactivo.actualizar(Acontecimiento.proclamacion(3, 1, "a", "b"));
        assertFalse(reactivo.tieneAtaquePotenciado());
    }

    @Test
    @DisplayName("La potencia termina con el combate")
    void terminaConElCombate() {
        reactivo.actualizar(caeCompanero(1));
        reactivo.prepararParaCombate();
        assertFalse(reactivo.tieneAtaquePotenciado());
    }

    @Test
    @DisplayName("Un reactivo caido no se activa")
    void caidoNoSeActiva() {
        reactivo.perderVida(1000);
        reactivo.actualizar(caeCompanero(1));
        assertFalse(reactivo.tieneAtaquePotenciado());
    }

    @Test
    @DisplayName("La accion del turno indica que el ataque estaba potenciado")
    void marcaEnElAcontecimiento() {
        reactivo.actualizar(caeCompanero(1));
        Acontecimiento accion = reactivo.ejecutarTurno(java.util.List.of(rival),
                new inmortales.torneo.Pareja(1, reactivo, Utiles.neutro(2)));
        assertTrue(accion.getDatoTexto("efecto").contains("potenciado"));
    }

    @Test
    @DisplayName("El creador sabe crear combatientes reactivos")
    void creador() {
        inmortales.escuelas.Escuela escuela = new inmortales.escuelas.Escuela("E", java.util.List.of(), java.util.List.of());
        Combatiente c = new CreadorCombatientes().crear("Combatiente Reactivo", "Furia", escuela);
        assertTrue(c instanceof CombatienteReactivo);
    }
}
