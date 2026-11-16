package inmortales.acontecimientos;

/**
 * Hecho del combate o del torneo. Las implementaciones son records inmutables que
 * guardan una copia de los datos del momento en que ocurrió (no referencias a objetos vivos).
 */
public interface Acontecimiento {
    String describir();

    /** true para acontecimientos de detalle (acciones y efectos), que un presentador puede omitir. */
    default boolean esDetalle() { return false; }
}
