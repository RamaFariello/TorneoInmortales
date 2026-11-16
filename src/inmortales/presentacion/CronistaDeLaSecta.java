package inmortales.presentacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.ObservadorTorneo;
import inmortales.acontecimientos.TipoAcontecimiento;

public class CronistaDeLaSecta implements ObservadorTorneo {
    private final List<Acontecimiento> historial = new ArrayList<>();

    @Override
    public void actualizar(Acontecimiento acontecimiento) {
        historial.add(acontecimiento);
    }

    public List<Acontecimiento> getHistorial() {
        return Collections.unmodifiableList(new ArrayList<>(historial));
    }

    public List<Acontecimiento> getResultados() {
        List<Acontecimiento> resultados = new ArrayList<>();
        for (Acontecimiento a : historial) {
            if (a.getTipo() == TipoAcontecimiento.RESULTADO_COMBATE) resultados.add(a);
        }
        return resultados;
    }

    public List<RegistroPosicion> generarTablaPosiciones() {
        Map<Integer, RegistroPosicion> registros = new LinkedHashMap<>();
        for (Acontecimiento r : getResultados()) {
            registrar(registros, r.getDatoInt("idGanadora"), r.getDatoTexto("ganadora"), r.getRonda(),
                    r.getDatoInt("vidaGanadora"));
            registrar(registros, r.getDatoInt("idPerdedora"), r.getDatoTexto("perdedora"), r.getRonda(),
                    r.getDatoInt("vidaPerdedora"));
        }
        List<RegistroPosicion> tabla = new ArrayList<>(registros.values());
        Collections.sort(tabla, new Comparator<RegistroPosicion>() {
            @Override
            public int compare(RegistroPosicion a, RegistroPosicion b) {
                if (a.getUltimaRonda() != b.getUltimaRonda()) return Integer.compare(b.getUltimaRonda(), a.getUltimaRonda());
                if (a.getVidaTotalFinal() != b.getVidaTotalFinal()) {
                    return Integer.compare(b.getVidaTotalFinal(), a.getVidaTotalFinal());
                }
                return Integer.compare(a.getIdPareja(), b.getIdPareja());
            }
        });
        return tabla;
    }

    private void registrar(Map<Integer, RegistroPosicion> registros, int idPareja, String pareja, int ronda, int vida) {
        RegistroPosicion actual = registros.get(idPareja);
        if (actual == null || ronda >= actual.getUltimaRonda()) {
            registros.put(idPareja, new RegistroPosicion(idPareja, pareja, ronda, vida));
        }
    }
}
