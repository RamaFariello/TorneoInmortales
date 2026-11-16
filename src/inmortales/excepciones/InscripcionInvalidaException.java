package inmortales.excepciones;

public class InscripcionInvalidaException extends TorneoInmortalesException {
    private static final long serialVersionUID = 1L;

    public InscripcionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
