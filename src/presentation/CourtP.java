package presentation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Lienzo del partido: compone los actores animados de la cancha (personajes,
 * balon, arcos, particulas y celebracion de gol) sobre el fondo callejero y los
 * pinta en orden, escalados de forma uniforme al espacio disponible. Solo los
 * recorre por la interfaz {@link Actor}; no decide ninguna regla.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CourtP extends JPanel {

    private static final double FADE_SECONDS = 0.6;

    private final Resources resources = Resources.getInstance();
    private final GoalCelebration celebration;
    private final List<Actor> actors;
    private final Dimension size;
    private double fade;

    /** @param model Fachada del dominio. */
    CourtP(HeadSoccer model) {
        Particles particles = new Particles();
        GoalSprite goals = new GoalSprite(model);
        celebration = new GoalCelebration(model, particles, goals);
        actors = Arrays.asList(new PlayerSprite(model, 1, particles), new PlayerSprite(model, 2, particles),
                new BallSprite(model, particles), goals, particles, celebration);
        size = new Dimension((int) model.getFieldWidth(), resources.courtHeight(model.getGroundY()));
        setPreferredSize(size);
        setBackground(Color.BLACK);
    }

    /** Prepara el lienzo para un partido nuevo: entra con un fundido y sin celebraciones pendientes. */
    void enter() {
        fade = FADE_SECONDS;
        celebration.forget();
    }

    /**
     * Avanza la animacion de todos los actores y pide repintar.
     *
     * @param seconds Segundos reales transcurridos desde el cuadro anterior.
     * @throws POOBSoccerException Si no hay un partido iniciado.
     */
    void animate(double seconds) throws POOBSoccerException {
        for (Actor actor : actors) {
            actor.update(seconds);
        }
        fade = Math.max(0.0, fade - seconds);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setComposite(AlphaComposite.SrcOver.derive((float) (1.0 - fade / FADE_SECONDS)));
        double scale = Math.min(getWidth() / (double) size.width, getHeight() / (double) size.height);
        g.translate((getWidth() - size.width * scale) / 2.0 + celebration.getShake(),
                (getHeight() - size.height * scale) / 2.0);
        resources.paintBackdrop(g, (int) Math.round(size.width * scale), (int) Math.round(size.height * scale));
        g.scale(scale, scale);
        for (Actor actor : actors) {
            actor.paint(g);
        }
        g.dispose();
        Toolkit.getDefaultToolkit().sync();
    }
}
