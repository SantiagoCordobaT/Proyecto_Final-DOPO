package dominio.modalidades;

import java.util.EnumSet;
import java.util.Set;

import dominio.Ball;
import dominio.personajes.Character;

/**
 * Control de un personaje (metodo plantilla). En cada tick {@link #control}
 * sigue siempre los mismos pasos: direccion, salto, patada y poder; cada
 * subclase decide los pasos (Polymorphism), asi nunca se pregunta si el jugador
 * es humano o maquina.
 * <p>
 * Las acciones mantenidas ({@link #press}/{@link #release}) son el contrato de
 * entrada de todo controlador: la fachada se las entrega a cualquier jugador y
 * {@link AIController} las ignora a proposito, por eso el teclado no mueve a la maquina.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Controller {

    private final Set<Action> mantenidas = EnumSet.noneOf(Action.class);

    /** @param accion Accion que empieza a mantenerse presionada. */
    public final void press(Action accion) {
        mantenidas.add(accion);
    }

    /** @param accion Accion que deja de mantenerse presionada. */
    public final void release(Action accion) {
        mantenidas.remove(accion);
    }

    /** Suelta todas las acciones mantenidas (por ejemplo, al pausar). */
    public final void releaseAll() {
        mantenidas.clear();
    }

    /**
     * @param accion Accion consultada.
     * @return true si la accion esta presionada.
     */
    protected final boolean isHeld(Action accion) {
        return mantenidas.contains(accion);
    }

    /**
     * Decide las intenciones del personaje para este tick, antes de actualizar la fisica.
     *
     * @param personaje Personaje controlado.
     * @param balon     Balon.
     */
    public final void control(Character personaje, Ball balon) {
        personaje.setMoveDirection(direction(personaje, balon));
        if (wantsJump(personaje, balon)) {
            personaje.jump();
        }
        if (wantsKick(personaje, balon)) {
            personaje.requestKick();
        }
        if (wantsPower()) {
            personaje.armPower();
        }
    }

    /**
     * @param personaje Personaje controlado.
     * @param balon     Balon.
     * @return -1 izquierda, 0 quieto, 1 derecha.
     */
    protected abstract int direction(Character personaje, Ball balon);

    /**
     * @param personaje Personaje controlado.
     * @param balon     Balon.
     * @return true si el personaje intenta saltar.
     */
    protected abstract boolean wantsJump(Character personaje, Ball balon);

    /**
     * @param personaje Personaje controlado.
     * @param balon     Balon.
     * @return true si el personaje pide patear.
     */
    protected abstract boolean wantsKick(Character personaje, Ball balon);

    /** @return true si el personaje intenta armar su poder. */
    protected abstract boolean wantsPower();
}
