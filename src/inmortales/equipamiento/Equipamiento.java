package inmortales.equipamiento;

public abstract class Equipamiento {
    private final String nombre;
    private final CategoriaEquipamiento categoria;

    protected Equipamiento(String nombre, CategoriaEquipamiento categoria) {
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public String getNombre() { return nombre; }

    public CategoriaEquipamiento getCategoria() { return categoria; }

    public int getBonusDanio() { return 0; }

    public int getReduccionDanio() { return 0; }

    public int getBonusVidaMaxima() { return 0; }

    public int getBonusEnergiaMaxima() { return 0; }

    @Override
    public String toString() { return nombre; }
}
