package inmortales.excepciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ExcepcionesTest {

    private final List<TorneoInmortalesException> todas = List.of(
            new InscripcionInvalidaException("a"),
            new CantidadDeParejasInvalidaException("b"),
            new EquipamientoInvalidoException("c"),
            new TecnicaNoDisponibleException("d"));

    @Test
    void todasLasExcepcionesDelDominioHeredanDeLaBase() {
        for (TorneoInmortalesException e : todas) {
            assertTrue(e instanceof RuntimeException);
        }
    }

    @Test
    void conservanSuMensaje() {
        assertEquals("a", todas.get(0).getMessage());
        assertEquals("b", todas.get(1).getMessage());
        assertEquals("c", todas.get(2).getMessage());
        assertEquals("d", todas.get(3).getMessage());
    }

    @Test
    void sePuedenAtraparTodasComoExcepcionDelTorneo() {
        for (TorneoInmortalesException e : todas) {
            assertThrows(TorneoInmortalesException.class, () -> {
                throw e;
            });
        }
    }

    @Test
    void cadaExcepcionSeDistingueDeLasDemas() {
        assertThrows(InscripcionInvalidaException.class, () -> {
            throw todas.get(0);
        });
        assertThrows(CantidadDeParejasInvalidaException.class, () -> {
            throw todas.get(1);
        });
        assertThrows(EquipamientoInvalidoException.class, () -> {
            throw todas.get(2);
        });
        assertThrows(TecnicaNoDisponibleException.class, () -> {
            throw todas.get(3);
        });
    }
}
