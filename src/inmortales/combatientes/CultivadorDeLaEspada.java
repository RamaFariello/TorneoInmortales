package inmortales.combatientes;

import inmortales.escuelas.Escuela;

public class CultivadorDeLaEspada extends Combatiente {
    private static final int PORCENTAJE_DANIO = 125;

    public CultivadorDeLaEspada(int id, String nombre, Escuela escuela) {
        super(id, nombre, escuela);
    }

    @Override
    public String getTipo() { return "Cultivador de la Espada"; }

    @Override
    protected int calcularDanioCausado(int danioBase) {
        return danioBase * PORCENTAJE_DANIO / 100;
    }
}
