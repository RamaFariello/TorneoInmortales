package inmortales.torneo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inmortales.Utiles;
import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.ObservadorTorneo;
import inmortales.acontecimientos.TipoAcontecimiento;
import inmortales.combatientes.Combatiente;
import inmortales.excepciones.CantidadDeParejasInvalidaException;
import inmortales.excepciones.InscripcionInvalidaException;
import inmortales.presentacion.CronistaDeLaSecta;
import inmortales.presentacion.RegistroPosicion;

class TorneoTest {
    private Torneo torneoCon(int cantidad, CronistaDeLaSecta cronista) {
        Torneo torneo = new Torneo();
        if (cronista != null) torneo.agregarObservador(cronista);
        for (Pareja p : Utiles.parejas(cantidad)) torneo.inscribir(p);
        return torneo;
    }

    @Test
    @DisplayName("Una pareja no puede registrarse dos veces")
    void parejaDuplicada() {
        Torneo torneo = new Torneo();
        List<Pareja> parejas = Utiles.parejas(2);
        torneo.inscribir(parejas.get(0));
        assertThrows(InscripcionInvalidaException.class, () -> torneo.inscribir(parejas.get(0)));
        Pareja mismoId = new Pareja(parejas.get(0).getId(), parejas.get(1).getIntegrantes().get(0),
                parejas.get(1).getIntegrantes().get(1));
        assertThrows(InscripcionInvalidaException.class, () -> torneo.inscribir(mismoId));
        assertEquals(1, torneo.getParejasInscritas().size());
    }

    @Test
    @DisplayName("Un combatiente no puede inscribirse dos veces (ni en otra pareja)")
    void combatienteDuplicado() {
        Torneo torneo = new Torneo();
        List<Pareja> parejas = Utiles.parejas(2);
        torneo.inscribir(parejas.get(0));
        Combatiente repetido = parejas.get(0).getIntegrantes().get(0);
        Pareja conRepetido = new Pareja(99, repetido, parejas.get(1).getIntegrantes().get(0));
        assertThrows(InscripcionInvalidaException.class, () -> torneo.inscribir(conRepetido));
        assertEquals(1, torneo.getParejasInscritas().size(), "el rechazo no deja inscripciones a medias");
        torneo.inscribir(parejas.get(1));
        assertEquals(2, torneo.getParejasInscritas().size());
    }

    @Test
    @DisplayName("Cantidad de parejas invalida: solo se juega con 4 u 8")
    void cantidadInvalida() {
        for (int n : new int[] { 0, 1, 2, 3, 5, 6, 7 }) {
            Torneo torneo = torneoCon(n, null);
            assertThrows(CantidadDeParejasInvalidaException.class, torneo::iniciar, n + " parejas");
            assertFalse(torneo.haTerminado());
        }
        assertDoesNotThrow(() -> torneoCon(4, null).iniciar());
        assertDoesNotThrow(() -> torneoCon(8, null).iniciar());
    }

    @Test
    @DisplayName("No se pueden inscribir mas de 8 parejas")
    void masDeOcho() {
        Torneo torneo = new Torneo();
        List<Pareja> parejas = Utiles.parejas(9);
        for (int i = 0; i < 8; i++) torneo.inscribir(parejas.get(i));
        assertThrows(CantidadDeParejasInvalidaException.class, () -> torneo.inscribir(parejas.get(8)));
    }

    @Test
    @DisplayName("Una vez iniciado no se admiten inscripciones ni se juega de nuevo")
    void despuesDeIniciar() {
        Torneo torneo = torneoCon(4, null);
        torneo.iniciar();
        assertThrows(InscripcionInvalidaException.class, () -> torneo.inscribir(Utiles.parejas(1).get(0)));
        assertThrows(IllegalStateException.class, torneo::iniciar);
    }

    @Test
    @DisplayName("Primera ronda: cruces por orden de inscripcion (1 vs 2, 3 vs 4, ...)")
    void crucesPrimeraRonda() {
        Torneo torneo = torneoCon(8, null);
        List<Pareja> inscriptas = new ArrayList<>(torneo.getParejasInscritas());
        torneo.iniciar();

        List<Cruce> primera = torneo.getRondas().get(0);
        assertEquals(4, primera.size());
        for (int i = 0; i < 4; i++) {
            assertSame(inscriptas.get(2 * i), primera.get(i).getPrimera());
            assertSame(inscriptas.get(2 * i + 1), primera.get(i).getSegunda());
        }
    }

    @Test
    @DisplayName("Rondas siguientes: cruces por el orden de los cruces de la ronda anterior")
    void crucesRondasSiguientes() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        Torneo torneo = torneoCon(8, cronista);
        torneo.iniciar();

        List<List<Cruce>> rondas = torneo.getRondas();
        assertEquals(3, rondas.size());
        assertEquals(4, rondas.get(0).size());
        assertEquals(2, rondas.get(1).size());
        assertEquals(1, rondas.get(2).size());

        for (int r = 1; r < rondas.size(); r++) {
            List<Integer> ganadorasAnteriores = ganadorasDeRonda(cronista, r);
            List<Integer> participantes = new ArrayList<>();
            for (Cruce c : rondas.get(r)) {
                participantes.add(c.getPrimera().getId());
                participantes.add(c.getSegunda().getId());
            }
            assertEquals(ganadorasAnteriores, participantes, "ronda " + (r + 1));
        }
    }

    private List<Integer> ganadorasDeRonda(CronistaDeLaSecta cronista, int ronda) {
        List<Integer> ids = new ArrayList<>();
        for (Acontecimiento r : cronista.getResultados()) {
            if (r.getRonda() == ronda) ids.add(r.getDatoInt("idGanadora"));
        }
        return ids;
    }

    @Test
    @DisplayName("El torneo termina cuando queda una sola pareja: cada otra perdio exactamente una vez")
    void terminaConUnaSolaPareja() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        Torneo torneo = torneoCon(8, cronista);
        Pareja devuelta = torneo.iniciar();

        assertTrue(torneo.haTerminado());
        assertSame(devuelta, torneo.getCampeona());
        assertEquals(7, cronista.getResultados().size(), "en eliminacion directa de 8 se juegan 7 combates");
        List<Integer> perdedoras = new ArrayList<>();
        for (Acontecimiento r : cronista.getResultados()) perdedoras.add(r.getDatoInt("idPerdedora"));
        assertEquals(7, new java.util.HashSet<>(perdedoras).size());
        assertFalse(perdedoras.contains(torneo.getCampeona().getId()));
    }

    @Test
    @DisplayName("Con 4 parejas se juegan 2 rondas y 3 combates")
    void cuatroParejas() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        Torneo torneo = torneoCon(4, cronista);
        torneo.iniciar();
        assertEquals(2, torneo.getRondas().size());
        assertEquals(3, cronista.getResultados().size());
    }

    @Test
    @DisplayName("Se proclaman los Dos Santos Supremos: los integrantes de la pareja campeona")
    void proclamacion() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        Torneo torneo = torneoCon(4, cronista);
        torneo.iniciar();

        List<Acontecimiento> historial = cronista.getHistorial();
        Acontecimiento p = historial.get(historial.size() - 1);
        assertEquals(TipoAcontecimiento.PROCLAMACION, p.getTipo());
        Pareja campeona = torneo.getCampeona();
        assertEquals(campeona.getId(), p.getDatoInt("idPareja"));
        assertEquals(campeona.getIntegrantes().get(0).getNombre(), p.getDatoTexto("santo1"));
        assertEquals(campeona.getIntegrantes().get(1).getNombre(), p.getDatoTexto("santo2"));
        Acontecimiento ultimo = cronista.getResultados().get(cronista.getResultados().size() - 1);
        assertEquals(ultimo.getDatoInt("idGanadora"), campeona.getId());
    }

    @Test
    @DisplayName("Dos torneos armados igual producen el mismo campeon y el mismo historial")
    void reproducible() {
        for (int cantidad : new int[] { 4, 8 }) {
            CronistaDeLaSecta c1 = new CronistaDeLaSecta();
            CronistaDeLaSecta c2 = new CronistaDeLaSecta();
            Torneo t1 = torneoCon(cantidad, c1);
            Torneo t2 = torneoCon(cantidad, c2);
            t1.iniciar();
            t2.iniciar();

            assertEquals(t1.getCampeona().getId(), t2.getCampeona().getId());
            assertFalse(c1.getHistorial().isEmpty());
            assertEquals(c1.getHistorial(), c2.getHistorial());
            assertEquals(c1.generarTablaPosiciones(), c2.generarTablaPosiciones());
        }
    }

    @Test
    @DisplayName("Se pueden sumar nuevos interesados sin modificar el torneo")
    void nuevosObservadores() {
        final List<Acontecimiento> recibidos = new ArrayList<>();
        Torneo torneo = new Torneo();
        torneo.agregarObservador(new ObservadorTorneo() {
            public void actualizar(Acontecimiento a) { recibidos.add(a); }
        });
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        torneo.agregarObservador(cronista);
        for (Pareja p : Utiles.parejas(4)) torneo.inscribir(p);
        torneo.iniciar();
        assertEquals(cronista.getHistorial(), recibidos);
    }

    @Test
    @DisplayName("El torneo tambien puede notificar acontecimientos propios")
    void notificarDirecto() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        Torneo torneo = new Torneo();
        torneo.agregarObservador(cronista);
        Acontecimiento a = Acontecimiento.proclamacion(1, 1, "x", "y");
        torneo.notificar(a);
        assertEquals(java.util.Collections.singletonList(a), cronista.getHistorial());
    }

    @Test
    @DisplayName("La tabla de posiciones del torneo queda ordenada por ultima ronda y vida total final")
    void tablaDelTorneoOrdenada() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        Torneo torneo = torneoCon(8, cronista);
        torneo.iniciar();

        List<RegistroPosicion> tabla = cronista.generarTablaPosiciones();
        assertEquals(8, tabla.size());
        assertEquals(torneo.getCampeona().getId(), tabla.get(0).getIdPareja());
        assertEquals(3, tabla.get(0).getUltimaRonda());
        for (int i = 1; i < tabla.size(); i++) {
            RegistroPosicion antes = tabla.get(i - 1);
            RegistroPosicion despues = tabla.get(i);
            boolean ordenado = antes.getUltimaRonda() > despues.getUltimaRonda()
                    || (antes.getUltimaRonda() == despues.getUltimaRonda()
                        && antes.getVidaTotalFinal() > despues.getVidaTotalFinal())
                    || (antes.getUltimaRonda() == despues.getUltimaRonda()
                        && antes.getVidaTotalFinal() == despues.getVidaTotalFinal()
                        && antes.getIdPareja() < despues.getIdPareja());
            assertTrue(ordenado, "posiciones " + i + " y " + (i + 1));
        }
    }
}
