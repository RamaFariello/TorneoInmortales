package inmortales.acontecimientos;

public record Eliminacion(int ronda, int asalto, int idCombatiente, String combatiente, int idPareja)
        implements Acontecimiento {
    @Override
    public String describir() {
        return "  X " + combatiente + " (pareja " + idPareja + ") queda fuera de combate en el asalto " + asalto;
    }
}
