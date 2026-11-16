package inmortales.tecnicas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TecnicaTest {

    @Test
    void unaTecnicaConoceSuNombreSuCostoYSuTipo() {
        Tecnica corte = new CorteDeLosMilAmaneceres();

        assertEquals("Corte de los Mil Amaneceres", corte.getNombre());
        assertEquals(10, corte.getCostoEnergia());
        assertEquals(TipoTecnica.OFENSIVA, corte.getTipo());
    }

    @Test
    void cadaTecnicaTieneElTipoQueLeCorresponde() {
        assertEquals(TipoTecnica.OFENSIVA, new DominioDeLaNada().getTipo());
        assertEquals(TipoTecnica.OFENSIVA, new RugidoDeLaBestiaPrimordial().getTipo());
        assertEquals(TipoTecnica.DEFENSIVA, new MuroDelCuerpoDorado().getTipo());
        assertEquals(TipoTecnica.RECUPERACION, new MeditacionDelInmortal().getTipo());
    }

    @Test
    void dosInstanciasDeLaMismaTecnicaSonIguales() {
        assertEquals(new CorteDeLosMilAmaneceres(), new CorteDeLosMilAmaneceres());
        assertEquals(new CorteDeLosMilAmaneceres().hashCode(), new CorteDeLosMilAmaneceres().hashCode());
    }

    @Test
    void tecnicasDistintasNoSonIguales() {
        assertNotEquals(new CorteDeLosMilAmaneceres(), new DominioDeLaNada());
    }

    @Test
    void unaTecnicaSirveComoClaveDeUnRegistroDeUsos() {
        Map<Tecnica, Integer> usos = new HashMap<>();
        usos.merge(new CorteDeLosMilAmaneceres(), 1, Integer::sum);
        usos.merge(new CorteDeLosMilAmaneceres(), 1, Integer::sum);
        usos.merge(new DominioDeLaNada(), 1, Integer::sum);

        assertEquals(2, usos.size());
        assertEquals(2, usos.get(new CorteDeLosMilAmaneceres()));
    }

    @Test
    void laRepresentacionEnTextoEsElNombre() {
        assertEquals("Muro del Cuerpo Dorado", new MuroDelCuerpoDorado().toString());
    }
}
