package inmortales.escuelas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inmortales.combatientes.Combatiente;
import inmortales.combatientes.CreadorCombatientes;
import inmortales.combatientes.CultivadorDeLaEspada;
import inmortales.combatientes.HechiceroDelVacio;
import inmortales.combatientes.MonjeDelCuerpoCelestial;
import inmortales.equipamiento.*;
import inmortales.tecnicas.*;

class EscuelaTest {
    private Escuela palacio() {
        return new Escuela("Palacio del Vacío",
                Arrays.asList(new DominioDeLaNada(), new MeditacionDelInmortal()),
                Arrays.asList(new AnilloEspiritual()));
    }

    @Test
    @DisplayName("La escuela prepara al combatiente: tecnicas (en orden) y equipamiento iniciales")
    void preparaCombatiente() {
        Combatiente c = new HechiceroDelVacio(1, "Nébula", null);
        palacio().preparar(c);
        assertEquals(Arrays.asList(new DominioDeLaNada(), new MeditacionDelInmortal()), c.getTecnicas());
        assertTrue(c.getEquipamientos().containsKey(CategoriaEquipamiento.ANILLO));
    }

    @Test
    @DisplayName("La escuela no determina el tipo: prepara combatientes de cualquier tipo")
    void noDeterminaTipo() {
        Escuela e = palacio();
        Combatiente[] todos = { new CultivadorDeLaEspada(1, "a", e), new MonjeDelCuerpoCelestial(2, "b", e),
                new HechiceroDelVacio(3, "c", e) };
        for (Combatiente c : todos) {
            e.preparar(c);
            assertEquals(2, c.getTecnicas().size());
        }
    }

    @Test
    @DisplayName("Una escuela no puede asignar mas de tres equipamientos")
    void maximoTresEquipamientos() {
        assertThrows(IllegalArgumentException.class, () -> new Escuela("X", Collections.<Tecnica>emptyList(),
                Arrays.asList(new EspadaCelestial(), new ArmaduraDorada(), new AnilloEspiritual(), new SelloDelFenix())));
    }

    @Test
    @DisplayName("Una escuela no puede asignar dos equipamientos de la misma categoria")
    void categoriaRepetida() {
        assertThrows(IllegalArgumentException.class, () -> new Escuela("X", Collections.<Tecnica>emptyList(),
                Arrays.asList(new EspadaCelestial(), new EspadaCelestial())));
    }

    @Test
    @DisplayName("La configuracion de la escuela no se puede alterar desde afuera")
    void configuracionInmutable() {
        Escuela e = palacio();
        assertThrows(UnsupportedOperationException.class, () -> e.getTecnicasIniciales().add(new CorteDeLosMilAmaneceres()));
    }

    @Test
    @DisplayName("El creador centraliza: se pide por nombre de tipo y devuelve el combatiente preparado")
    void creadorCreaPreparados() {
        CreadorCombatientes creador = new CreadorCombatientes();
        Combatiente h = creador.crear("Hechicero del Vacío", "Nébula", palacio());
        assertTrue(h instanceof HechiceroDelVacio);
        assertEquals("Nébula", h.getNombre());
        assertEquals(2, h.getTecnicas().size());
        assertEquals(1, h.getEquipamientos().size());
        assertEquals("Palacio del Vacío", h.getEscuela().getNombre());
    }

    @Test
    @DisplayName("Los identificadores de los combatientes los asigna el creador en orden de creacion")
    void idsEnOrdenDeCreacion() {
        CreadorCombatientes creador = new CreadorCombatientes();
        Escuela e = palacio();
        assertEquals(1, creador.crear("Cultivador de la Espada", "a", e).getId());
        assertEquals(2, creador.crear("Monje del Cuerpo Celestial", "b", e).getId());
        assertEquals(3, creador.crear("Hechicero del Vacío", "c", e).getId());
    }

    @Test
    @DisplayName("El tipo se reconoce con o sin tilde (el main de la consigna usa 'Vacío')")
    void tipoConYSinTilde() {
        CreadorCombatientes creador = new CreadorCombatientes();
        assertTrue(creador.crear("Hechicero del Vacio", "a", palacio()) instanceof HechiceroDelVacio);
        assertTrue(creador.crear("hechicero del vacío", "b", palacio()) instanceof HechiceroDelVacio);
    }

    @Test
    @DisplayName("Un tipo desconocido o una escuela nula se rechazan")
    void rechazos() {
        CreadorCombatientes creador = new CreadorCombatientes();
        assertThrows(IllegalArgumentException.class, () -> creador.crear("Bardo", "x", palacio()));
        assertThrows(IllegalArgumentException.class, () -> creador.crear("Hechicero del Vacío", "x", null));
    }

    @Test
    @DisplayName("Se puede registrar un tipo nuevo sin modificar el creador")
    void registrarTipoNuevo() {
        CreadorCombatientes creador = new CreadorCombatientes();
        creador.registrarTipo("Berserker", (id, nombre, escuela) -> new CultivadorDeLaEspada(id, nombre, escuela));
        assertEquals(1, creador.crear("Berserker", "Ragnar", palacio()).getId());
        assertThrows(IllegalArgumentException.class, () -> creador.registrarTipo("berserker",
                (id, nombre, escuela) -> new CultivadorDeLaEspada(id, nombre, escuela)));
    }
}
