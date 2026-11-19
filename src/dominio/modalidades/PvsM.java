package dominio.modalidades;

/**
 * Jugador vs Maquina: el jugador 1 es humano y el 2 usa IA. Admite Supervivencia.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class PvsM extends GameMode {

    /** Crea la modalidad PvsM; solo la crea {@link GameModeCatalog}. */
    PvsM() {
        super("Jugador vs Máquina");
    }

    @Override
    public Controller createPlayer1Controller() {
        return new HumanController();
    }

    @Override
    public Controller createPlayer2Controller() {
        return new AIController();
    }

    @Override
    public boolean supportsSurvival() {
        return true;
    }
}
