package inmortales.tecnicas;

import inmortales.combatientes.Combatiente;

public class RugidoDeLaBestiaPrimordial extends Tecnica {
    public static final int DANIO_BASE = 35;
    private static final int COSTO = 25;

    public RugidoDeLaBestiaPrimordial() {
        super("Rugido de la Bestia Primordial", COSTO, TipoTecnica.OFENSIVA);
    }

    @Override
    protected void aplicarEfecto(Combatiente ejecutor, Combatiente objetivo) {
        ejecutor.atacar(objetivo, DANIO_BASE);
    }

    @Override
    public String describirEfecto(int variacionVida) { return "causa " + variacionVida + " de daño"; }
}
