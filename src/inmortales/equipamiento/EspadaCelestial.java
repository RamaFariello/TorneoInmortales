package inmortales.equipamiento;

public class EspadaCelestial extends  Equipamiento{
    private static final int BONUS_DANIO = 5;

    public EspadaCelestial(){
        super("Espada Celestial",Categoria.ARMA);
    }

    @Override 
    public int modificarDanioProvocado(int danio){
        return (danio + BONUS_DANIO);
    }
}
