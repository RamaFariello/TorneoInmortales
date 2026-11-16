package inmortales.equipamiento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EquipamientoTest {

    @Test
    void cadaObjetoPerteneceASuCategoria() {
        assertEquals(Categoria.ARMA, new EspadaCelestial().getCategoria());
        assertEquals(Categoria.ARMADURA, new ArmaduraDorada().getCategoria());
        assertEquals(Categoria.ANILLO, new AnilloEspiritual().getCategoria());
        assertEquals(Categoria.SELLO, new SelloDelFenix().getCategoria());
    }

    @Test
    void cadaObjetoConoceSuNombre() {
        assertEquals("Espada Celestial", new EspadaCelestial().getNombre());
        assertEquals("Armadura Dorada", new ArmaduraDorada().getNombre());
    }

    @Test
    void laEspadaAumentaElDanioProvocado() {
        assertTrue(new EspadaCelestial().modificarDanioProvocado(10) > 10);
    }

    @Test
    void laArmaduraReduceElDanioRecibido() {
        assertTrue(new ArmaduraDorada().modificarDanioRecibido(10) < 10);
    }

    @Test
    void elAnilloSubeLaEnergiaMaximaYElSelloLaVidaMaxima() {
        assertTrue(new AnilloEspiritual().getBonusEnergiaMaxima() > 0);
        assertEquals(0, new AnilloEspiritual().getBonusVidaMaxima());
        assertTrue(new SelloDelFenix().getBonusVidaMaxima() > 0);
        assertEquals(0, new SelloDelFenix().getBonusEnergiaMaxima());
    }

    @Test
    void unObjetoNoModificaLoQueNoLeCorresponde() {
        Equipamiento espada = new EspadaCelestial();
        assertEquals(10, espada.modificarDanioRecibido(10));
        assertEquals(0, espada.getBonusVidaMaxima());
        assertEquals(0, espada.getBonusEnergiaMaxima());
    }
}
