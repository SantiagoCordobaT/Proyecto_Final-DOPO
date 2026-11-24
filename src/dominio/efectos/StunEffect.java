package dominio.efectos;

/**
 * Aturdimiento: impide moverse, saltar y patear durante 3 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class StunEffect extends Effect {

    private static final String NOMBRE = "Aturdido";
    private static final double SEGUNDOS_ATURDIMIENTO = 3.0;

    public StunEffect() {
        super(NOMBRE, SEGUNDOS_ATURDIMIENTO);
    }

    @Override
    boolean permiteAcciones() {
        return false;
    }
}
