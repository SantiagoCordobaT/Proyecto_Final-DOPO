package dominio.poderes;

import dominio.efectos.ShrinkEffect;
import dominio.personajes.Character;

/**
 * Poder de reducción: encoge al oponente a la mitad de su tamaño.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ShrinkPower extends Power {

    public ShrinkPower() {
        super("Reducción", "Encoge al oponente a un tamaño diminuto.");
    }

    @Override
    public void aplicarEfecto(Character oponente) {
        oponente.agregarEfecto(new ShrinkEffect());
    }
}
