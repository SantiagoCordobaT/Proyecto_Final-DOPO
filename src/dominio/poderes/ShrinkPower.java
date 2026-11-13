package dominio.poderes;

import dominio.efectos.Effect;
import dominio.efectos.ShrinkEffect;

/**
 * Poder de reduccion: encoge al oponente a la mitad de su tamano.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ShrinkPower extends Power {

    /** Crea el poder de reduccion. */
    public ShrinkPower() {
        super("Reducción", "Encoge al oponente a un tamaño diminuto.");
    }

    @Override
    protected Effect createEffect() {
        return new ShrinkEffect();
    }
}
