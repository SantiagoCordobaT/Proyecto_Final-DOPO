package dominio.efectos;

/**
 * Efecto que reduce a la mitad el tamaño y el hitbox durante 6 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ShrinkEffect extends Effect {

    private static final String NOMBRE = "Reducido";
    private static final double SEGUNDOS_REDUCCION = 6.0;
    private static final double ESCALA_REDUCCION = 0.5;

    public ShrinkEffect() {
        super(NOMBRE, SEGUNDOS_REDUCCION);
    }

    @Override
    double obtenerEscala() {
        return ESCALA_REDUCCION;
    }
}
