package inmortales.tecnicas;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inmortales.combatientes.Combatiente;
import inmortales.combatientes.CultivadorDeLaEspada;
import inmortales.combatientes.HechiceroDelVacio;

class TecnicaTest {
    @Test
    @DisplayName("Dos tecnicas con el mismo nombre son la misma (aunque sean instancias distintas)")
    void igualdadPorNombre() {
        assertEquals(new CorteDeLosMilAmaneceres(), new CorteDeLosMilAmaneceres());
        assertNotEquals(new CorteDeLosMilAmaneceres(), new RugidoDeLaBestiaPrimordial());
    }

    @Test
    @DisplayName("Una tecnica es solo definicion: nombre, costo y tipo")
    void definicion() {
        Tecnica t = new CorteDeLosMilAmaneceres();
        assertEquals("Corte de los Mil Amaneceres", t.getNombre());
        assertEquals(10, t.getCostoEnergia());
        assertEquals(TipoTecnica.OFENSIVA, t.getTipo());
    }

    @Test
    @DisplayName("Costo negativo o nombre vacio se rechazan")
    void validaciones() {
        assertThrows(IllegalArgumentException.class, () -> new Tecnica("X", -1, TipoTecnica.OFENSIVA) {
            @Override protected void aplicarEfecto(Combatiente e, Combatiente o) { }
            @Override public String describirEfecto(int v) { return ""; }
        });
        assertThrows(IllegalArgumentException.class, () -> new Tecnica(" ", 1, TipoTecnica.OFENSIVA) {
            @Override protected void aplicarEfecto(Combatiente e, Combatiente o) { }
            @Override public String describirEfecto(int v) { return ""; }
        });
    }

    @Test
    @DisplayName("Dominio de la Nada daña y le quita energia al adversario")
    void dominioDeLaNada() {
        Combatiente ejecutor = new HechiceroDelVacio(1, "Hec", null);
        Combatiente objetivo = new HechiceroDelVacio(2, "Obj", null);
        Tecnica dominio = new DominioDeLaNada();
        ejecutor.aprenderTecnica(dominio);
        ejecutor.usarTecnica(dominio, objetivo);
        assertEquals(85, objetivo.getVidaActual());
        assertEquals(40, objetivo.getEnergiaActual());
    }

    @Test
    @DisplayName("Rugido de la Bestia Primordial es el ataque mas poderoso")
    void rugidoEsElMasPoderoso() {
        assertTrue(RugidoDeLaBestiaPrimordial.DANIO_BASE > CorteDeLosMilAmaneceres.DANIO_BASE);
        assertTrue(RugidoDeLaBestiaPrimordial.DANIO_BASE > DominioDeLaNada.DANIO_BASE);
    }

    @Test
    @DisplayName("Meditacion del Inmortal recupera vida del ejecutor sin superar el maximo")
    void meditacion() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        Tecnica meditacion = new MeditacionDelInmortal();
        c.aprenderTecnica(meditacion);
        assertEquals(TipoTecnica.RECUPERACION, meditacion.getTipo());
        c.perderVida(40);
        c.usarTecnica(meditacion, c);
        assertEquals(85, c.getVidaActual());
        c.recuperarEnergia(50);
        c.usarTecnica(meditacion, c);
        assertEquals(100, c.getVidaActual(), "solo recupera lo que falta");
    }

    @Test
    @DisplayName("Muro del Cuerpo Dorado reduce el daño que recibe el ejecutor")
    void muro() {
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        Tecnica muro = new MuroDelCuerpoDorado();
        c.aprenderTecnica(muro);
        assertEquals(TipoTecnica.DEFENSIVA, muro.getTipo());
        assertFalse(c.tieneDefensaActiva());
        c.usarTecnica(muro, c);
        assertTrue(c.tieneDefensaActiva());
        assertEquals(14, c.recibirDanio(20));
    }

    @Test
    @DisplayName("Agregar una tecnica nueva no modifica las existentes")
    void tecnicaNuevaSinModificarExistentes() {
        Tecnica llamarada = new Tecnica("Llamarada", 8, TipoTecnica.OFENSIVA) {
            @Override protected void aplicarEfecto(Combatiente ejecutor, Combatiente objetivo) {
                ejecutor.atacar(objetivo, 12);
            }
            @Override public String describirEfecto(int v) { return "quema " + v; }
        };
        Combatiente c = new CultivadorDeLaEspada(1, "Cul", null);
        Combatiente objetivo = new HechiceroDelVacio(2, "o", null);
        c.aprenderTecnica(llamarada);
        c.usarTecnica(llamarada, objetivo);
        assertEquals(100 - 15, objetivo.getVidaActual());
    }

    @Test
    @DisplayName("Cada tecnica describe su efecto")
    void descripciones() {
        assertEquals("causa 25 de daño", new CorteDeLosMilAmaneceres().describirEfecto(25));
        assertEquals("recupera 25 de vida", new MeditacionDelInmortal().describirEfecto(-25));
        assertTrue(new DominioDeLaNada().describirEfecto(15).contains("energía"));
        assertTrue(new MuroDelCuerpoDorado().describirEfecto(0).contains("30%"));
    }
}
