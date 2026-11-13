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

    private final Set<Action> held = EnumSet.noneOf(Action.class);

    /** @param action Accion que empieza a mantenerse presionada. */
    public final void press(Action action) {
        held.add(action);
    }

    /** @param action Accion que deja de mantenerse presionada. */
    public final void release(Action action) {
        held.remove(action);
    }

    /** Suelta todas las acciones mantenidas (por ejemplo, al pausar). */
    public final void releaseAll() {
        held.clear();
    }

    /**
     * @param action Accion consultada.
     * @return true si la accion esta presionada.
     */
    protected final boolean isHeld(Action action) {
        return held.contains(action);
    }

    /**
     * Decide las intenciones del personaje para este tick, antes de actualizar la fisica.
     *
     * @param self Personaje controlado.
     * @param ball Balon.
     */
    public final void control(Character self, Ball ball) {
        self.setMoveDirection(direction(self, ball));
        if (wantsJump(self, ball)) {
            self.jump();
        }
        if (wantsKick(self, ball)) {
            self.requestKick();
        }
        if (wantsPower()) {
            self.armPower();
        }
    }

    /**
     * @param self Personaje controlado.
     * @param ball Balon.
     * @return -1 izquierda, 0 quieto, 1 derecha.
     */
    protected abstract int direction(Character self, Ball ball);

    /**
     * @param self Personaje controlado.
     * @param ball Balon.
     * @return true si el personaje intenta saltar.
     */
    protected abstract boolean wantsJump(Character self, Ball ball);

    /**
     * @param self Personaje controlado.
     * @param ball Balon.
     * @return true si el personaje pide patear.
     */
    protected abstract boolean wantsKick(Character self, Ball ball);

    /** @return true si el personaje intenta armar su poder. */
    protected abstract boolean wantsPower();
}
