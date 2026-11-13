package dominio.modalidades;

import dominio.Ball;
import dominio.personajes.Character;

/**
 * Estrategia de control de un personaje (Strategy). Evita preguntar si el
 * jugador es humano o maquina.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public interface Controller {

    /**
     * Se invoca en cada tick antes de actualizar la fisica.
     *
     * @param self Personaje controlado.
     * @param ball Balon.
     */
    void control(Character self, Ball ball);
}
