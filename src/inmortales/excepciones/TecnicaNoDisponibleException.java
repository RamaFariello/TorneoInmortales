package inmortales.excepciones;

public class TecnicaNoDisponibleException extends TorneoInmortalesException {
    private static final long serialVersionUID = 1L;

    public TecnicaNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
