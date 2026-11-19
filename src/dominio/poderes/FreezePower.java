package dominio.poderes;

import dominio.efectos.Effect;
import dominio.efectos.FreezeEffect;

/**
 * Poder de Canada: congela al oponente en un bloque de hielo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class FreezePower extends Power {

    /** Crea el poder de congelamiento. */
    public FreezePower() {
        super("Congelamiento", "Congela al oponente en un bloque de hielo.");
    }

    @Override
    protected Effect createEffect() {
        return new FreezeEffect();
    }
}
