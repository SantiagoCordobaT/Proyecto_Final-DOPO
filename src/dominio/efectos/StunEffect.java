package dominio.efectos;

/**
 * Aturde al jugador durante 3 segundos sin dejarlo actuar.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class StunEffect extends Effect {

    public StunEffect() {
        super("Aturdido", 3.0);
    }

    @Override
    void aplicarSobre(StatusEffects estado) {
        estado.bloquear();
    }
}
