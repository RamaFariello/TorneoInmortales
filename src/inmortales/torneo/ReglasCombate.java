package inmortales.torneo;

/** Valores configurables del combate. */
public record ReglasCombate(int limiteAsaltos, int energiaPorAsalto) {
    public static final int LIMITE_ASALTOS = 25;
    public static final int ENERGIA_POR_ASALTO = 8;

    public ReglasCombate {
        if (limiteAsaltos < 1) throw new IllegalArgumentException("El límite de asaltos debe ser al menos 1");
        if (energiaPorAsalto < 0) throw new IllegalArgumentException("La energía por asalto no puede ser negativa");
    }

    public static ReglasCombate porDefecto() { return new ReglasCombate(LIMITE_ASALTOS, ENERGIA_POR_ASALTO); }
}
