package dominio.efectos;

/**
 * Confusion: invierte el movimiento horizontal (izquierda/derecha) durante 5 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ConfusionEffect extends Effect {

    private static final String NOMBRE = "Confundido";
    private static final double SEGUNDOS_CONFUSION = 5.0;

    /** Crea una confusion de 5 segundos. */
    public ConfusionEffect() {
        super(NOMBRE, SEGUNDOS_CONFUSION);
    }

    @Override
    int getHorizontalSign() {
        return -1;
    }
}
