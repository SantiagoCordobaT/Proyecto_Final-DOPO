package dominio.efectos;

/**
 * Reduccion: la escala y el hitbox del personaje se reducen a la mitad durante 6 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ShrinkEffect extends Effect {

    private static final String NAME = "Reducido";
    private static final double SHRINK_SECONDS = 6.0;
    private static final double SHRINK_SCALE = 0.5;

    /** Crea una reduccion de 6 segundos. */
    public ShrinkEffect() {
        super(NAME, SHRINK_SECONDS);
    }

    @Override
    double getScale() {
        return SHRINK_SCALE;
    }
}
