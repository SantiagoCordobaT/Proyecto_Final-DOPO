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
    protected int direccion(Character personaje, Ball balon) {
        return valorPresionada(Action.RIGHT) - valorPresionada(Action.LEFT);
    }

    @Override
    protected boolean quiereSaltar(Character personaje, Ball balon) {
        return estaPresionada(Action.JUMP);
    }

    @Override
    protected boolean quierePatear(Character personaje, Ball balon) {
        return estaPresionada(Action.KICK);
    }

    @Override
    protected boolean quierePoder() {
        return estaPresionada(Action.POWER);
    }

    /**
     * @return 1 si la accion esta presionada, 0 si no (dos teclas opuestas se
     *         cancelan).
     */
    private int valorPresionada(Action accion) {
        return estaPresionada(accion) ? 1 : 0;
    }
}
