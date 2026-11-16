package inmortales.torneo;

import java.util.ArrayList;
import java.util.List;

import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.ObservadorTorneo;
import inmortales.combatientes.Combatiente;

public class Combate {
    private final int ronda;
    private final Pareja pareja1;
    private final Pareja pareja2;
    private final int limiteAsaltos;
    private final List<ObservadorTorneo> observadores;
    private int asaltoActual;
    private Pareja ganadora;

    public Combate(int ronda, Pareja una, Pareja otra, List<ObservadorTorneo> observadores) {
        this(ronda, una, otra, observadores, ReglasCombate.LIMITE_ASALTOS);
    }

    public Combate(int ronda, Pareja una, Pareja otra, List<ObservadorTorneo> observadores, int limiteAsaltos) {
        if (limiteAsaltos < 0) throw new IllegalArgumentException("El límite de asaltos no puede ser negativo");
        this.ronda = ronda;
        this.pareja1 = una.getId() <= otra.getId() ? una : otra;
        this.pareja2 = pareja1 == una ? otra : una;
        this.limiteAsaltos = limiteAsaltos;
        this.observadores = new ArrayList<>(observadores);
    }

    public Pareja getGanadora() { return ganadora; }

    public int getAsaltosJugados() { return asaltoActual; }

    public Pareja ejecutar() {
        if (ganadora != null) throw new IllegalStateException("El combate ya se jugó");
        pareja1.prepararParaCombate();
        pareja2.prepararParaCombate();
        notificar(Acontecimiento.inicioCombate(ronda, pareja1.getId(), pareja1.getDescripcion(),
                pareja2.getId(), pareja2.getDescripcion()));

        while (asaltoActual < limiteAsaltos && ambasEnPie()) {
            asaltoActual++;
            recuperarEnergia();
            jugarTurno(pareja1, pareja2);
            if (ambasEnPie()) jugarTurno(pareja2, pareja1);
        }

        ganadora = determinarGanadora();
        Pareja perdedora = ganadora == pareja1 ? pareja2 : pareja1;
        pareja1.finalizarCombate();
        pareja2.finalizarCombate();
        notificar(Acontecimiento.resultadoCombate(ronda, asaltoActual,
                ganadora.getId(), ganadora.getDescripcion(), ganadora.getVidaTotal(),
                perdedora.getId(), perdedora.getDescripcion(), perdedora.getVidaTotal()));
        return ganadora;
    }

    private boolean ambasEnPie() { return pareja1.estaEnPie() && pareja2.estaEnPie(); }

    private void recuperarEnergia() {
        for (Combatiente c : pareja1.getIntegrantes()) c.recuperarEnergia(ReglasCombate.ENERGIA_POR_ASALTO);
        for (Combatiente c : pareja2.getIntegrantes()) c.recuperarEnergia(ReglasCombate.ENERGIA_POR_ASALTO);
    }

    private void jugarTurno(Pareja propia, Pareja rival) {
        for (Combatiente actor : propia.getIntegrantes()) {
            if (!ambasEnPie()) return;
            if (!actor.estaEnPie()) continue;
            List<Combatiente> oponentes = rival.getIntegrantesEnPie();
            Acontecimiento accion = actor.ejecutarTurno(oponentes, propia);
            notificar(accion.ubicarEn(ronda, asaltoActual));
            for (Combatiente oponente : oponentes) {
                if (!oponente.estaEnPie()) {
                    notificar(Acontecimiento.eliminacion(ronda, asaltoActual, oponente.getId(),
                            oponente.getNombre(), rival.getId(), rival.getCompanero(oponente).getId()));
                }
            }
        }
    }

    private Pareja determinarGanadora() {
        if (!pareja1.estaEnPie()) return pareja2;
        if (!pareja2.estaEnPie()) return pareja1;
        return pareja2.getVidaTotal() > pareja1.getVidaTotal() ? pareja2 : pareja1;
    }

    private void notificar(Acontecimiento acontecimiento) {
        for (ObservadorTorneo o : observadores) o.actualizar(acontecimiento);
    }
}
