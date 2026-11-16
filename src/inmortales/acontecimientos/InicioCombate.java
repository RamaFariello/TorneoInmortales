package inmortales.acontecimientos;

public record InicioCombate(int ronda, int idPareja1, String pareja1, int idPareja2, String pareja2)
        implements Acontecimiento {
    @Override
    public String describir() {
        return "\n=== Ronda " + ronda + " | Comienza el combate: " + pareja1 + " vs " + pareja2 + " ===";
    }
}
