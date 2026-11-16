package inmortales.acontecimientos;

/** Observador de acontecimientos (patrón Observer). */
public interface Interesado {
    void notificar(Acontecimiento acontecimiento);
}
