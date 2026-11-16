package inmortales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import inmortales.acontecimientos.Acontecimiento;
import inmortales.acontecimientos.TipoAcontecimiento;
import inmortales.combatientes.Combatiente;
import inmortales.combatientes.CreadorCombatientes;
import inmortales.equipamiento.AnilloEspiritual;
import inmortales.equipamiento.ArmaduraDorada;
import inmortales.equipamiento.EspadaCelestial;
import inmortales.equipamiento.SelloDelFenix;
import inmortales.escuelas.Escuela;
import inmortales.tecnicas.CorteDeLosMilAmaneceres;
import inmortales.tecnicas.DominioDeLaNada;
import inmortales.tecnicas.MeditacionDelInmortal;
import inmortales.tecnicas.MuroDelCuerpoDorado;
import inmortales.tecnicas.RugidoDeLaBestiaPrimordial;
import inmortales.torneo.Pareja;

public final class Utiles {
    private Utiles() { }

    public static class CombatienteDePrueba extends Combatiente {
        private final int porcentajeDanio;

        public CombatienteDePrueba(int id, int vida, int energia) {
            this(id, vida, energia, 100);
        }

        public CombatienteDePrueba(int id, int vida, int energia, int porcentajeDanio) {
            super(id, "C" + id, null, vida, energia);
            this.porcentajeDanio = porcentajeDanio;
        }

        @Override
        public String getTipo() { return "De prueba"; }

        @Override
        protected int calcularDanioCausado(int danioBase) { return danioBase * porcentajeDanio / 100; }
    }

    public static CombatienteDePrueba neutro(int id) { return new CombatienteDePrueba(id, 100, 50); }

    public static List<Pareja> parejas(int cantidad) {
        Escuela[] escuelas = {
            new Escuela("Escuela de la Espada Celestial",
                    Arrays.asList(new CorteDeLosMilAmaneceres(), new RugidoDeLaBestiaPrimordial()),
                    Arrays.asList(new EspadaCelestial())),
            new Escuela("Palacio del Vacío",
                    Arrays.asList(new DominioDeLaNada(), new MeditacionDelInmortal()),
                    Arrays.asList(new AnilloEspiritual())),
            new Escuela("Monasterio del Cuerpo Dorado",
                    Arrays.asList(new MuroDelCuerpoDorado(), new MeditacionDelInmortal()),
                    Arrays.asList(new ArmaduraDorada(), new SelloDelFenix()))
        };
        String[] tipos = { "Cultivador de la Espada", "Monje del Cuerpo Celestial", "Hechicero del Vacío" };
        CreadorCombatientes creador = new CreadorCombatientes();
        List<Pareja> parejas = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Combatiente a = creador.crear(tipos[(2 * i) % 3], "A" + i, escuelas[i % 3]);
            Combatiente b = creador.crear(tipos[(2 * i + 1) % 3], "B" + i, escuelas[(i + 1) % 3]);
            parejas.add(new Pareja(i + 1, a, b));
        }
        return parejas;
    }

    public static List<Acontecimiento> deTipo(List<Acontecimiento> historial, TipoAcontecimiento tipo) {
        List<Acontecimiento> r = new ArrayList<>();
        for (Acontecimiento a : historial) if (a.getTipo() == tipo) r.add(a);
        return r;
    }

    public static List<Acontecimiento> acciones(List<Acontecimiento> historial) {
        return deTipo(historial, TipoAcontecimiento.ACCION);
    }

    public static List<Acontecimiento> eliminaciones(List<Acontecimiento> historial) {
        return deTipo(historial, TipoAcontecimiento.ELIMINACION);
    }
}
