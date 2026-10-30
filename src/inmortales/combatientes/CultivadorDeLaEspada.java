package inmortales.combatientes;

public class CultivadorDeLaEspada extends Combatiente{
    private static final int DANIO_PROVOCA_PORCENTAJE = 125;

    public CultivadorDeLaEspada(int id,String nombre){
        super(id,nombre);
    }

    @Override 
    public int modificarDanioProvocado(int danio){
        return (danio*DANIO_PROVOCA_PORCENTAJE/100);
    }
}
