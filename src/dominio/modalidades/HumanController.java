package dominio.modalidades;

import dominio.Ball;
import dominio.personajes.Character;

/**
 * Controlador de un jugador humano: cada paso lee las acciones que la
 * presentacion mantiene presionadas (teclado). Recibe el personaje y el balon
 * porque es la firma comun del metodo plantilla, pero no los necesita.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class HumanController extends Controller {

    @Override
    protected int direction(Character self, Ball ball) {
        return heldValue(Action.RIGHT) - heldValue(Action.LEFT);
    }

    @Override
    protected boolean wantsJump(Character self, Ball ball) {
        return isHeld(Action.JUMP);
    }

    @Override
    protected boolean wantsKick(Character self, Ball ball) {
        return isHeld(Action.KICK);
    }

    @Override
    protected boolean wantsPower() {
        return isHeld(Action.POWER);
    }

    /** @return 1 si la accion esta presionada, 0 si no (dos teclas opuestas se cancelan). */
    private int heldValue(Action action) {
        return isHeld(action) ? 1 : 0;
    }
}
