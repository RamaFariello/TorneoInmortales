package inmortales.tecnicas;

import inmortales.combatientes.Combatiente;

public class MuroDelCuerpoDorado extends Tecnica {
    public static final double REDUCCION = 0.30;
    private static final int COSTO = 12;

    public MuroDelCuerpoDorado() {
        super("Muro del Cuerpo Dorado", COSTO, TipoTecnica.DEFENSIVA);
    }

    @Override
    protected void aplicarEfecto(Combatiente ejecutor, Combatiente objetivo) {
        ejecutor.activarReduccionDanio(REDUCCION);
    }

    @Override
    public String describirEfecto(int variacionVida) {
        return "reduce " + (int) Math.round(REDUCCION * 100) + "% el daño que recibe";
    }
}
