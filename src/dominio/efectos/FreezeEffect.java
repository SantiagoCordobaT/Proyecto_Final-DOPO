package dominio.efectos;

/**
 * Efecto de congelamiento que bloquea las acciones durante 3 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class FreezeEffect extends Effect {

    private static final String NOMBRE = "Congelado";
    private static final double SEGUNDOS_CONGELAMIENTO = 3.0;

    public FreezeEffect() {
        super(NOMBRE, SEGUNDOS_CONGELAMIENTO);
    }

    @Override
    boolean permiteAcciones() {
        return false;
    }
}
