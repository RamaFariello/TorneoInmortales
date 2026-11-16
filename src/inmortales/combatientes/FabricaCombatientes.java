package inmortales.combatientes;

import inmortales.escuelas.Escuela;

public interface FabricaCombatientes {
    Combatiente crear(int id, String nombre, Escuela escuela);
}
