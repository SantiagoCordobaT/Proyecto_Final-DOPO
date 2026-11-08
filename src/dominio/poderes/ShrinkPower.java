package dominio.poderes;

import dominio.Ball;
import dominio.efectos.ShrinkEffect;
import dominio.personajes.Character;

/**
 * Poder de reduccion: encoge al oponente a la mitad de su tamano.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ShrinkPower extends Power {

    public static final double SHRINK_SECONDS = 6.0;

    /** Crea el poder de reduccion. */
    public ShrinkPower() {
        super("Reduccion", "Encoge al oponente a un tamaño diminuto.", SHRINK_SECONDS);
    }

    @Override
    public void applyEffect(Character opponent, Ball ball) {
        opponent.addEffect(new ShrinkEffect(getDurationSeconds()));
    }
}
