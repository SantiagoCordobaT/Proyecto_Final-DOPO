package dominio.poderes;

import dominio.efectos.StunEffect;
import dominio.personajes.Character;

/**
 * Poder que aturde al oponente durante 3 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class StunPower extends Power {

    public StunPower() {
        super("Aturdimiento", "Aturde al oponente: no puede moverse, saltar ni patear.");
    }

    @Override
    public void aplicarEfecto(Character oponente) {
        oponente.agregarEfecto(new StunEffect());
    }
}
