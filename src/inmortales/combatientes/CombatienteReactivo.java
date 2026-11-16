package inmortales.combatientes;

import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.TipoAcontecimiento;
import inmortales.escuelas.Escuela;

public class CombatienteReactivo extends Combatiente {
    private static final int PORCENTAJE_POTENCIADO = 150;

    private boolean ataquePotenciado;

    public CombatienteReactivo(int id, String nombre, Escuela escuela) {
        super(id, nombre, escuela);
    }

    @Override
    public String getTipo() { return "Combatiente Reactivo"; }

    public boolean tieneAtaquePotenciado() { return ataquePotenciado; }

    @Override
    public void actualizar(Acontecimiento acontecimiento) {
        if (acontecimiento.getTipo() == TipoAcontecimiento.ELIMINACION
                && estaEnPie()
                && acontecimiento.getDatoInt("idCompanero") == getId()) {
            ataquePotenciado = true;
        }
    }

    @Override
    protected int calcularDanioCausado(int danioBase) {
        if (!ataquePotenciado) return danioBase;
        ataquePotenciado = false;
        return danioBase * PORCENTAJE_POTENCIADO / 100;
    }

    @Override
    protected String marcaDeEstado() {
        return ataquePotenciado ? " [ataque potenciado]" : "";
    }

    @Override
    public void prepararParaCombate() {
        super.prepararParaCombate();
        ataquePotenciado = false;
    }
}
