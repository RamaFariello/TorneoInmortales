package inmortales.equipamiento;

public class ArmaduraDorada extends Equipamiento {
    private static final int VALOR = 4;

    public ArmaduraDorada() {
        super("Armadura Dorada", CategoriaEquipamiento.ARMADURA);
    }

    @Override
    public int getReduccionDanio() { return VALOR; }
}
