package dominio.modalidades;

/**
 * Jugador vs Jugador: ambos jugadores controlados por teclado.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class PvsP extends GameMode {

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
