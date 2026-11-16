package inmortales.torneo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.ObservadorTorneo;
import inmortales.combatientes.Combatiente;
import inmortales.excepciones.CantidadDeParejasInvalidaException;
import inmortales.excepciones.InscripcionInvalidaException;

public class Torneo {
    private static final int MAXIMO_PAREJAS = 8;

    private final List<Pareja> parejasInscritas = new ArrayList<>();
    private final Set<Pareja> parejasRegistradas = new HashSet<>();
    private final Set<Combatiente> combatientesInscriptos = new HashSet<>();
    private final List<List<Cruce>> rondas = new ArrayList<>();
    private final List<ObservadorTorneo> observadores = new ArrayList<>();
    private boolean iniciado;
    private Pareja campeona;
    private List<Pareja> vigentes = new ArrayList<>();
    private int rondaActual;

    public void agregarObservador(ObservadorTorneo observador) {
        if (observador == null) throw new IllegalArgumentException("El observador no puede ser nulo");
        observadores.add(observador);
    }

    public void notificar(Acontecimiento acontecimiento) {
        for (ObservadorTorneo o : observadores) o.actualizar(acontecimiento);
    }

    public void inscribir(Pareja pareja) {
        if (pareja == null) throw new IllegalArgumentException("La pareja no puede ser nula");
        if (iniciado) throw new InscripcionInvalidaException("El torneo ya comenzó");
        if (parejasRegistradas.contains(pareja)) {
            throw new InscripcionInvalidaException("Ya hay una pareja registrada con el identificador " + pareja.getId());
        }
        for (Combatiente c : pareja.getIntegrantes()) {
            if (combatientesInscriptos.contains(c)) {
                throw new InscripcionInvalidaException(c.getNombre() + " ya está inscripto");
            }
        }
        if (parejasInscritas.size() >= MAXIMO_PAREJAS) {
            throw new CantidadDeParejasInvalidaException("El torneo admite como máximo " + MAXIMO_PAREJAS + " parejas");
        }
        parejasInscritas.add(pareja);
        parejasRegistradas.add(pareja);

        combatientesInscriptos.addAll(pareja.getIntegrantes());
        observadores.addAll(pareja.getIntegrantes());
    }

    public Pareja iniciar() {
        if (haTerminado()) throw new IllegalStateException("El torneo ya se jugó");
        while (!haTerminado()) jugarSiguienteRonda();
        return campeona;
    }
    public void jugarSiguienteRonda() {
        if (haTerminado()) throw new IllegalStateException("El torneo ya se jugó");
        if (!iniciado) {
            if (parejasInscritas.size() != 4 && parejasInscritas.size() != 8) {
                throw new CantidadDeParejasInvalidaException(
                        "El torneo se juega con 4 u 8 parejas (hay " + parejasInscritas.size() + ")");
            }
            iniciado = true;
            vigentes = new ArrayList<>(parejasInscritas);
        }
        rondaActual++;
        List<Cruce> cruces = new ArrayList<>();
        List<Pareja> ganadoras = new ArrayList<>();
        for (int i = 0; i < vigentes.size(); i += 2) {
            Cruce cruce = new Cruce(vigentes.get(i), vigentes.get(i + 1));
            cruces.add(cruce);
            ganadoras.add(new Combate(rondaActual, cruce.getPrimera(), cruce.getSegunda(), observadores).ejecutar());
        }
        rondas.add(Collections.unmodifiableList(cruces));
        vigentes = ganadoras;
        if (vigentes.size() == 1) {
            campeona = vigentes.get(0);
            List<Combatiente> santos = campeona.getIntegrantes();
            notificar(Acontecimiento.proclamacion(rondaActual, campeona.getId(),
                    santos.get(0).getNombre(), santos.get(1).getNombre()));
        }
    }

    public List<Pareja> getParejasVigentes() { return Collections.unmodifiableList(vigentes); }

    public boolean haTerminado() { return campeona != null; }

    public Pareja getCampeona() { return campeona; }

    public List<Pareja> getParejasInscritas() { return Collections.unmodifiableList(parejasInscritas); }

    public List<List<Cruce>> getRondas() { return Collections.unmodifiableList(rondas); }
}
