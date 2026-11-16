package inmortales.excepciones;

public class EquipamientoInvalidoException extends TorneoInmortalesException {
    private static final long serialVersionUID = 1L;

    public EquipamientoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
