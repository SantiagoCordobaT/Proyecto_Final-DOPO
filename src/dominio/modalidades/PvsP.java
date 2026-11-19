package dominio.modalidades;

/**
 * Jugador vs Jugador: dos humanos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class PvsP extends GameMode {

    /** Crea la modalidad PvsP; solo la crea {@link GameModeCatalog}. */
    PvsP() {
        super("Jugador vs Jugador");
    }

    @Override
    public Controller crearControladorJugador1() {
        return new HumanController();
    }

    @Override
    public Controller crearControladorJugador2() {
        return new HumanController();
    }
}
