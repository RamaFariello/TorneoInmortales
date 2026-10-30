package inmortales.combatientes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import inmortales.equipamiento.Categoria;
import inmortales.equipamiento.Equipamiento;
import inmortales.excepciones.EquipamientoInvalidoException;
import inmortales.tecnicas.Tecnica;

public abstract class Combatiente {
    public static final int VIDA_INICIAL = 100;
    public static final int ENERGIA_INICIAL = 50;
    public static final int USOS_POR_TECNICA = 5;
    public static final int DANIO_ATAQUE_BASICO = 10;
    public static final int MAX_EQUIPAMIENTO = 3;
    //public static final int DANIO_MINIMO = 1;
    
    private final int id;
    private final String nombre;
    private int vidaAct;
    private int vidaMax;
    private int energiaAct;
    private int energiaMax;
    private boolean enCombate;
    
    //Técnicas en el orden en que las asignó la escuela (usado para desempatar).
    private final List<Tecnica> tecnicas = new ArrayList<>();
    // Map por categoria: la clave impide tener dos del mismo tipo (arma, armadura...)
    private final Map<Categoria,Equipamiento> equipamientos = new LinkedHashMap<>();
    // Map tecnica -> usos restantes: los usos se registran en el combatiente
    private final Map<Tecnica, Integer> usosRestantes = new HashMap<>();


    public Combatiente(int id, String nombre){
        this(id, nombre,VIDA_INICIAL,ENERGIA_INICIAL);
    }

    public Combatiente(int id, String nombre, int vidaMax, int energMax){
        if (vidaMax <= 0 || energMax < 0 || nombre.isBlank() || nombre == null){
            throw new IllegalArgumentException("Valores iniciales inválidos");
        }
        this.id = id;
        this.nombre=nombre;
        this.vidaMax = vidaMax;
        this.vidaAct = vidaMax;
        this.energiaAct = energMax;
        this.energiaMax = energMax;
    }

    //combatiente puede preguntar por que equipamiento tiene 
    public Map<Categoria, Equipamiento> getEquipamientos() {
        return equipamientos;
    }

    public void equipar(Equipamiento equipo){
        if(enCombate){throw new EquipamientoInvalidoException("No se puede cambiar el equipamiento mistras se esta en combate");}
        if(equipamientos.size() > MAX_EQUIPAMIENTO){throw new EquipamientoInvalidoException(nombre + " ya lleva " + MAX_EQUIPAMIENTO + " equipamientos");}
        if(equipamientos.containsKey(equipo.getCategoria())){
            throw new EquipamientoInvalidoException("Ya se otorgo un equipamiento de la categoria " + equipo.getCategoria());
        }
        equipamientos.put(equipo.getCategoria(), equipo);
    }

    public void aprenderTecnica(Tecnica tecnica){
        if(!tecnicas.contains(tecnica)) {
            tecnicas.add(tecnica);
        }
    }

    public int modificarDanioProvocado(int danio) {return danio;};
    public int modificarDanioRecibido(int danio) {return danio;};
    public int modificarCostoEnergia(Tecnica tecnica){return tecnica.getCostoEnergia();};

    





}
