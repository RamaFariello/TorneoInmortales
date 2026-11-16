package inmortales.torneo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inmortales.combatientes.Combatiente;
import inmortales.excepciones.TecnicaNoDisponibleException;
import inmortales.tecnicas.Tecnica;

public class Pareja {
    private final int id;
    private final Combatiente integrante1;
    private final Combatiente integrante2;
    private final Map<Tecnica, Combatiente> tecnicasUsadasEnCombate = new HashMap<>();

    public Pareja(int id, Combatiente uno, Combatiente otro) {
        if (uno == null || otro == null) throw new IllegalArgumentException("Faltan integrantes");
        if (uno.equals(otro)) throw new IllegalArgumentException("Una pareja necesita dos combatientes distintos");
        this.id = id;
        if (uno.getId() < otro.getId()) {
            this.integrante1 = uno;
            this.integrante2 = otro;
        } else {
            this.integrante1 = otro;
            this.integrante2 = uno;
        }
    }

    public int getId() { return id; }

    public List<Combatiente> getIntegrantes() {
        return Collections.unmodifiableList(Arrays.asList(integrante1, integrante2));
    }

    public List<Combatiente> getIntegrantesEnPie() {
        List<Combatiente> enPie = new ArrayList<>();
        if (integrante1.estaEnPie()) enPie.add(integrante1);
        if (integrante2.estaEnPie()) enPie.add(integrante2);
        return enPie;
    }

    public boolean estaEnPie() { return integrante1.estaEnPie() || integrante2.estaEnPie(); }

    public int getVidaTotal() { return integrante1.getVidaActual() + integrante2.getVidaActual(); }

    public Combatiente getCompanero(Combatiente combatiente) {
        return combatiente.equals(integrante1) ? integrante2 : integrante1;
    }

    public boolean puedeUsarTecnica(Combatiente combatiente, Tecnica tecnica) {
        Combatiente primero = tecnicasUsadasEnCombate.get(tecnica);
        if (primero == null || primero.equals(combatiente)) return true;
        return !primero.estaEnPie() || primero.usosRestantes(tecnica) == 0;
    }

    public void registrarUsoTecnica(Combatiente combatiente, Tecnica tecnica) {
        if (!puedeUsarTecnica(combatiente, tecnica)) {
            throw new TecnicaNoDisponibleException(combatiente.getNombre() + " no puede usar "
                    + tecnica.getNombre() + ": la reservó " + tecnicasUsadasEnCombate.get(tecnica).getNombre());
        }
        if (!tecnicasUsadasEnCombate.containsKey(tecnica)) tecnicasUsadasEnCombate.put(tecnica, combatiente);
    }

    public void reiniciarRestricciones() { tecnicasUsadasEnCombate.clear(); }

    public void prepararParaCombate() {
        reiniciarRestricciones();
        integrante1.prepararParaCombate();
        integrante2.prepararParaCombate();
    }

    public void finalizarCombate() {
        integrante1.finalizarCombate();
        integrante2.finalizarCombate();
    }

    public String getDescripcion() {
        return "Pareja " + id + " (" + integrante1.getNombre() + " y " + integrante2.getNombre() + ")";
    }

    @Override
    public boolean equals(Object o) { return (o instanceof Pareja) && id == ((Pareja) o).id; }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() { return getDescripcion(); }
}
