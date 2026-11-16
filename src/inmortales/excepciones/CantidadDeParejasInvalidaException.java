package inmortales.excepciones;

public class CantidadDeParejasInvalidaException extends TorneoInmortalesException {
    private static final long serialVersionUID = 1L;

    public CantidadDeParejasInvalidaException(String mensaje) {
        super(mensaje);
    }
}
