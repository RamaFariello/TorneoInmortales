package inmortales.escuelas;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import inmortales.combatientes.Combatiente;
import inmortales.equipamiento.Categoria;
import inmortales.equipamiento.Equipamiento;
import inmortales.tecnicas.Tecnica;

public class Escuela {
    private final String nombre;
    private final List<Tecnica> tecnicas;
    private final List<Equipamiento> equipamientos;

    public Escuela(String nombre, List<Tecnica> tecnicas, List<Equipamiento> equipamientos){
        this.nombre = nombre;
        this.tecnicas = List.copyOf(tecnicas);
        this.equipamientos = List.copyOf(equipamientos);
        validarEquipamiento();
    }

    private void validarEquipamiento(){
        if(equipamientos.size() > Combatiente.MAX_EQUIPAMIENTO){
            throw new IllegalArgumentException("No se puede asignas mas de " + Combatiente.MAX_EQUIPAMIENTO + " equipamientos");
        }
        Set<Categoria> categoriasUsadas = new HashSet<>();
        for(Equipamiento equipo : equipamientos){
            if(categoriasUsadas.add(equipo.getCategoria()) == false){ //osea la categoria esta duplicada
                throw new IllegalArgumentException("Ya existe un equipamiento de categoria " + equipo.getCategoria());
            }
        }
    }

    //la escuela le da las tecnicas y equipamiento a su combatiente
    public void preparar(Combatiente combatiente){ 
        for (Tecnica t : tecnicas) combatiente.aprenderTecnica(t);
        for (Equipamiento e : equipamientos) combatiente.equipar(e);
        
    }

   


}
