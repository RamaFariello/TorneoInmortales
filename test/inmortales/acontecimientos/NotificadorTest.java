package inmortales.acontecimientos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class NotificadorTest {

    /** Interesado de prueba: anota cada acontecimiento que recibe, con su nombre. */
    private static class Anotador implements Interesado {
        private final String nombre;
        private final List<String> bitacora;

        Anotador(String nombre, List<String> bitacora) {
            this.nombre = nombre;
            this.bitacora = bitacora;
        }

        @Override
        public void notificar(Acontecimiento acontecimiento) {
            bitacora.add(nombre + ":" + acontecimiento.getClass().getSimpleName());
        }
    }

    private final Acontecimiento inicio = new InicioCombate(1, 1, "P1", 2, "P2");

    @Test
    void todosLosInteresadosRecibenElAcontecimientoEnOrdenDeSuscripcion() {
        List<String> bitacora = new ArrayList<>();
        Notificador notificador = new Notificador();
        notificador.suscribir(new Anotador("heraldo", bitacora));
        notificador.suscribir(new Anotador("cronista", bitacora));

        notificador.publicar(inicio);

        assertEquals(List.of("heraldo:InicioCombate", "cronista:InicioCombate"), bitacora);
    }

    @Test
    void unInteresadoSuscriptoDosVecesRecibeUnaSolaCopia() {
        List<String> bitacora = new ArrayList<>();
        Notificador notificador = new Notificador();
        Interesado uno = new Anotador("uno", bitacora);
        notificador.suscribir(uno);
        notificador.suscribir(uno);

        notificador.publicar(inicio);

        assertEquals(1, bitacora.size());
    }

    @Test
    void unInteresadoDesuscriptoDejaDeRecibir() {
        List<String> bitacora = new ArrayList<>();
        Notificador notificador = new Notificador();
        Interesado uno = new Anotador("uno", bitacora);
        notificador.suscribir(uno);
        notificador.desuscribir(uno);

        notificador.publicar(inicio);

        assertEquals(0, bitacora.size());
    }

    @Test
    void sePuedeSumarUnNuevoInteresadoSinTocarAlNotificador() {
        List<String> bitacora = new ArrayList<>();
        Notificador notificador = new Notificador();
        notificador.suscribir(a -> bitacora.add("lambda"));

        notificador.publicar(inicio);

        assertEquals(List.of("lambda"), bitacora);
    }

    @Test
    void noSeAceptaUnInteresadoNulo() {
        assertThrows(NullPointerException.class, () -> new Notificador().suscribir(null));
    }
}
