package inmortales.presentacion;

import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.Interesado;

import java.util.List;

/** Única clase del sistema que escribe en pantalla. */
public class HeraldoImperial implements Interesado {
    private final boolean detallado;

    public HeraldoImperial() { this(true); }

    /** @param detallado si es false, omite acciones y efectos (muestra solo lo principal). */
    public HeraldoImperial(boolean detallado) { this.detallado = detallado; }

    @Override
    public void notificar(Acontecimiento acontecimiento) {
        if (detallado || !acontecimiento.esDetalle()) System.out.println(acontecimiento.describir());
    }

    public void anunciar(String mensaje) { System.out.println(mensaje); }

    public void presentarTabla(List<EntradaTabla> tabla) {
        System.out.println("\n========== TABLA DE POSICIONES ==========");
        System.out.printf("%-4s %-40s %-8s %-6s%n", "Pos", "Pareja", "Ronda", "Vida");
        int puesto = 1;
        for (EntradaTabla fila : tabla) {
            System.out.printf("%-4d %-40s %-8d %-6d%n", puesto++, fila.pareja(), fila.ultimaRonda(), fila.vidaFinal());
        }
        System.out.println("=========================================");
    }
}
