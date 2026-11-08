package dominio.efectos;

/**
 * Confusion: invierte el movimiento horizontal (izquierda/derecha).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ConfusionEffect extends Effect {

    public static final String NAME = "Confundido";

    /** @param durationSeconds Duracion de la confusion. */
    public ConfusionEffect(double durationSeconds) {
        super(NAME, durationSeconds);
    }

    @Override
    public int getHorizontalSign() {
        return -1;
    }
}
