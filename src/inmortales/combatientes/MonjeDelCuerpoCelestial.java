package inmortales.combatientes;

import inmortales.escuelas.Escuela;

public class MonjeDelCuerpoCelestial extends Combatiente {
    private static final int PORCENTAJE_DANIO_RECIBIDO = 80;

    public MonjeDelCuerpoCelestial(int id, String nombre, Escuela escuela) {
        super(id, nombre, escuela);
    }

    @Override
    public String getTipo() { return "Monje del Cuerpo Celestial"; }

    @Override
    protected int calcularDanioRecibido(int danioBase) {
        return danioBase * PORCENTAJE_DANIO_RECIBIDO / 100;
    }
}
