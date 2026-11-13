package dominio.efectos;

/**
 * Congelamiento en un bloque de hielo: impide moverse, saltar y patear durante 3 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class FreezeEffect extends Effect {

    public static final String NAME = "Congelado";
    private static final double FREEZE_SECONDS = 3.0;

    /** Crea un congelamiento de 3 segundos. */
    public FreezeEffect() {
        super(NAME, FREEZE_SECONDS);
    }

    @Override
    boolean allowsActions() {
        return false;
    }
}
