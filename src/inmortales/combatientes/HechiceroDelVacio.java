package inmortales.combatientes;

import inmortales.tecnicas.Tecnica;

public class HechiceroDelVacio extends Combatiente {
    public static final int ENERGIA_GASTA_PROCENTAJE = 80;

    public HechiceroDelVacio(int id, String nombre){
        super(id, nombre);
    }

    
    @Override 
    public int modificarCostoEnergia(Tecnica tecnica){
        return (tecnica.getCostoEnergia()*ENERGIA_GASTA_PROCENTAJE/100);
    }
}
