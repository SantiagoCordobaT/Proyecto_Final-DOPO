package dominio.modalidades;

/**
 * Jugador vs Jugador: dos humanos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class PvsP extends GameMode {

    /** Crea la modalidad PvsP. */
    public PvsP() {
        super("Jugador vs Jugador");
    }

    @Override
    public Controller createPlayer1Controller() {
        return new HumanController();
    }

    @Override
    public Controller createPlayer2Controller() {
        return new HumanController();
    }
}
