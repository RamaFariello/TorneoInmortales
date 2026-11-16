package inmortales.equipamiento;

public class AnilloEspiritual extends Equipamiento {
    private static final int VALOR = 5;

    public AnilloEspiritual() {
        super("Anillo Espiritual", CategoriaEquipamiento.ANILLO);
    }

    @Override
    public int getBonusEnergiaMaxima() { return VALOR; }
}
