package inmortales.equipamiento;

public class EspadaCelestial extends Equipamiento {
    private static final int VALOR = 5;

    public EspadaCelestial() {
        super("Espada Celestial", CategoriaEquipamiento.ARMA);
    }

    @Override
    public int getBonusDanio() { return VALOR; }
}
