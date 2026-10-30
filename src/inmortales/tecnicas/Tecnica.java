package inmortales.tecnicas;

public abstract class Tecnica {
    private final String nombre;
    private final int costoEnergia;
    private final TipoTecnica tipo;  

    public Tecnica(String nombre, int costoEnergia, TipoTecnica tipo){
        if(costoEnergia < 0) throw new IllegalArgumentException("costo negativo");
        this.nombre = nombre;
        this.costoEnergia = costoEnergia;
        this.tipo = tipo;
    }

    public int getCostoEnergia() {
        return costoEnergia;
    }
    

}
