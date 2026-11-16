package inmortales.tecnicas;

import inmortales.combatientes.Combatiente;

public class MeditacionDelInmortal extends Tecnica {
    public static final int VIDA_RECUPERADA = 25;
    private static final int COSTO = 15;

    public MeditacionDelInmortal() {
        super("Meditación del Inmortal", COSTO, TipoTecnica.RECUPERACION);
    }

    @Override
    protected void aplicarEfecto(Combatiente ejecutor, Combatiente objetivo) {
        ejecutor.curar(VIDA_RECUPERADA);
    }

    @Override
    public String describirEfecto(int variacionVida) { return "recupera " + (-variacionVida) + " de vida"; }
}
