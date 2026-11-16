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

    public String getNombre() { return nombre; }

    public int getCostoEnergia() {
        return costoEnergia;
    }

    public TipoTecnica getTipo() { return tipo; }

    /** Dos tecnicas son la misma si tienen el mismo nombre (sirve para usarlas como clave de un Map). */
    @Override
    public boolean equals(Object o) {
        return (o instanceof Tecnica) && nombre.equals(((Tecnica) o).nombre);
    }

    @Override
    public int hashCode() { return nombre.hashCode(); }

    @Override
    public String toString() { return nombre; }
    

}
