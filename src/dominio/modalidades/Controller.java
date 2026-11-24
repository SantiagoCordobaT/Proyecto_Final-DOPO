package dominio.modalidades;

import dominio.Ball;
import dominio.personajes.Character;

/**
 * Clase base para controlar un personaje en cada turno, ya sea por teclado o
 * de forma automática.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Controller {

    private final boolean[] mantenidas = new boolean[Action.values().length];

    public void presionar(Action accion) {
        mantenidas[accion.ordinal()] = true;
    }

    public void soltar(Action accion) {
        mantenidas[accion.ordinal()] = false;
    }

    public void soltarTodo() {
        for (int i = 0; i < mantenidas.length; i++) {
            mantenidas[i] = false;
        }
    }

    protected boolean estaPresionada(Action accion) {
        return mantenidas[accion.ordinal()];
    }

    public abstract void controlar(Character personaje, Ball balon);
}
