package inmortales.equipamiento;

public class AnilloEspiritual extends  Equipamiento{
    private static final int BONUS_ENERGIA = 5;

    public AnilloEspiritual(){
        super("Anillo Espiritual",Categoria.ANILLO);
    }

    @Override 
    public int getBonusEnergiaMaxima(){
        return BONUS_ENERGIA;
    }
}
