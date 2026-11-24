package dominio.efectos;

/**
 * Efecto que invierte el movimiento horizontal durante 5 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ConfusionEffect extends Effect {

    private static final String NOMBRE = "Confundido";
    private static final double SEGUNDOS_CONFUSION = 5.0;

    public ConfusionEffect() {
        super(NOMBRE, SEGUNDOS_CONFUSION);
    }

    @Override
    int obtenerSignoHorizontal() {
        return -1;
    }
}
