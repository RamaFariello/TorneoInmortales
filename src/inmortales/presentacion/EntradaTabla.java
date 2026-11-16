package inmortales.presentacion;

import java.util.Comparator;

/** Fila de la tabla de posiciones. */
public record EntradaTabla(int idPareja, String pareja, int ultimaRonda, int vidaFinal) {
    /** Última ronda jugada (desc), vida total final (desc), identificador (asc). */
    public static final Comparator<EntradaTabla> ORDEN = Comparator
            .comparingInt(EntradaTabla::ultimaRonda).reversed()
            .thenComparing(Comparator.comparingInt(EntradaTabla::vidaFinal).reversed())
            .thenComparingInt(EntradaTabla::idPareja);
}
