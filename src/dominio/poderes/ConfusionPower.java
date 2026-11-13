package dominio.poderes;

import dominio.efectos.ConfusionEffect;
import dominio.efectos.Effect;

/**
 * Poder de confusion: invierte los controles horizontales del oponente.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ConfusionPower extends Power {

    /** Crea el poder de confusion. */
    public ConfusionPower() {
        super("Confusión", "Invierte los controles izquierda/derecha del oponente.");
    }

    @Override
    protected Effect createEffect() {
        return new ConfusionEffect();
    }
}
