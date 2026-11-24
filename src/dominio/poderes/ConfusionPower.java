package dominio.poderes;

import dominio.efectos.ConfusionEffect;
import dominio.personajes.Character;

/**
 * Poder de confusión: invierte los controles horizontales del oponente.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ConfusionPower extends Power {

    public ConfusionPower() {
        super("Confusión", "Invierte los controles izquierda/derecha del oponente.");
    }

    @Override
    public void aplicarEfecto(Character oponente) {
        oponente.agregarEfecto(new ConfusionEffect());
    }
}
