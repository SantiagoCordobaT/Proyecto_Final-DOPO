package presentation;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Controles del teclado compartido durante el partido: traduce cada tecla presionada
 * o soltada a la {@link PlayerKey} que le corresponde, segun la tabla de controles.
 * Una tabla en vez de un switch de teclas; las teclas sin accion se ignoran. La tecla
 * se distingue tambien por su ubicacion, para que solo el Ctrl derecho sea el poder
 * del jugador 2.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class KeyBindings extends KeyAdapter {

    /** Mayor que cualquier ubicacion de tecla (KeyEvent.KEY_LOCATION_*). */
    private static final int LOCATION_SPAN = 8;
    /** Acciones de cada jugador, en el orden en que {@link #bindPlayer} recibe sus teclas. */
    private static final String[] ACTIONS = { "LEFT", "RIGHT", "JUMP", "KICK", "POWER" };

    private final Map<Integer, PlayerKey> keys = new HashMap<Integer, PlayerKey>();
    private final Component owner;
    private final HeadSoccer game;

    /**
     * Crea la tabla de controles: el jugador 1 usa A, D, W, S y Q; el jugador 2 las
     * flechas y el Ctrl derecho.
     *
     * @param owner Componente sobre el que se informan los errores.
     * @param game Fachada del juego que recibe las acciones.
     */
    KeyBindings(Component owner, HeadSoccer game) {
        this.owner = owner;
        this.game = game;
        bindPlayer(1, standard(KeyEvent.VK_A), standard(KeyEvent.VK_D), standard(KeyEvent.VK_W),
                standard(KeyEvent.VK_S), standard(KeyEvent.VK_Q));
        bindPlayer(2, standard(KeyEvent.VK_LEFT), standard(KeyEvent.VK_RIGHT), standard(KeyEvent.VK_UP),
                standard(KeyEvent.VK_DOWN), slot(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        forward(e, PlayerKey::press);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        forward(e, PlayerKey::release);
    }

    private void forward(KeyEvent e, KeyChange change) {
        PlayerKey key = keys.get(slot(e.getKeyCode(), e.getKeyLocation()));
        if (key != null) {
            try {
                change.apply(key, game);
            } catch (POOBSoccerException ex) {
                Screen.report(owner, ex);
            }
        }
    }

    private void bindPlayer(int player, int... slots) {
        for (int i = 0; i < ACTIONS.length; i++) {
            keys.put(slots[i], new PlayerKey(player, ACTIONS[i]));
        }
    }

    private static int standard(int keyCode) {
        return slot(keyCode, KeyEvent.KEY_LOCATION_STANDARD);
    }

    private static int slot(int keyCode, int location) {
        return keyCode * LOCATION_SPAN + location;
    }

    /** Lo que un evento de teclado hace con la tecla del jugador: presionarla o soltarla. */
    private interface KeyChange {
        void apply(PlayerKey key, HeadSoccer game) throws POOBSoccerException;
    }
}
