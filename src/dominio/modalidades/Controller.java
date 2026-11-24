package dominio.modalidades;

import java.util.Arrays;

import dominio.Ball;
import dominio.personajes.Character;

/**
 * Base para los controladores de jugador (humano o máquina). Guarda qué
 * acciones
 * están presionadas y cada subclase decide cómo mover al personaje en el turno.
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
        Arrays.fill(mantenidas, false);
    }

    protected boolean estaPresionada(Action accion) {
        return mantenidas[accion.ordinal()];
    }

    public abstract void controlar(Character personaje, Ball balon);
}
