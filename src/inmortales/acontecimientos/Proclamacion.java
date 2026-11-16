package inmortales.acontecimientos;

public record Proclamacion(int idPareja, String pareja, String primerSanto, String segundoSanto)
        implements Acontecimiento {
    @Override
    public String describir() {
        return "\n*** ¡" + primerSanto + " y " + segundoSanto + " (pareja " + idPareja
                + ") son proclamados los Dos Santos Supremos! ***";
    }
}
