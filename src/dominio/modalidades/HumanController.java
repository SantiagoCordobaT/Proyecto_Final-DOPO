package dominio.modalidades;

import dominio.Ball;
import dominio.Field;
import dominio.personajes.Character;

/**
 * Controlador de un jugador humano: sus acciones llegan desde la fachada
 * (teclado en la presentacion), por eso aqui no se decide nada.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class HumanController implements Controller {

    @Override
    public void control(Character self, Character opponent, Ball ball, Field field) {
        // Las acciones del humano se reciben externamente.
    }
}
