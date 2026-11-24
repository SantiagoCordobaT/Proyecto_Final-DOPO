package dominio.modalidades;

/**
 * Máquina vs Máquina: ambos jugadores controlados por IA.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class MvsM extends GameMode {

    MvsM() {
        super("Máquina vs Máquina");
    }

    @Override
    public Controller crearControladorJugador1() {
        return new AIController();
    }

    @Override
    public Controller crearControladorJugador2() {
        return new AIController();
    }
}
