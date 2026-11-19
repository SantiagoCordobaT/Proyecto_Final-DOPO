package dominio.efectos;

/**
 * Aturdimiento: el personaje no puede moverse, saltar ni patear durante 3 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class StunEffect extends Effect {

    private static final String NOMBRE = "Aturdido";
    private static final double SEGUNDOS_ATURDIMIENTO = 3.0;

    /** Crea un aturdimiento de 3 segundos. */
    public StunEffect() {
        super(NOMBRE, SEGUNDOS_ATURDIMIENTO);
    }

    @Override
    boolean allowsActions() {
        return false;
    }
}
