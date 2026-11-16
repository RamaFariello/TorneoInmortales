package inmortales.acontecimientos;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Acontecimiento {
    private final TipoAcontecimiento tipo;
    private final int ronda;
    private final int asalto;
    private final String descripcion;
    private final Map<String, Object> datosExtra;

    public Acontecimiento(TipoAcontecimiento tipo, int ronda, int asalto, String descripcion,
                          Map<String, Object> datosExtra) {
        this.tipo = tipo;
        this.ronda = ronda;
        this.asalto = asalto;
        this.descripcion = descripcion;
        this.datosExtra = Collections.unmodifiableMap(new LinkedHashMap<>(datosExtra));
    }

    public Acontecimiento ubicarEn(int nuevaRonda, int nuevoAsalto) {
        return new Acontecimiento(tipo, nuevaRonda, nuevoAsalto, descripcion, datosExtra);
    }

    public TipoAcontecimiento getTipo() { return tipo; }

    public int getRonda() { return ronda; }

    public int getAsalto() { return asalto; }

    public String getDescripcion() { return descripcion; }

    public Map<String, Object> getDatosExtra() { return datosExtra; }

    public Object getDato(String clave) { return datosExtra.get(clave); }

    public int getDatoInt(String clave) { return ((Integer) datosExtra.get(clave)).intValue(); }

    public String getDatoTexto(String clave) { return (String) datosExtra.get(clave); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Acontecimiento)) return false;
        Acontecimiento otro = (Acontecimiento) o;
        return tipo == otro.tipo && ronda == otro.ronda && asalto == otro.asalto
                && descripcion.equals(otro.descripcion) && datosExtra.equals(otro.datosExtra);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(tipo, ronda, asalto, descripcion, datosExtra);
    }

    @Override
    public String toString() { return tipo + " r" + ronda + " a" + asalto + ": " + descripcion; }

    public static Acontecimiento inicioCombate(int ronda, int idPareja1, String pareja1, int idPareja2, String pareja2) {
        Map<String, Object> d = new LinkedHashMap<>();
        d.put("idPareja1", idPareja1);
        d.put("pareja1", pareja1);
        d.put("idPareja2", idPareja2);
        d.put("pareja2", pareja2);
        return new Acontecimiento(TipoAcontecimiento.INICIO_COMBATE, ronda, 0, pareja1 + " vs " + pareja2, d);
    }

    public static Acontecimiento accion(int idActor, String actor, String accion, boolean esTecnica,
            int idObjetivo, String objetivo, String efecto, int vidaObjetivo) {
		Map<String, Object> d = new LinkedHashMap<>();
		d.put("idActor", idActor);
		d.put("actor", actor);
		d.put("idObjetivo", idObjetivo);
        d.put("actor", actor);
        d.put("accion", accion);
        d.put("esTecnica", esTecnica);
        d.put("objetivo", objetivo);
        d.put("efecto", efecto);
        d.put("vidaObjetivo", vidaObjetivo);
        String que = esTecnica ? "usa " + accion : "realiza un " + accion.toLowerCase();
        String texto = actor + " " + que + " sobre " + objetivo + ": " + efecto
                + " (vida de " + objetivo + ": " + vidaObjetivo + ")";
        return new Acontecimiento(TipoAcontecimiento.ACCION, 0, 0, texto, d);
    }

    public static Acontecimiento eliminacion(int ronda, int asalto, int idCombatiente, String combatiente,
                                             int idPareja, int idCompanero) {
        Map<String, Object> d = new LinkedHashMap<>();
        d.put("idCombatiente", idCombatiente);
        d.put("combatiente", combatiente);
        d.put("idPareja", idPareja);
        d.put("idCompanero", idCompanero);
        return new Acontecimiento(TipoAcontecimiento.ELIMINACION, ronda, asalto,
                combatiente + " queda fuera de combate", d);
    }

    public static Acontecimiento resultadoCombate(int ronda, int asaltos, int idGanadora, String ganadora,
                                                  int vidaGanadora, int idPerdedora, String perdedora, int vidaPerdedora) {
        Map<String, Object> d = new LinkedHashMap<>();
        d.put("asaltos", asaltos);
        d.put("idGanadora", idGanadora);
        d.put("ganadora", ganadora);
        d.put("vidaGanadora", vidaGanadora);
        d.put("idPerdedora", idPerdedora);
        d.put("perdedora", perdedora);
        d.put("vidaPerdedora", vidaPerdedora);
        String texto = "Resultado (ronda " + ronda + ", " + asaltos + " asaltos): gana " + ganadora + " con "
                + vidaGanadora + " de vida total; " + perdedora + " termina con " + vidaPerdedora;
        return new Acontecimiento(TipoAcontecimiento.RESULTADO_COMBATE, ronda, asaltos, texto, d);
    }

    public static Acontecimiento proclamacion(int ronda, int idPareja, String santo1, String santo2) {
        Map<String, Object> d = new LinkedHashMap<>();
        d.put("idPareja", idPareja);
        d.put("santo1", santo1);
        d.put("santo2", santo2);
        return new Acontecimiento(TipoAcontecimiento.PROCLAMACION, ronda, 0,
                "LOS DOS SANTOS SUPREMOS: " + santo1 + " y " + santo2 + " (Pareja " + idPareja + ")", d);
    }
}
