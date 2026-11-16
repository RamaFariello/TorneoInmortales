package inmortales.presentacion;

public class RegistroPosicion {
    private final int idPareja;
    private final String pareja;
    private final int ultimaRonda;
    private final int vidaTotalFinal;

    public RegistroPosicion(int idPareja, String pareja, int ultimaRonda, int vidaTotalFinal) {
        this.idPareja = idPareja;
        this.pareja = pareja;
        this.ultimaRonda = ultimaRonda;
        this.vidaTotalFinal = vidaTotalFinal;
    }

    public int getIdPareja() { return idPareja; }

    public String getPareja() { return pareja; }

    public int getUltimaRonda() { return ultimaRonda; }

    public int getVidaTotalFinal() { return vidaTotalFinal; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RegistroPosicion)) return false;
        RegistroPosicion r = (RegistroPosicion) o;
        return idPareja == r.idPareja && pareja.equals(r.pareja) && ultimaRonda == r.ultimaRonda
                && vidaTotalFinal == r.vidaTotalFinal;
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(idPareja, pareja, ultimaRonda, vidaTotalFinal); }

    @Override
    public String toString() { return pareja + " ronda " + ultimaRonda + " vida " + vidaTotalFinal; }
}
