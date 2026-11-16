package inmortales.acontecimientos;

import inmortales.tecnicas.TipoTecnica;

/**
 * Acción realizada en un turno. Para un ataque básico, ataqueBasico es true y tipo es OFENSIVA.
 * valor: daño causado (ofensiva) o vida recuperada (recuperación).
 */
public record AccionRealizada(int ronda, int asalto, int idEjecutor, String ejecutor, String accion,
                              boolean ataqueBasico, TipoTecnica tipo, int idObjetivo, String objetivo,
                              int valor, int vidaObjetivo, int energiaEjecutor) implements Acontecimiento {
    @Override
    public String describir() {
        String prefijo = "  [A" + asalto + "] " + ejecutor + " ";
        if (ataqueBasico) {
            return prefijo + "lanza un ataque básico contra " + objetivo + ": " + valor + " de daño (vida " + vidaObjetivo + ")";
        }
        return switch (tipo) {
            case OFENSIVA -> prefijo + "usa " + accion + " contra " + objetivo + ": " + valor
                    + " de daño (vida " + vidaObjetivo + ")";
            case DEFENSIVA -> prefijo + "usa " + accion + " y endurece su defensa";
            case RECUPERACION -> prefijo + "usa " + accion + " y recupera " + valor + " de vida (vida " + vidaObjetivo + ")";
        };
    }

    @Override
    public boolean esDetalle() { return true; }
}
