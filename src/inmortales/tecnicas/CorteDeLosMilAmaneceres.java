package inmortales.tecnicas;

import inmortales.combatientes.Combatiente;

public class CorteDeLosMilAmaneceres extends Tecnica {
    public static final int DANIO_BASE = 20;
    private static final int COSTO = 10;

    public CorteDeLosMilAmaneceres() {
        super("Corte de los Mil Amaneceres", COSTO, TipoTecnica.OFENSIVA);
    }

    @Override
    protected void aplicarEfecto(Combatiente ejecutor, Combatiente objetivo) {
        ejecutor.atacar(objetivo, DANIO_BASE);
    }

    @Override
    public String describirEfecto(int variacionVida) { return "causa " + variacionVida + " de daño"; }
}
