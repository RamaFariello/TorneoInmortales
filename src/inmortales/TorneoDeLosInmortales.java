package inmortales;

import java.util.ArrayList;
import java.util.List;

import inmortales.combatientes.Combatiente;
import inmortales.combatientes.CreadorCombatientes;
import inmortales.equipamiento.AnilloEspiritual;
import inmortales.equipamiento.ArmaduraDorada;
import inmortales.equipamiento.EspadaCelestial;
import inmortales.equipamiento.SelloDelFenix;
import inmortales.escuelas.Escuela;
import inmortales.presentacion.CronistaDeLaSecta;
import inmortales.presentacion.HeraldoImperial;
import inmortales.tecnicas.CorteDeLosMilAmaneceres;
import inmortales.tecnicas.DominioDeLaNada;
import inmortales.tecnicas.MeditacionDelInmortal;
import inmortales.tecnicas.MuroDelCuerpoDorado;
import inmortales.tecnicas.RugidoDeLaBestiaPrimordial;
import inmortales.torneo.Pareja;
import inmortales.torneo.Torneo;

import java.util.Arrays;

public class TorneoDeLosInmortales {
	public static void main(String[] args) {
		escenarioCuatroParejas(new HeraldoImperial(true));
		escenarioOchoParejas(new HeraldoImperial(false));
		escenarioReglasRechazadas(new HeraldoImperial(true));
	}

	private static Escuela escuelaDeLaEspada() {
		return new Escuela("Escuela de la Espada Celestial",
				Arrays.asList(new CorteDeLosMilAmaneceres(), new RugidoDeLaBestiaPrimordial()),
				Arrays.asList(new EspadaCelestial()));
	}

	private static Escuela palacioDelVacio() {
		return new Escuela("Palacio del Vacío", Arrays.asList(new DominioDeLaNada(), new MeditacionDelInmortal()),
				Arrays.asList(new AnilloEspiritual()));
	}

	private static Escuela monasterioDelCuerpoDorado() {
		return new Escuela("Monasterio del Cuerpo Dorado",
				Arrays.asList(new MuroDelCuerpoDorado(), new MeditacionDelInmortal()),
				Arrays.asList(new ArmaduraDorada(), new SelloDelFenix()));
	}

	private static void escenarioCuatroParejas(HeraldoImperial heraldo) {
		heraldo.anunciar(
				"##########  ESCENARIO 1: TORNEO DE 4 PAREJAS (detallado, con combatiente reactivo)  ##########");
		Escuela espada = escuelaDeLaEspada();
		Escuela vacio = palacioDelVacio();
		Escuela monasterio = monasterioDelCuerpoDorado();
		CreadorCombatientes creador = new CreadorCombatientes();

		Combatiente aurelio = creador.crear("Cultivador de la Espada", "Aurelio", espada);
		Combatiente nebula = creador.crear("Hechicero del Vacío", "Nébula", vacio);
		Combatiente kael = creador.crear("Monje del Cuerpo Celestial", "Kael", vacio);
		Combatiente darian = creador.crear("Cultivador de la Espada", "Darian", espada);
		Combatiente lysa = creador.crear("Monje del Cuerpo Celestial", "Lysa", monasterio);
		Combatiente orion = creador.crear("Hechicero del Vacío", "Orión", monasterio);

		Combatiente thorn = creador.crear("Hechicero del Vacío", "Thorn", espada);
		Combatiente furia = creador.crear("Combatiente Reactivo", "Furia", espada);

		kael.equipar(new ArmaduraDorada());
		darian.equipar(new ArmaduraDorada());
		thorn.equipar(new AnilloEspiritual());

		Torneo torneo = new Torneo();
		CronistaDeLaSecta cronista = new CronistaDeLaSecta();
		torneo.agregarObservador(heraldo);
		torneo.agregarObservador(cronista);

		torneo.inscribir(new Pareja(1, aurelio, nebula));
		torneo.inscribir(new Pareja(2, kael, darian));
		torneo.inscribir(new Pareja(3, lysa, orion));
		torneo.inscribir(new Pareja(4, furia, thorn));
		torneo.jugarSiguienteRonda();

		heraldo.anunciar("--- Entre rondas: los finalistas intentan equiparse un Sello del Fénix ---");
		for (Pareja finalista : torneo.getParejasVigentes()) {
			final Combatiente c = finalista.getIntegrantes().get(0);
			intentar(heraldo, c.getNombre() + " se equipa un Sello del Fénix", new Runnable() {
				public void run() {
					c.equipar(new SelloDelFenix());
				}
			});
		}
		torneo.iniciar();

		heraldo.mostrarTablaPosiciones(cronista.generarTablaPosiciones());
	}

	private static void escenarioOchoParejas(HeraldoImperial heraldo) {
		heraldo.anunciar("");
		heraldo.anunciar("##########  ESCENARIO 2: TORNEO DE 8 PAREJAS (resumido)  ##########");
		Escuela[] escuelas = { escuelaDeLaEspada(), palacioDelVacio(), monasterioDelCuerpoDorado() };
		String[] tipos = { "Cultivador de la Espada", "Monje del Cuerpo Celestial", "Hechicero del Vacío" };
		String[] nombres = { "Aurelio", "Nébula", "Kael", "Darian", "Lysa", "Orión", "Brisa", "Thorn", "Mira", "Vex",
				"Selene", "Ragnar", "Ilya", "Zhao", "Anya", "Bruno" };

		CreadorCombatientes creador = new CreadorCombatientes();
		List<Combatiente> combatientes = new ArrayList<>();
		for (int i = 0; i < nombres.length; i++) {
			combatientes.add(creador.crear(tipos[i % tipos.length], nombres[i], escuelas[(i / 2) % escuelas.length]));
		}

		Torneo torneo = new Torneo();
		CronistaDeLaSecta cronista = new CronistaDeLaSecta();
		torneo.agregarObservador(heraldo);
		torneo.agregarObservador(cronista);
		for (int i = 0; i < 8; i++) {
			torneo.inscribir(new Pareja(i + 1, combatientes.get(2 * i), combatientes.get(2 * i + 1)));
		}
		torneo.iniciar();
		heraldo.mostrarTablaPosiciones(cronista.generarTablaPosiciones());
	}

	private static void escenarioReglasRechazadas(HeraldoImperial heraldo) {
		heraldo.anunciar("");
		heraldo.anunciar("##########  ESCENARIO 3: REGLAS QUE SE RECHAZAN  ##########");
		CreadorCombatientes creador = new CreadorCombatientes();
		final Combatiente a = creador.crear("Cultivador de la Espada", "Aurelio", escuelaDeLaEspada());
		final Combatiente b = creador.crear("Hechicero del Vacío", "Nébula", palacioDelVacio());
		final Combatiente c = creador.crear("Monje del Cuerpo Celestial", "Kael", monasterioDelCuerpoDorado());
		final Combatiente d = creador.crear("Cultivador de la Espada", "Darian", escuelaDeLaEspada());

		intentar(heraldo, "Kael ya lleva Armadura: se le suma otra armadura (categoría repetida)", new Runnable() {
			public void run() {
				c.equipar(new ArmaduraDorada());
			}
		});
		intentar(heraldo, "Aurelio recibe Armadura y Anillo, y luego un cuarto objeto (Sello)", new Runnable() {
			public void run() {
				a.equipar(new ArmaduraDorada());
				a.equipar(new AnilloEspiritual());
				a.equipar(new SelloDelFenix());
			}
		});

		final Torneo torneo = new Torneo();
		torneo.inscribir(new Pareja(1, a, b));
		intentar(heraldo, "Se inscribe de nuevo la pareja 1", new Runnable() {
			public void run() {
				torneo.inscribir(new Pareja(1, c, d));
			}
		});
		intentar(heraldo, "Se inscribe a Aurelio en otra pareja", new Runnable() {
			public void run() {
				torneo.inscribir(new Pareja(2, a, c));
			}
		});
		intentar(heraldo, "Se inicia el torneo con 1 sola pareja", new Runnable() {
			public void run() {
				torneo.iniciar();
			}
		});
	}

	private static void intentar(HeraldoImperial heraldo, String situacion, Runnable accion) {
		try {
			accion.run();
			heraldo.anunciar(" - " + situacion + " -> (aceptado)");
		} catch (RuntimeException e) {
			heraldo.anunciar(" - " + situacion + " -> RECHAZADO: " + e.getMessage());
		}
	}
}
