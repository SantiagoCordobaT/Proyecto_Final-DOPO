package dominio.efectos;

/**
 * Congelamiento en un bloque de hielo: impide moverse, saltar y patear.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class FreezeEffect extends Effect {

    public static final String NAME = "Congelado";

    /** @param durationSeconds Duracion del congelamiento. */
    public FreezeEffect(double durationSeconds) {
        super(NAME, durationSeconds);
    }

    @Override
    public boolean allowsActions() {
        return false;
    }
}
