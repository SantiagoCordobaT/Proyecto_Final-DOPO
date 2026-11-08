package dominio.efectos;

/**
 * Reduccion: la escala y el hitbox del personaje se reducen a la mitad.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ShrinkEffect extends Effect {

    public static final String NAME = "Reducido";
    public static final double SHRINK_SCALE = 0.5;

    /** @param durationSeconds Duracion de la reduccion. */
    public ShrinkEffect(double durationSeconds) {
        super(NAME, durationSeconds);
    }

    @Override
    public double getScale() {
        return SHRINK_SCALE;
    }
}
