package dominio.modalidades;

import java.util.EnumSet;
import java.util.Set;

import dominio.Ball;
import dominio.personajes.Character;

/**
 * Control de un personaje (metodo plantilla). En cada tick {@link #controlar}
 * sigue siempre los mismos pasos: direccion, salto, patada y poder; cada
 * subclase decide los pasos (Polymorphism), asi nunca se pregunta si el jugador
 * es humano o maquina.
 * <p>
 * Las acciones mantenidas ({@link #presionar}/{@link #soltar}) son el contrato
 * de
 * entrada de todo controlador: la fachada se las entrega a cualquier jugador y
 * {@link AIController} las ignora a proposito, por eso el teclado no mueve a la
 * maquina.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Controller {

    private final Set<Action> mantenidas = EnumSet.noneOf(Action.class);

    /** @param accion Accion que empieza a mantenerse presionada. */
    public final void presionar(Action accion) {
        mantenidas.add(accion);
    }

    /** @param accion Accion que deja de mantenerse presionada. */
    public final void soltar(Action accion) {
        mantenidas.remove(accion);
    }

    /** Suelta todas las acciones mantenidas (por ejemplo, al pausar). */
    public final void soltarTodo() {
        mantenidas.clear();
    }

    /**
     * @param accion Accion consultada.
     * @return true si la accion esta presionada.
     */
    protected final boolean estaPresionada(Action accion) {
        return mantenidas.contains(accion);
    }

    /**
     * Decide las intenciones del personaje para este tick, antes de actualizar la
     * fisica.
     *
     * @param personaje Personaje controlado.
     * @param balon     Balon.
     */
    public final void controlar(Character personaje, Ball balon) {
        personaje.establecerDireccionMovimiento(direccion(personaje, balon));
        if (quiereSaltar(personaje, balon)) {
            personaje.saltar();
        }
        if (quierePatear(personaje, balon)) {
            personaje.solicitarPatada();
        }
        if (quierePoder()) {
            personaje.armarPoder();
        }
    }

    /**
     * @param personaje Personaje controlado.
     * @param balon     Balon.
     * @return -1 izquierda, 0 quieto, 1 derecha.
     */
    protected abstract int direccion(Character personaje, Ball balon);

    /**
     * @param personaje Personaje controlado.
     * @param balon     Balon.
     * @return true si el personaje intenta saltar.
     */
    protected abstract boolean quiereSaltar(Character personaje, Ball balon);

    /**
     * @param personaje Personaje controlado.
     * @param balon     Balon.
     * @return true si el personaje pide patear.
     */
    protected abstract boolean quierePatear(Character personaje, Ball balon);

    /** @return true si el personaje intenta armar su poder. */
    protected abstract boolean quierePoder();
}
