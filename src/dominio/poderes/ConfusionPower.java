package dominio.poderes;

import dominio.Ball;
import dominio.efectos.ConfusionEffect;
import dominio.personajes.Character;

/**
 * Poder de confusion: invierte los controles horizontales del oponente.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ConfusionPower extends Power {

    public static final double CONFUSION_SECONDS = 5.0;

    /** Crea el poder de confusion. */
    public ConfusionPower() {
        super("Confusion", "Invierte los controles izquierda/derecha del oponente.", CONFUSION_SECONDS);
    }

    @Override
    public void applyEffect(Character opponent, Ball ball) {
        opponent.addEffect(new ConfusionEffect(getDurationSeconds()));
    }
}
