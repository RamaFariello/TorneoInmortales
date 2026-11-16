package inmortales.combatientes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inmortales.acontecimientos.Acontecimiento;
import inmortales.equipamiento.CategoriaEquipamiento;
import inmortales.equipamiento.Equipamiento;
import inmortales.escuelas.Escuela;
import inmortales.excepciones.EquipamientoInvalidoException;
import inmortales.excepciones.TecnicaNoDisponibleException;
import inmortales.tecnicas.Tecnica;
import inmortales.tecnicas.TipoTecnica;
import inmortales.torneo.Pareja;
import inmortales.acontecimientos.ObservadorTorneo;

public abstract class Combatiente implements ObservadorTorneo {
    public static final int VIDA_BASE = 100;
    public static final int ENERGIA_BASE = 50;
    public static final int USOS_POR_TECNICA = 5;
    public static final int DANIO_ATAQUE_BASICO = 10;
    public static final int MAX_EQUIPAMIENTO = 3;
    public static final int DANIO_MINIMO_RECIBIDO = 1;

    private final int id;
    private final String nombre;
    private final Escuela escuela;
    private final int vidaBase;
    private final int energiaBase;
    private int vidaMaxima;
    private int energiaMaxima;
    private int vidaActual;
    private int energiaActual;
    private double reduccionDanioRecibido;
    private boolean enCombate;

    private final List<Tecnica> tecnicas = new ArrayList<>();

    private final Map<CategoriaEquipamiento, Equipamiento> equipamientos =
            new EnumMap<>(CategoriaEquipamiento.class);

    private final Map<Tecnica, Integer> usosTecnica = new HashMap<>();

    public Combatiente(int id, String nombre, Escuela escuela) {
        this(id, nombre, escuela, VIDA_BASE, ENERGIA_BASE);
    }

    public Combatiente(int id, String nombre, Escuela escuela, int vidaBase, int energiaBase) {
        if (nombre == null || nombre.trim().isEmpty() || vidaBase <= 0 || energiaBase < 0) {
            throw new IllegalArgumentException("Valores iniciales inválidos");
        }
        this.id = id;
        this.nombre = nombre;
        this.escuela = escuela;
        this.vidaBase = vidaBase;
        this.energiaBase = energiaBase;
        this.vidaMaxima = vidaBase;
        this.energiaMaxima = energiaBase;
        this.vidaActual = vidaBase;
        this.energiaActual = energiaBase;
    }

    public abstract String getTipo();

    public int getId() { return id; }

    public String getNombre() { return nombre; }

    public Escuela getEscuela() { return escuela; }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Combatiente) && id == ((Combatiente) o).id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() { return nombre; }

    public int getVidaActual() { return vidaActual; }

    public int getVidaMaxima() { return vidaMaxima; }

    public int getEnergiaActual() { return energiaActual; }

    public int getEnergiaMaxima() { return energiaMaxima; }

    public boolean estaEnPie() { return vidaActual > 0; }

    public int perderVida(int cantidad) {
        int perdida = Math.min(vidaActual, Math.max(0, cantidad));
        vidaActual -= perdida;
        return perdida;
    }

    public int curar(int cantidad) {
        if (!estaEnPie()) return 0;
        int curado = Math.min(vidaMaxima - vidaActual, Math.max(0, cantidad));
        vidaActual += curado;
        return curado;
    }

    public void gastarEnergia(int cantidad) {
        energiaActual -= Math.min(energiaActual, Math.max(0, cantidad));
    }

    public void recuperarEnergia(int cantidad) {
        if (!estaEnPie()) return;
        energiaActual += Math.min(energiaMaxima - energiaActual, Math.max(0, cantidad));
    }

    protected int calcularDanioCausado(int danioBase) { return danioBase; }

    protected int calcularDanioRecibido(int danioBase) { return danioBase; }

    protected int calcularCostoEnergia(int costoBase) { return costoBase; }

    public int atacar(Combatiente objetivo, int danioBase) {
        int danio = danioBase;
        for (Equipamiento e : equipamientos.values()) danio += e.getBonusDanio();
        danio = Math.max(0, calcularDanioCausado(danio));
        return objetivo.recibirDanio(danio);
    }

    public int atacarBasico(Combatiente objetivo) {
        if (!estaEnPie()) throw new IllegalStateException(nombre + " está fuera de combate");
        return atacar(objetivo, DANIO_ATAQUE_BASICO);
    }

    public int recibirDanio(int cantidad) {
        if (!estaEnPie()) return 0;
        int danio = cantidad;
        for (Equipamiento e : equipamientos.values()) danio -= e.getReduccionDanio();
        danio = Math.max(0, calcularDanioRecibido(Math.max(0, danio)));
        danio = (int) Math.floor(danio * (1.0 - reduccionDanioRecibido) + 1e-9);
        return perderVida(Math.max(DANIO_MINIMO_RECIBIDO, danio));
    }

    public void activarReduccionDanio(double porcentaje) {
        reduccionDanioRecibido = Math.max(reduccionDanioRecibido, Math.min(0.9, porcentaje));
    }

    public boolean tieneDefensaActiva() { return reduccionDanioRecibido > 0; }

    public Map<CategoriaEquipamiento, Equipamiento> getEquipamientos() {
        return Collections.unmodifiableMap(equipamientos);
    }

    public void equipar(Equipamiento item) {
        if (item == null) throw new IllegalArgumentException("El equipamiento no puede ser nulo");
        if (enCombate) {
            throw new EquipamientoInvalidoException("No se puede cambiar el equipamiento durante el combate");
        }
        if (equipamientos.size() >= MAX_EQUIPAMIENTO) {
            throw new EquipamientoInvalidoException(nombre + " ya lleva " + MAX_EQUIPAMIENTO + " equipamientos");
        }
        if (equipamientos.containsKey(item.getCategoria())) {
            throw new EquipamientoInvalidoException(
                    nombre + " ya lleva un equipamiento de categoría " + item.getCategoria());
        }
        equipamientos.put(item.getCategoria(), item);
        recalcularMaximos();
    }

    public Equipamiento desequipar(CategoriaEquipamiento categoria) {
        if (enCombate) {
            throw new EquipamientoInvalidoException("No se puede cambiar el equipamiento durante el combate");
        }
        Equipamiento quitado = equipamientos.remove(categoria);
        if (quitado == null) {
            throw new EquipamientoInvalidoException(nombre + " no lleva equipamiento de categoría " + categoria);
        }
        recalcularMaximos();
        vidaActual = Math.min(vidaActual, vidaMaxima);
        energiaActual = Math.min(energiaActual, energiaMaxima);
        return quitado;
    }

    private void recalcularMaximos() {
        vidaMaxima = vidaBase;
        energiaMaxima = energiaBase;
        for (Equipamiento e : equipamientos.values()) {
            vidaMaxima += e.getBonusVidaMaxima();
            energiaMaxima += e.getBonusEnergiaMaxima();
        }
    }

    public void aprenderTecnica(Tecnica tecnica) {
        if (tecnica == null) throw new IllegalArgumentException("La técnica no puede ser nula");
        if (!tecnicas.contains(tecnica)) tecnicas.add(tecnica);
    }

    public List<Tecnica> getTecnicas() { return Collections.unmodifiableList(tecnicas); }

    public int getUsosRealizados(Tecnica tecnica) {
        Integer usos = usosTecnica.get(tecnica);
        return usos == null ? 0 : usos.intValue();
    }

    public int usosRestantes(Tecnica tecnica) {
        return tecnicas.contains(tecnica) ? USOS_POR_TECNICA - getUsosRealizados(tecnica) : 0;
    }

    public int costoEnergia(Tecnica tecnica) { return calcularCostoEnergia(tecnica.getCostoEnergia()); }

    public boolean puedeUsar(Tecnica tecnica) {
        return estaEnPie() && usosRestantes(tecnica) > 0 && energiaActual >= costoEnergia(tecnica);
    }

    public void usarTecnica(Tecnica tecnica, Combatiente objetivo) {
        if (!estaEnPie()) throw new TecnicaNoDisponibleException(nombre + " está fuera de combate");
        if (!tecnicas.contains(tecnica)) {
            throw new TecnicaNoDisponibleException(nombre + " no conoce " + tecnica.getNombre());
        }
        if (usosRestantes(tecnica) <= 0) {
            throw new TecnicaNoDisponibleException(nombre + " agotó los usos de " + tecnica.getNombre());
        }
        if (energiaActual < costoEnergia(tecnica)) {
            throw new TecnicaNoDisponibleException(nombre + " no tiene energía para " + tecnica.getNombre());
        }
        energiaActual -= costoEnergia(tecnica);
        usosTecnica.merge(tecnica, 1, Integer::sum);
        tecnica.ejecutar(this, objetivo);
    }

    public Acontecimiento ejecutarTurno(List<Combatiente> oponentes, Pareja pareja) {
        if (!estaEnPie()) throw new IllegalStateException(nombre + " está fuera de combate");
        if (oponentes.isEmpty()) throw new IllegalArgumentException("No hay oponentes en pie");
        String marca = marcaDeEstado();
        Tecnica tecnica = elegirTecnica(pareja);
        if (tecnica != null) {
            Combatiente objetivo = tecnica.getTipo() == TipoTecnica.OFENSIVA ? elegirObjetivo(oponentes) : this;
            int vidaAntes = objetivo.getVidaActual();
            usarTecnica(tecnica, objetivo);
            pareja.registrarUsoTecnica(this, tecnica);
            String efecto = tecnica.describirEfecto(vidaAntes - objetivo.getVidaActual())
                    + (tecnica.getTipo() == TipoTecnica.OFENSIVA ? marca : "");
            return Acontecimiento.accion(id, nombre, tecnica.getNombre(), true, objetivo.getId(),
                    objetivo.getNombre(), efecto, objetivo.getVidaActual());
        }
        Combatiente objetivo = elegirObjetivo(oponentes);
        int danio = atacarBasico(objetivo);
        return Acontecimiento.accion(id, nombre, "Ataque básico", false, objetivo.getId(), objetivo.getNombre(),
                "causa " + danio + " de daño" + marca, objetivo.getVidaActual());
    }

    protected String marcaDeEstado() { return ""; }

    protected Combatiente elegirObjetivo(List<Combatiente> oponentes) {
        Combatiente elegido = null;
        for (Combatiente c : oponentes) {
            if (!c.estaEnPie()) continue;
            if (elegido == null || c.getVidaActual() < elegido.getVidaActual()
                    || (c.getVidaActual() == elegido.getVidaActual() && c.getId() < elegido.getId())) {
                elegido = c;
            }
        }
        if (elegido == null) throw new IllegalArgumentException("No hay oponentes en pie");
        return elegido;
    }

    protected Tecnica elegirTecnica(Pareja pareja) {
        if (vidaActual < vidaMaxima) {
            Tecnica recuperacion = primeraDisponible(TipoTecnica.RECUPERACION, pareja);
            if (recuperacion != null) return recuperacion;
        }
        if (!tieneDefensaActiva()) {
            Tecnica defensiva = primeraDisponible(TipoTecnica.DEFENSIVA, pareja);
            if (defensiva != null) return defensiva;
        }
        Tecnica elegida = null;
        for (Tecnica t : tecnicas) {
            if (t.getTipo() != TipoTecnica.OFENSIVA || !disponible(t, pareja)) continue;

            if (elegida == null || usosRestantes(t) > usosRestantes(elegida)) elegida = t;
        }
        return elegida;
    }

    private Tecnica primeraDisponible(TipoTecnica tipo, Pareja pareja) {
        for (Tecnica t : tecnicas) {
            if (t.getTipo() == tipo && disponible(t, pareja)) return t;
        }
        return null;
    }

    private boolean disponible(Tecnica tecnica, Pareja pareja) {
        return puedeUsar(tecnica) && pareja.puedeUsarTecnica(this, tecnica);
    }

    public void prepararParaCombate() {
        vidaActual = vidaMaxima;
        energiaActual = energiaMaxima;
        reduccionDanioRecibido = 0;
        enCombate = true;
    }

    public void finalizarCombate() { enCombate = false; }

    public boolean estaEnCombate() { return enCombate; }

    @Override
    public void actualizar(Acontecimiento acontecimiento) { }
}
