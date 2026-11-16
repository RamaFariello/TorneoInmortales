package inmortales.tecnicas;

import inmortales.combatientes.Combatiente;

public class DominioDeLaNada extends Tecnica {
    public static final int DANIO_BASE = 15;
    public static final int ENERGIA_DRENADA = 10;
    private static final int COSTO = 15;

    public DominioDeLaNada() {
        super("Dominio de la Nada", COSTO, TipoTecnica.OFENSIVA);
    }

    @Override
    protected void aplicarEfecto(Combatiente ejecutor, Combatiente objetivo) {
        ejecutor.atacar(objetivo, DANIO_BASE);
        objetivo.gastarEnergia(ENERGIA_DRENADA);
    }

    @Override
    public String describirEfecto(int variacionVida) {
        return "causa " + variacionVida + " de daño y le quita " + ENERGIA_DRENADA + " de energía";
    }
}
