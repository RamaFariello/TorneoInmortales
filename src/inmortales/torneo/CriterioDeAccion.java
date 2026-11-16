package inmortales.torneo;

import inmortales.combatientes.Combatiente;
import inmortales.tecnicas.Tecnica;

import java.util.Optional;

/** Decide qué técnica usa un combatiente en su turno. Vacío = ataque básico. Debe ser determinista. */
public interface CriterioDeAccion {
    Optional<Tecnica> elegir(Combatiente combatiente, Pareja pareja);
}
