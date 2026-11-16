package inmortales.torneo;

public class Cruce {
    private final Pareja primera;
    private final Pareja segunda;

    public Cruce(Pareja primera, Pareja segunda) {
        this.primera = primera;
        this.segunda = segunda;
    }

    public Pareja getPrimera() { return primera; }

    public Pareja getSegunda() { return segunda; }
}
