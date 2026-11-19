package dominio.poderes;

import dominio.efectos.Effect;
import dominio.efectos.StunEffect;

/**
 * Poder de aturdimiento (Japon): deja al oponente aturdido, sin moverse,
 * saltar ni patear durante 3 segundos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class StunPower extends Power {

    /** Crea el poder de aturdimiento. */
    public StunPower() {
        super("Aturdimiento", "Aturde al oponente: no puede moverse, saltar ni patear.");
    }

    @Override
    protected Effect createEffect() {
        return new StunEffect();
    }
}
