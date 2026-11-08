package dominio.poderes;

import dominio.Ball;
import dominio.efectos.FreezeEffect;
import dominio.personajes.Character;

/**
 * Poder de Canada: congela al oponente en un bloque de hielo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class FreezePower extends Power {

    public static final double FREEZE_SECONDS = 3.0;

    /** Crea el poder de congelamiento. */
    public FreezePower() {
        super("Congelamiento", "Congela al oponente en un bloque de hielo.", FREEZE_SECONDS);
    }

    @Override
    public void applyEffect(Character opponent, Ball ball) {
        opponent.addEffect(new FreezeEffect(getDurationSeconds()));
    }
}
