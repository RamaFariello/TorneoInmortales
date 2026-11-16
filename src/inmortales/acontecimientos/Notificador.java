package inmortales.acontecimientos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Mantiene la lista de interesados y les comunica cada acontecimiento, en orden de suscripción. */
public class Notificador {
    private final List<Interesado> interesados = new ArrayList<>();

    public void suscribir(Interesado interesado) {
        Objects.requireNonNull(interesado);
        if (!interesados.contains(interesado)) interesados.add(interesado);
    }

    public void desuscribir(Interesado interesado) { interesados.remove(interesado); }

    public void publicar(Acontecimiento acontecimiento) {
        for (Interesado i : interesados) i.notificar(acontecimiento);
    }
}
