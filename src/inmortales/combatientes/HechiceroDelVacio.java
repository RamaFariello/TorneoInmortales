package inmortales.combatientes;

import inmortales.escuelas.Escuela;

public class HechiceroDelVacio extends Combatiente {
    private static final int PORCENTAJE_COSTO = 80;

    public HechiceroDelVacio(int id, String nombre, Escuela escuela) {
        super(id, nombre, escuela);
    }

    @Override
    public String getTipo() { return "Hechicero del Vacío"; }

    @Override
    protected int calcularCostoEnergia(int costoBase) {
        return costoBase * PORCENTAJE_COSTO / 100;
    }
}
