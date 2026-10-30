package inmortales.equipamiento;

public class ArmaduraDorada extends Equipamiento {
    private static final int REDUCCION_DANIO = 4;

    public ArmaduraDorada(){
        super("Armadura Dorada",Categoria.ARMADURA);
    }

    @Override 
    public int modificarDanioRecibido(int danio){
        return (danio - REDUCCION_DANIO);
    }
}
