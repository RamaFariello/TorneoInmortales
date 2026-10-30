package inmortales.combatientes;

public class MonjeDelCuerpoCelestial extends Combatiente {
    public static final int DANIO_RECIBE_PROCENTAJE = 80;
    

    public MonjeDelCuerpoCelestial(int id, String nombre){
        super(id, nombre);
    }

    @Override 
    public int modificarDanioRecibido(int danio){
        return (danio*DANIO_RECIBE_PROCENTAJE/100);
    }
}
