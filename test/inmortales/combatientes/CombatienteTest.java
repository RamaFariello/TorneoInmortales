package inmortales.combatientes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import inmortales.equipamiento.AnilloEspiritual;
import inmortales.equipamiento.ArmaduraDorada;
import inmortales.equipamiento.Categoria;
import inmortales.equipamiento.EspadaCelestial;
import inmortales.equipamiento.SelloDelFenix;
import inmortales.excepciones.EquipamientoInvalidoException;

class CombatienteTest {

    private Combatiente nuevo() { return new CultivadorDeLaEspada(1, "Aurelio"); }

    @Test
    void equiparUnCuartoObjetoSeRechazaSinCambios() {
        Combatiente c = nuevo();
        c.equipar(new EspadaCelestial());
        c.equipar(new ArmaduraDorada());
        c.equipar(new AnilloEspiritual());

        assertThrows(EquipamientoInvalidoException.class, () -> c.equipar(new SelloDelFenix()));
        assertEquals(3, c.getEquipamientos().size());
        assertEquals(false, c.getEquipamientos().containsKey(Categoria.SELLO));
    }

    @Test
    void equiparUnaCategoriaRepetidaSeRechazaSinCambios() {
        Combatiente c = nuevo();
        c.equipar(new EspadaCelestial());

        assertThrows(EquipamientoInvalidoException.class, () -> c.equipar(new EspadaCelestial()));
        assertEquals(1, c.getEquipamientos().size());
    }

    @Test
    void unNombreNuloOVacioSeRechazaConIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new CultivadorDeLaEspada(1, null));
        assertThrows(IllegalArgumentException.class, () -> new CultivadorDeLaEspada(1, "   "));
    }
}
