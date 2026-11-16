package inmortales.escuelas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import inmortales.combatientes.Combatiente;
import inmortales.equipamiento.CategoriaEquipamiento;
import inmortales.equipamiento.Equipamiento;
import inmortales.tecnicas.Tecnica;

public class Escuela {
    private final String nombre;
    private final List<Tecnica> tecnicasIniciales;
    private final List<Equipamiento> equipamientoInicial;

    public Escuela(String nombre, List<Tecnica> tecnicasIniciales, List<Equipamiento> equipamientoInicial) {
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("La escuela necesita nombre");
        this.nombre = nombre;
        this.tecnicasIniciales = Collections.unmodifiableList(new ArrayList<>(tecnicasIniciales));
        this.equipamientoInicial = Collections.unmodifiableList(new ArrayList<>(equipamientoInicial));
        validarEquipamiento();
    }

    private void validarEquipamiento() {
        if (equipamientoInicial.size() > Combatiente.MAX_EQUIPAMIENTO) {
            throw new IllegalArgumentException(
                    "No se puede asignar más de " + Combatiente.MAX_EQUIPAMIENTO + " equipamientos");
        }
        Set<CategoriaEquipamiento> usadas = EnumSet.noneOf(CategoriaEquipamiento.class);
        for (Equipamiento e : equipamientoInicial) {
            if (!usadas.add(e.getCategoria())) {
                throw new IllegalArgumentException("Ya existe un equipamiento de categoría " + e.getCategoria());
            }
        }
    }

    public void preparar(Combatiente combatiente) {
        for (Tecnica t : tecnicasIniciales) combatiente.aprenderTecnica(t);
        for (Equipamiento e : equipamientoInicial) combatiente.equipar(e);
    }

    public String getNombre() { return nombre; }

    public List<Tecnica> getTecnicasIniciales() { return tecnicasIniciales; }

    public List<Equipamiento> getEquipamientoInicial() { return equipamientoInicial; }

    @Override
    public String toString() { return nombre; }
}
