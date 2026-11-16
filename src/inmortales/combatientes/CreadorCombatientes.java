package inmortales.combatientes;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import inmortales.escuelas.Escuela;

public class CreadorCombatientes {
    private final Map<String, FabricaCombatientes> fabricas = new HashMap<>();
    private int contadorId = 1;

    public CreadorCombatientes() {
        registrarTipo("Cultivador de la Espada", CultivadorDeLaEspada::new);
        registrarTipo("Monje del Cuerpo Celestial", MonjeDelCuerpoCelestial::new);
        registrarTipo("Hechicero del Vacío", HechiceroDelVacio::new);
        registrarTipo("Combatiente Reactivo", CombatienteReactivo::new);
    }

    public void registrarTipo(String tipo, FabricaCombatientes fabrica) {
        String clave = clave(tipo);
        if (fabricas.containsKey(clave)) {
            throw new IllegalArgumentException("El tipo de combatiente " + tipo + " ya está registrado");
        }
        fabricas.put(clave, fabrica);
    }

    public Combatiente crear(String tipo, String nombre, Escuela escuela) {
        FabricaCombatientes fabrica = fabricas.get(clave(tipo));
        if (fabrica == null) throw new IllegalArgumentException("Tipo de combatiente desconocido: " + tipo);
        if (escuela == null) throw new IllegalArgumentException("La escuela no puede ser nula");
        Combatiente combatiente = fabrica.crear(contadorId++, nombre, escuela);
        escuela.preparar(combatiente);
        return combatiente;
    }

    private static String clave(String tipo) {
        if (tipo == null) throw new IllegalArgumentException("El tipo no puede ser nulo");
        String sinTildes = Normalizer.normalize(tipo.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT);
    }
}
