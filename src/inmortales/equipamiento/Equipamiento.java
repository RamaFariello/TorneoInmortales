package inmortales.equipamiento;

public abstract class Equipamiento {
    private final String nombre;
    private final Categoria categoria;

    public Equipamiento(String nombre, Categoria categoria){
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public Categoria getCategoria(){
        return this.categoria;
    }

    public int getBonusVidaMaxima() { return 0; }
    public int getBonusEnergiaMaxima() { return 0; }
    public int modificarDanioProvocado(int danio) { return danio; }
    public int modificarDanioRecibido(int danio) { return danio; }
    
}
