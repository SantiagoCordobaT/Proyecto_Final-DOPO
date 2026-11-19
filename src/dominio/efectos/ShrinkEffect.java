package dominio.efectos;

/**
 * Reduccion: la escala y el hitbox del personaje se reducen a la mitad durante 6 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ShrinkEffect extends Effect {

    private static final String NOMBRE = "Reducido";
    private static final double SEGUNDOS_REDUCCION = 6.0;
    private static final double ESCALA_REDUCCION = 0.5;

    /** Crea una reduccion de 6 segundos. */
    public ShrinkEffect() {
        super(NOMBRE, SEGUNDOS_REDUCCION);
    }

    @Override
    double getScale() {
        return ESCALA_REDUCCION;
    }
}
