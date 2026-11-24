package dominio.modalidades;

/**
 * Modalidad de un jugador contra la máquina; única compatible con Supervivencia.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class PvsM extends GameMode {

    PvsM() {
        super("Jugador vs Máquina");
    }

    @Override
    public Controller crearControladorJugador1() {
        return new HumanController();
    }

    @Override
    public Controller crearControladorJugador2() {
        return new AIController();
    }

    @Override
    public boolean admiteSupervivencia() {
        return true;
    }
}
