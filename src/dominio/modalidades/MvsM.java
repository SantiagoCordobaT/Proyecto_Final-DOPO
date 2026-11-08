package dominio.modalidades;

/**
 * Maquina vs Maquina: ambos jugadores usan IA.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class MvsM extends GameMode {

    /** Crea la modalidad MvsM. */
    public MvsM() {
        super("Máquina vs Máquina");
    }

    @Override
    public Controller createPlayer1Controller() {
        return new AIController();
    }

    @Override
    public Controller createPlayer2Controller() {
        return new AIController();
    }
}
