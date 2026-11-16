package inmortales.acontecimientos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AcontecimientoTest {
    @Test
    @DisplayName("Cada acontecimiento guarda los datos del momento en que ocurrio")
    void guardaDatos() {
        Acontecimiento r = Acontecimiento.resultadoCombate(2, 7, 3, "P3", 120, 1, "P1", 0);
        assertEquals(TipoAcontecimiento.RESULTADO_COMBATE, r.getTipo());
        assertEquals(2, r.getRonda());
        assertEquals(3, r.getDatoInt("idGanadora"));
        assertEquals(120, r.getDatoInt("vidaGanadora"));
        assertEquals("P1", r.getDatoTexto("perdedora"));
        assertEquals(0, r.getDatoInt("vidaPerdedora"));
    }

    @Test
    @DisplayName("Son inmutables: los datos extra no se pueden modificar")
    void inmutables() {
        Acontecimiento a = Acontecimiento.proclamacion(3, 2, "Kael", "Darian");
        assertThrows(UnsupportedOperationException.class, () -> a.getDatosExtra().put("x", 1));
    }

    @Test
    @DisplayName("ubicarEn devuelve una copia con ronda y asalto, sin tocar el original")
    void ubicarEn() {
    	Acontecimiento a = Acontecimiento.accion(1, "A", "Ataque básico", false, 2, "B", "causa 10 de daño", 90);
    	Acontecimiento ubicado = a.ubicarEn(2, 5);
        assertEquals(0, a.getRonda());
        assertEquals(2, ubicado.getRonda());
        assertEquals(5, ubicado.getAsalto());
        assertNotEquals(a, ubicado);
    }

    @Test
    @DisplayName("Dos acontecimientos con los mismos datos son iguales")
    void igualdad() {
        assertEquals(Acontecimiento.eliminacion(1, 2, 3, "X", 1, 4), Acontecimiento.eliminacion(1, 2, 3, "X", 1, 4));
        assertEquals(Acontecimiento.eliminacion(1, 2, 3, "X", 1, 4).hashCode(),
                Acontecimiento.eliminacion(1, 2, 3, "X", 1, 4).hashCode());
        assertNotEquals(Acontecimiento.eliminacion(1, 2, 3, "X", 1, 4), Acontecimiento.eliminacion(1, 2, 5, "X", 1, 4));
    }

    @Test
    @DisplayName("Existen los cinco tipos pedidos")
    void tipos() {
        assertEquals(5, TipoAcontecimiento.values().length);
        assertEquals(TipoAcontecimiento.INICIO_COMBATE,
                Acontecimiento.inicioCombate(1, 1, "P1", 2, "P2").getTipo());
    }
}
