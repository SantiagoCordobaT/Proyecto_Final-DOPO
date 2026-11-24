package dominio.efectos;

/**
 * Invierte izquierda y derecha durante 5 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ConfusionEffect extends Effect {

    public ConfusionEffect() {
        super("Confundido", 5.0);
    }

    @Override
    void aplicarSobre(StatusEffects estado) {
        estado.invertirMovimiento();
    }
}
