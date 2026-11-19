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
class HumanController extends Controller {

    @Override
    protected int direction(Character personaje, Ball balon) {
        return heldValue(Action.RIGHT) - heldValue(Action.LEFT);
    }

    @Override
    protected boolean wantsJump(Character personaje, Ball balon) {
        return isHeld(Action.JUMP);
    }

    @Override
    protected boolean wantsKick(Character personaje, Ball balon) {
        return isHeld(Action.KICK);
    }

    @Override
    protected boolean wantsPower() {
        return isHeld(Action.POWER);
    }

    /**
     * @return 1 si la accion esta presionada, 0 si no (dos teclas opuestas se
     *         cancelan).
     */
    private int heldValue(Action accion) {
        return isHeld(accion) ? 1 : 0;
    }
}
