package presentation;

import static presentation.Texts.text;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Celebracion de gol: cuando el marcador cambia entre dos cuadros muestra el
 * cartel de gol, lanza papelitos, sacude la red del arco donde entro el balon y
 * hace temblar la camara un instante. No decide si hubo gol: solo reacciona al
 * marcador que informa la fachada.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class GoalCelebration implements Actor {

    private static final int UNKNOWN_SCORE = -1;
    private static final double SECONDS = 1.5;
    private static final double BANNER_Y = 0.36;
    private static final double POP = 6.0;
    private static final double SHAKE_PIXELS = 7.0;
    private static final double SHAKE_SPEED = 70.0;
    private static final int CONFETTI_BURSTS = 6;
    private static final int CONFETTI_COUNT = 14;
    private static final double CONFETTI_SPEED = 420.0;
    private static final FontRenderContext RENDER = new FontRenderContext(null, true, true);
    private static final BasicStroke OUTLINE = new BasicStroke(9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final BasicStroke EDGE = new BasicStroke(1.5f);
    private static final GradientPaint FILL = new GradientPaint(0f, -60f, Resources.READY, 0f, 0f, Resources.ACCENT);

    private final HeadSoccer model;
    private final Particles particles;
    private final GoalSprite goals;
    private final double width;
    private final double ground;
    private final Color[] confetti;
    private Shape banner = new Rectangle2D.Double();
    private int score = UNKNOWN_SCORE;
    private double ballX;
    private double time;

    /**
     * @param model Fachada del dominio.
     * @param particles Particulas de la cancha, para los papelitos.
     * @param goals Arcos, para sacudir la red donde entro el balon.
     */
    GoalCelebration(HeadSoccer model, Particles particles, GoalSprite goals) {
        Resources resources = Resources.getInstance();
        this.model = model;
        this.particles = particles;
        this.goals = goals;
        this.width = model.getFieldWidth();
        this.ground = model.getGroundY();
        this.confetti = new Color[] { resources.playerColor(1), resources.playerColor(2), Resources.READY,
            Resources.TEXT };
    }

    /** Olvida el marcador conocido: el primer cuadro de un partido nuevo no es un gol. */
    void forget() {
        score = UNKNOWN_SCORE;
        time = 0.0;
    }

    /** @return Desplazamiento horizontal de la camara por el temblor del gol. */
    double getShake() {
        double strength = time / SECONDS;
        return SHAKE_PIXELS * strength * strength * Math.sin(time * SHAKE_SPEED);
    }

    @Override
    public void update(double seconds) throws POOBSoccerException {
        int current = model.getPlayerGoals(1) + model.getPlayerGoals(2);
        if (score != UNKNOWN_SCORE && current != score) {
            celebrate();
        }
        score = current;
        ballX = model.getBallPosition()[0];
        time = Math.max(0.0, time - seconds);
    }

    private void celebrate() {
        time = SECONDS;
        goals.shakeNear(ballX);
        Shape outline = Resources.TITLE_FONT.createGlyphVector(RENDER, text("match.goal")).getOutline();
        Rectangle2D box = outline.getBounds2D();
        banner = AffineTransform.getTranslateInstance(-box.getCenterX(), -box.getMaxY()).createTransformedShape(outline);
        for (int i = 0; i < CONFETTI_BURSTS; i++) {
            particles.burst(width * (i + 0.5) / CONFETTI_BURSTS, ground * BANNER_Y, confetti[i % confetti.length],
                    CONFETTI_COUNT, CONFETTI_SPEED);
        }
    }

    @Override
    public void paint(Graphics2D g) {
        if (time > 0.0) {
            double progress = 1.0 - time / SECONDS;
            double size = Math.min(1.0, progress * POP) * Math.min(1.0, (1.0 - progress) * POP);
            AffineTransform world = g.getTransform();
            g.translate(width / 2.0, ground * BANNER_Y);
            g.scale(size, size);
            g.setColor(Color.BLACK);
            g.setStroke(OUTLINE);
            g.draw(banner);
            g.setPaint(FILL);
            g.fill(banner);
            g.setColor(Color.WHITE);
            g.setStroke(EDGE);
            g.draw(banner);
            g.setTransform(world);
        }
    }
}
