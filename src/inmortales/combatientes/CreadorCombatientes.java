package inmortales.combatientes;

import java.util.HashMap;
import java.util.Map;

import inmortales.escuelas.Escuela;

public class CreadorCombatientes {
    private final Map<String, FabricaCombatientes> fabricas = new HashMap<>();
    private int proximoId = 1;
    
    
    public CreadorCombatientes(){
        registrarTipo("Cultivador de la Espada",CultivadorDeLaEspada::new);
        registrarTipo("Monje del Cuerpo Celestial", MonjeDelCuerpoCelestial::new);
        registrarTipo("Hechicero del Vacio", HechiceroDelVacio::new);
    }


    private void registrarTipo(String tipoCombatiente, FabricaCombatientes fabrica){
        if(fabricas.containsKey(tipoCombatiente)){
            throw new IllegalArgumentException("El tipo de combatiente " + tipoCombatiente + " ya esta registrado");
        }
        fabricas.put(tipoCombatiente,fabrica);
    }

    public Combatiente crear(String tipoCombatiente, String nombre, Escuela escuela){
        FabricaCombatientes fabrica = fabricas.get(tipoCombatiente);
        if(fabrica == null){
            throw new IllegalArgumentException("Tipo de combatiente desconocido: " + tipoCombatiente);
        }
        Combatiente combatiente = fabrica.crear(proximoId++, nombre);
        escuela.preparar(combatiente);
        return combatiente;
    }
}
