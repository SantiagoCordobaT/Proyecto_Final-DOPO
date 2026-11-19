package dominio.efectos;

/**
 * Congelamiento en un bloque de hielo: impide moverse, saltar y patear durante
 * 3 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class FreezeEffect extends Effect {

    private static final String NOMBRE = "Congelado";
    private static final double SEGUNDOS_CONGELAMIENTO = 3.0;

    /** Crea un congelamiento de 3 segundos. */
    public FreezeEffect() {
        super(NOMBRE, SEGUNDOS_CONGELAMIENTO);
    }

    @Override
    boolean permiteAcciones() {
        return false;
    }
}
