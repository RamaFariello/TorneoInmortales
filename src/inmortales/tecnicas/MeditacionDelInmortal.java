package inmortales.tecnicas;

public class MeditacionDelInmortal extends Tecnica{
    private static final int VIDA_RECUPERADA = 25;
    private static final int COSTO = 15;

    public  MeditacionDelInmortal(){
        super("Meditacion del Inmortal", COSTO,TipoTecnica.RECUPERACION);
    }
}
