package dominio.efectos;

/**
 * Aturdimiento: el personaje no puede moverse, saltar ni patear durante 3 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class StunEffect extends Effect {

    public static final String NAME = "Aturdido";
    private static final double STUN_SECONDS = 3.0;

    /** Crea un aturdimiento de 3 segundos. */
    public StunEffect() {
        super(NAME, STUN_SECONDS);
    }

    @Override
    boolean allowsActions() {
        return false;
    }
}
