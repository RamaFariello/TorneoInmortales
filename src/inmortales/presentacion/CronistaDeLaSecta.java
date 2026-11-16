package inmortales.presentacion;

import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.Interesado;
import inmortales.acontecimientos.ResultadoCombate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Registra todos los acontecimientos y arma la tabla de posiciones. */
public class CronistaDeLaSecta implements Interesado {
    private final List<Acontecimiento> historial = new ArrayList<>();

    @Override
    public void notificar(Acontecimiento acontecimiento) { historial.add(acontecimiento); }

    public List<Acontecimiento> getHistorial() { return Collections.unmodifiableList(historial); }

    /** Consulta el historial filtrando por clase de acontecimiento. */
    public <T extends Acontecimiento> List<T> consultar(Class<T> clase) {
        return historial.stream().filter(clase::isInstance).map(clase::cast).toList();
    }

    public List<EntradaTabla> tablaDePosiciones() {
        // El último resultado de cada pareja pisa a los anteriores.
        Map<Integer, EntradaTabla> ultimaEntrada = new LinkedHashMap<>();
        for (ResultadoCombate r : consultar(ResultadoCombate.class)) {
            ultimaEntrada.put(r.idPareja1(), new EntradaTabla(r.idPareja1(), r.pareja1(), r.ronda(), r.vidaPareja1()));
            ultimaEntrada.put(r.idPareja2(), new EntradaTabla(r.idPareja2(), r.pareja2(), r.ronda(), r.vidaPareja2()));
        }
        List<EntradaTabla> tabla = new ArrayList<>(ultimaEntrada.values());
        tabla.sort(EntradaTabla.ORDEN);
        return tabla;
    }
}
