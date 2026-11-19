package presentation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Timer;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Pantalla del partido: marcador al norte y lienzo de la cancha al
 * centro. Su temporizador entrega el tiempo real a la fachada y repinta; su
 * escuchador de teclado traduce las teclas de los dos jugadores en acciones de
 * la fachada. No contiene reglas del juego.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class MatchP extends JPanel {

    private static final int FRAME_MILLIS = 8;
    private static final double NANOS_PER_SECOND = 1e9;
    // Tope de tiempo por cuadro: una pausa de la ventana no hace saltar la fisica
    private static final double MAX_FRAME_SECONDS = 0.1;
    private static final int LOCATION_SPAN = 8;
    private static final String[] ACTIONS = { "LEFT", "RIGHT", "JUMP", "KICK", "POWER" };

    private final HeadSoccerGUI gui;
    private final HeadSoccer model;
    private final HudP hud;
    private final CourtP court;
    private final Timer timer;
    private final Map<Integer, PlayerKey> keys = new HashMap<Integer, PlayerKey>();
    private long lastNanos;

    /**
     * @param gui Ventana principal.
     * @param model Fachada del dominio.
     */
    MatchP(HeadSoccerGUI gui, HeadSoccer model) {
        super(new BorderLayout());
        this.gui = gui;
        this.model = model;
        hud = new HudP(model);
        court = new CourtP(model);
        add(hud, BorderLayout.NORTH);
        add(court, BorderLayout.CENTER);
        timer = new Timer(FRAME_MILLIS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                advance();
            }
        });
        bindPlayer(1, standard(KeyEvent.VK_A), standard(KeyEvent.VK_D), standard(KeyEvent.VK_W),
                standard(KeyEvent.VK_S), standard(KeyEvent.VK_Q));
        bindPlayer(2, standard(KeyEvent.VK_LEFT), standard(KeyEvent.VK_RIGHT), standard(KeyEvent.VK_UP),
                standard(KeyEvent.VK_DOWN), slot(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT));
        setFocusable(true);
        addKeyListener(new Keyboard());
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                releaseKeys();
            }
        });
    }

    /** Arranca el ciclo del partido ya iniciado en la fachada y toma el teclado. */
    void start() {
        hud.relabel();
        court.enter();
        lastNanos = System.nanoTime();
        timer.start();
        requestFocusInWindow();
    }

    /** Detiene el ciclo del partido. */
    void stop() {
        timer.stop();
    }

    /** Reanuda el partido pausado y recupera el teclado. */
    void resumeGame() {
        try {
            model.resume();
            requestFocusInWindow();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.report(this, e);
        }
    }

    /** Abandona el partido en curso y vuelve al menu. */
    void giveUp() {
        timer.stop();
        gui.showMenu();
    }

    /** Un cuadro: avanza el dominio el tiempo real transcurrido, refresca y detecta el final. */
    private void advance() {
        long now = System.nanoTime();
        double seconds = Math.min((now - lastNanos) / NANOS_PER_SECOND, MAX_FRAME_SECONDS);
        lastNanos = now;
        try {
            model.updateTick(seconds);
            hud.refresh(seconds);
            court.animate(seconds);
            if (model.isMatchFinished()) {
                timer.stop();
                gui.showResult();
            }
        } catch (POOBSoccerException e) {
            timer.stop();
            HeadSoccerGUI.report(this, e);
            gui.showMenu();
        }
    }

    private void pauseGame() {
        try {
            model.pause();
            new PauseD(gui, this).open();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.report(this, e);
        }
    }

    private void releaseKeys() {
        try {
            model.releaseAllActions();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.report(this, e);
        }
    }

    /** Asigna a un jugador, en el orden de {@link #ACTIONS}, las teclas indicadas. */
    private void bindPlayer(int player, int... slots) {
        for (int i = 0; i < ACTIONS.length; i++) {
            keys.put(slots[i], new PlayerKey(player, ACTIONS[i]));
        }
    }

    private static int standard(int keyCode) {
        return slot(keyCode, KeyEvent.KEY_LOCATION_STANDARD);
    }

    /** Combina tecla y ubicacion: distingue el Ctrl derecho del izquierdo. */
    private static int slot(int keyCode, int location) {
        return keyCode * LOCATION_SPAN + location;
    }

    /** Cambio de estado de una tecla de jugador: presionarla o soltarla. */
    private interface KeyChange {
        void apply(PlayerKey key, HeadSoccer model) throws POOBSoccerException;
    }

    /**
     * Escuchador del teclado compartido: P o Esc pausan; las teclas de la tabla se
     * reenvian a la fachada como acciones de su jugador; las demas se ignoran.
     */
    private final class Keyboard extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                pauseGame();
            } else {
                forward(e, PlayerKey::press);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            forward(e, PlayerKey::release);
        }

        private void forward(KeyEvent e, KeyChange change) {
            PlayerKey key = keys.get(slot(e.getKeyCode(), e.getKeyLocation()));
            if (key != null) {
                try {
                    change.apply(key, model);
                } catch (POOBSoccerException ex) {
                    HeadSoccerGUI.report(MatchP.this, ex);
                }
            }
        }
    }
}
