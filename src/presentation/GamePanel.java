package presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Pantalla del partido. Un {@link Timer} de Swing hace avanzar el juego con el
 * tiempo real transcurrido (la fachada simula) y vuelve a dibujar: arriba el
 * marcador y debajo la cancha, cada uno como una lista de {@link Drawable}. El
 * teclado llega a la fachada por {@link KeyBindings}; P o Esc pausan el partido.
 * Al terminar el partido muestra el resultado.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class GamePanel extends Screen {

    private static final int FRAME_MILLIS = 16;
    private static final double NANOS_PER_SECOND = 1e9;
    /** Tope del tiempo de un cuadro: una pausa larga de la ventana no hace saltar la fisica. */
    private static final double MAX_FRAME_SECONDS = 0.1;
    private static final int HUD_HEIGHT = 90;
    private static final int HUD_MARGIN = 20;
    private static final Color HUD_BACKGROUND = new Color(20, 30, 60);
    private static final Color PLAYER1_COLOR = new Color(220, 40, 40);
    private static final Color PLAYER2_COLOR = new Color(40, 90, 220);
    private static final Color ERROR_COLOR = Color.WHITE;
    private static final Font ERROR_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    private static final String PAUSE = "pause";

    private final Timer timer = new Timer(FRAME_MILLIS, e -> advance());
    private final List<Drawable> hud;
    private final List<Drawable> scene;
    private long lastNanos;

    /**
     * @param gui Ventana que contiene la pantalla.
     * @param game Fachada del juego: simula el partido y da su estado.
     */
    GamePanel(HeadSoccerGUI gui, HeadSoccer game) {
        super(gui, game);
        int width = (int) game.getFieldWidth();
        setPreferredSize(new Dimension(width, HUD_HEIGHT + (int) game.getGroundY() + FieldDrawing.GRASS_DEPTH));
        setBackground(HUD_BACKGROUND);
        hud = Arrays.asList(
                new PlayerHud(1, HUD_MARGIN, PLAYER1_COLOR),
                new StatusDrawing(),
                new PlayerHud(2, width - HUD_MARGIN - PlayerHud.WIDTH, PLAYER2_COLOR));
        scene = Arrays.asList(
                new FieldDrawing(),
                new PlayerDrawing(1, PLAYER1_COLOR),
                new PlayerDrawing(2, PLAYER2_COLOR),
                new BallDrawing());

        setFocusable(true);
        addKeyListener(new KeyBindings(this, game));
        InputMap pauseKeys = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        pauseKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), PAUSE);
        pauseKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), PAUSE);
        getActionMap().put(PAUSE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame();
            }
        });
    }

    /** Empieza a contar el tiempo desde ahora y arranca el ciclo de juego. */
    @Override
    protected void onShow() {
        lastNanos = System.nanoTime();
        timer.start();
    }

    /**
     * Un cuadro del ciclo de juego: avanza el partido con el tiempo transcurrido y lo
     * dibuja. Al terminar el partido el ciclo se detiene y se muestra el resultado;
     * ante un error del dominio tambien se detiene, y se vuelve a la seleccion de
     * personajes. En pausa el ciclo sigue corriendo, pero el partido no avanza.
     */
    private void advance() {
        long now = System.nanoTime();
        double seconds = Math.min((now - lastNanos) / NANOS_PER_SECOND, MAX_FRAME_SECONDS);
        lastNanos = now;
        try {
            game.updateTick(seconds);
            repaint();
            if (game.isMatchFinished()) {
                timer.stop();
                gui.showResult();
            }
        } catch (POOBSoccerException e) {
            timer.stop();
            report(this, e);
            gui.showCharacterSelect();
        }
    }

    /** Pausa el partido (suelta las teclas mantenidas) y abre la ventana de pausa. */
    private void pauseGame() {
        try {
            game.pause();
            new PauseD(gui, this).setVisible(true);
        } catch (POOBSoccerException e) {
            report(this, e);
        }
    }

    /** Reanuda el partido y recupera el foco del teclado que tenia la ventana de pausa. */
    void resumeGame() {
        try {
            game.resume();
            requestFocusInWindow();
        } catch (POOBSoccerException e) {
            report(this, e);
        }
    }

    /** Abandona el partido: detiene el ciclo y vuelve a la seleccion de personajes. */
    void giveUp() {
        timer.stop();
        gui.showCharacterSelect();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            draw(hud, g);
            g.translate(0, HUD_HEIGHT);
            draw(scene, g);
        } catch (POOBSoccerException e) {
            g.setColor(ERROR_COLOR);
            g.setFont(ERROR_FONT);
            g.drawString(e.getMessage(), HUD_MARGIN, HUD_MARGIN);
        } finally {
            g.dispose();
        }
    }

    private void draw(List<Drawable> parts, Graphics2D g) throws POOBSoccerException {
        for (Drawable part : parts) {
            part.draw(g, game);
        }
    }
}
