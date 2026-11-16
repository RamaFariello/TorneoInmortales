package inmortales.excepciones;

public class TorneoInmortalesException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TorneoInmortalesException(String mensaje) {
        super(mensaje);
    }
}
