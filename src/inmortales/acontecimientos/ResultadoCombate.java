package inmortales.acontecimientos;

public record ResultadoCombate(int ronda, int idGanadora, String ganadora,
                               int idPareja1, String pareja1, int vidaPareja1,
                               int idPareja2, String pareja2, int vidaPareja2,
                               int asaltos, boolean porLimiteDeAsaltos) implements Acontecimiento {
    @Override
    public String describir() {
        return "--- Resultado ronda " + ronda + ": gana " + ganadora + " tras " + asaltos + " asaltos"
                + (porLimiteDeAsaltos ? " (por límite de asaltos)" : "") + ". Vida total: "
                + pareja1 + " = " + vidaPareja1 + " | " + pareja2 + " = " + vidaPareja2;
    }

    public int vidaDe(int idPareja) {
        if (idPareja == idPareja1) return vidaPareja1;
        if (idPareja == idPareja2) return vidaPareja2;
        throw new IllegalArgumentException("La pareja " + idPareja + " no jugó este combate");
    }
}
