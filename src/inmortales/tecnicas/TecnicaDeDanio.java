package inmortales.tecnicas;

public abstract class TecnicaDeDanio extends Tecnica{
    private final int danioBase;

    public TecnicaDeDanio(String nombre, int costoEnergia, int danioBase ){
        super(nombre,costoEnergia,TipoTecnica.OFENSIVA);
        this.danioBase = danioBase;
    }
}
