package inmortales.tecnicas;

import inmortales.combatientes.Combatiente;

public abstract class Tecnica {
    private final String nombre;
    private final int costoEnergia;
    private final TipoTecnica tipo;

    protected Tecnica(String nombre, int costoEnergia, TipoTecnica tipo) {
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("nombre invalido");
        if (costoEnergia < 0) throw new IllegalArgumentException("costo negativo");
        this.nombre = nombre;
        this.costoEnergia = costoEnergia;
        this.tipo = tipo;
    }

    public final void ejecutar(Combatiente ejecutor, Combatiente objetivo) {
        aplicarEfecto(ejecutor, objetivo);
    }

    protected abstract void aplicarEfecto(Combatiente ejecutor, Combatiente objetivo);

    public abstract String describirEfecto(int variacionVida);

    public String getNombre() { return nombre; }

    public int getCostoEnergia() { return costoEnergia; }

    public TipoTecnica getTipo() { return tipo; }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Tecnica) && nombre.equals(((Tecnica) o).nombre);
    }

    @Override
    public int hashCode() { return nombre.hashCode(); }

    @Override
    public String toString() { return nombre; }
}
