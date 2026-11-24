package dominio.efectos;

/**
 * Encoge al jugador a la mitad durante 6 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ShrinkEffect extends Effect {

    public ShrinkEffect() {
        super("Reducido", 6.0);
    }

    @Override
    void aplicarSobre(StatusEffects estado) {
        estado.cambiarEscala(0.5);
    }
}
