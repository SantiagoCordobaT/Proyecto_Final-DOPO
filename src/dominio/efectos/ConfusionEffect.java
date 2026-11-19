package dominio.efectos;

/**
 * Confusion: invierte el movimiento horizontal (izquierda/derecha) durante 5 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ConfusionEffect extends Effect {

    private static final String NAME = "Confundido";
    private static final double CONFUSION_SECONDS = 5.0;

    /** Crea una confusion de 5 segundos. */
    public ConfusionEffect() {
        super(NAME, CONFUSION_SECONDS);
    }

    @Override
    int getHorizontalSign() {
        return -1;
    }
}
