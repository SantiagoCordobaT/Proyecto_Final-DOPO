package dominio;

/**
 * Agrupa la configuración completa de un partido (modalidad, tipo y ambos
 * personajes) para iniciarlo en una sola llamada.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class MatchSetup {

    private final int indiceModo;
    private final int indiceTipo;
    private final int indiceJugador1;
    private final int indiceJugador2;

    public MatchSetup(int indiceModo, int indiceTipo, int indiceJugador1, int indiceJugador2) {
        this.indiceModo = indiceModo;
        this.indiceTipo = indiceTipo;
        this.indiceJugador1 = indiceJugador1;
        this.indiceJugador2 = indiceJugador2;
    }

    public int obtenerIndiceModo() {
        return indiceModo;
    }

    public int obtenerIndiceTipo() {
        return indiceTipo;
    }

    public int obtenerIndiceJugador1() {
        return indiceJugador1;
    }

    public int obtenerIndiceJugador2() {
        return indiceJugador2;
    }
}
