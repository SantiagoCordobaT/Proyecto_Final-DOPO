package dominio.poderes;

import dominio.efectos.FreezeEffect;
import dominio.personajes.Character;

/**
 * Poder que congela al oponente en un bloque de hielo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class FreezePower extends Power {

    public FreezePower() {
        super("Congelamiento", "Congela al oponente en un bloque de hielo.");
    }

    @Override
    public void aplicarEfecto(Character oponente) {
        oponente.agregarEfecto(new FreezeEffect());
    }
}
