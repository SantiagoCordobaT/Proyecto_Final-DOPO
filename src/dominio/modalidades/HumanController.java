package dominio.modalidades;

import dominio.Ball;
import dominio.personajes.Character;

/**
 * Controlador de jugador humano según las teclas mantenidas en este turno.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class HumanController extends Controller {

    @Override
    public void controlar(Character personaje, Ball balon) {
        int direccion = 0;
        if (estaPresionada(Action.RIGHT)) {
            direccion++;
        }
        if (estaPresionada(Action.LEFT)) {
            direccion--;
        }
        personaje.establecerDireccionMovimiento(direccion);
        if (estaPresionada(Action.JUMP)) {
            personaje.saltar();
        }
        if (estaPresionada(Action.KICK)) {
            personaje.solicitarPatada();
        }
        if (estaPresionada(Action.POWER)) {
            personaje.armarPoder();
        }
    }
}
