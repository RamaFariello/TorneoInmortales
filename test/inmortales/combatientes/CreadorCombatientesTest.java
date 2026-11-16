package inmortales.combatientes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import inmortales.escuelas.Escuela;

class CreadorCombatientesTest {

    // Los acentos se escriben como escapes unicode para que no dependan de la codificacion del IDE.
    private static final String HECHICERO = "Hechicero del Vac\u00edo";

    private final Escuela escuela = new Escuela("Escuela de prueba", List.of(), List.of());

    @Test
    void creaCadaTipoSinConocerSuClaseConcreta() {
        CreadorCombatientes creador = new CreadorCombatientes();
        assertTrue(creador.crear("Cultivador de la Espada", "A", escuela) instanceof CultivadorDeLaEspada);
        assertTrue(creador.crear("Monje del Cuerpo Celestial", "B", escuela) instanceof MonjeDelCuerpoCelestial);
        assertTrue(creador.crear(HECHICERO, "C", escuela) instanceof HechiceroDelVacio);
    }

    @Test
    void elTipoSeReconoceConOSinTildesYSinImportarMayusculas() {
        CreadorCombatientes creador = new CreadorCombatientes();
        assertTrue(creador.crear(HECHICERO, "A", escuela) instanceof HechiceroDelVacio);
        assertTrue(creador.crear("hechicero del vacio", "B", escuela) instanceof HechiceroDelVacio);
        assertTrue(creador.crear("  HECHICERO DEL VAC\u00cdO ", "C", escuela) instanceof HechiceroDelVacio);
    }

    @Test
    void losIdentificadoresSeAsignanEnOrdenDeCreacion() {
        CreadorCombatientes creador = new CreadorCombatientes();
        assertEquals(1, creador.crear("Cultivador de la Espada", "A", escuela).getId());
        assertEquals(2, creador.crear("Monje del Cuerpo Celestial", "B", escuela).getId());
        assertEquals(3, creador.crear(HECHICERO, "C", escuela).getId());
    }

    @Test
    void unTipoDesconocidoSeRechaza() {
        CreadorCombatientes creador = new CreadorCombatientes();
        assertThrows(IllegalArgumentException.class, () -> creador.crear("Drag\u00f3n", "X", escuela));
    }
}
