package dominio.efectos;

/**
 * Congela al jugador durante 3 segundos impidiendo que se mueva o patee.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class FreezeEffect extends Effect {

    public FreezeEffect() {
        super("Congelado", 3.0);
    }

    @Override
    void aplicarSobre(StatusEffects estado) {
        estado.bloquear();
    }
}
