package inmortales.equipamiento;

public class SelloDelFenix extends Equipamiento{
    private static final int BONUS_VIDA = 30;

    public SelloDelFenix(){
        super("Sello del Fenix",Categoria.SELLO);
    }

    @Override 
    public int getBonusVidaMaxima(){
        return BONUS_VIDA;
    }
}
