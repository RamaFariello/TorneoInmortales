package inmortales.presentacion;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.ObservadorTorneo;
import inmortales.acontecimientos.TipoAcontecimiento;

public class HeraldoImperial implements ObservadorTorneo {
    private final PrintStream salida;
    private final boolean mostrarAcciones;

    public HeraldoImperial() {
        this(true);
    }

    public HeraldoImperial(boolean mostrarAcciones) {
        this(new PrintStream(System.out, true, StandardCharsets.UTF_8), mostrarAcciones);
    }

    public HeraldoImperial(PrintStream salida, boolean mostrarAcciones) {
        this.salida = salida;
        this.mostrarAcciones = mostrarAcciones;
    }

    @Override
    public void actualizar(Acontecimiento a) {
        switch (a.getTipo()) {
            case INICIO_COMBATE:
                salida.println("--- Ronda " + a.getRonda() + ": " + a.getDescripcion());
                break;
            case ACCION:
                if (mostrarAcciones) salida.println("    [Asalto " + a.getAsalto() + "] " + a.getDescripcion());
                break;
            case ELIMINACION:
                salida.println("    [Asalto " + a.getAsalto() + "] " + a.getDescripcion());
                break;
            case RESULTADO_COMBATE:
                salida.println("    " + a.getDescripcion());
                break;
            case PROCLAMACION:
                salida.println();
                salida.println("*** " + a.getDescripcion() + " ***");
                break;
            default:
                salida.println(a.getDescripcion());
        }
    }

    public void anunciar(String texto) { salida.println(texto); }

    public void mostrarTablaPosiciones(List<RegistroPosicion> tabla) {
        salida.println();
        salida.println("=== TABLA DE POSICIONES ===");
        salida.println(String.format("%-4s %-45s %-8s %s", "Pos", "Pareja", "Ronda", "Vida total"));
        int posicion = 1;
        for (RegistroPosicion r : tabla) {
            salida.println(String.format("%-4d %-45s %-8d %d", posicion++, r.getPareja(), r.getUltimaRonda(),
                    r.getVidaTotalFinal()));
        }
    }
}
