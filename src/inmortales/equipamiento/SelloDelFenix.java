package inmortales.equipamiento;

public class SelloDelFenix extends Equipamiento {
    private static final int VALOR = 30;

    public SelloDelFenix() {
        super("Sello del Fénix", CategoriaEquipamiento.SELLO);
    }

    @Override
    public int getBonusVidaMaxima() { return VALOR; }
}
