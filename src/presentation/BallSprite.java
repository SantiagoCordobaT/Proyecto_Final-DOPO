package presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Balon animado: gira segun lo que avanza, deja estela cuando va rapido, se
 * aplasta al botar y proyecta una sombra que se encoge con la altura.
 *
 * Se dibuja en coordenadas nominales (radio 15) y se escala al radio real.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class BallSprite implements Actor {

    private static final double NOMINAL_RADIUS = 15.0;
    private static final int TRAIL = 6;
    private static final double TRAIL_SPEED = 520.0;
    private static final double TELEPORT_PIXELS = 120.0;
    private static final double BOUNCE_SPEED = 160.0;
    private static final double SQUASH_AMOUNT = 0.22;
    private static final double SQUASH_RECOVERY = 14.0;
    private static final double SHADOW_FADE_HEIGHT = 380.0;
    private static final double DUST_HEIGHT = 60.0;
    private static final int DUST_COUNT = 5;
    private static final double DUST_SPEED = 90.0;
    private static final int PATCHES = 5;

    private static final Color LEATHER = new Color(248, 248, 244);
    private static final Color PATCH = new Color(26, 26, 30);
    private static final Color SHADOW = new Color(0, 0, 0, 95);
    private static final Color DUST = new Color(176, 170, 160);
    private static final Color[] TRAIL_COLORS = { new Color(255, 255, 255, 70), new Color(255, 255, 255, 56),
        new Color(255, 255, 255, 42), new Color(255, 255, 255, 30), new Color(255, 255, 255, 20),
        new Color(255, 255, 255, 10) };
    private static final BasicStroke LINE = new BasicStroke(1.4f);
    private static final Ellipse2D SPHERE = new Ellipse2D.Double(-15.0, -15.0, 30.0, 30.0);
    private static final Polygon CENTER_PATCH = new Polygon(new int[] { 0, 5, 3, -3, -5 },
            new int[] { -5, -2, 4, 4, -2 }, 5);
    private static final Polygon EDGE_PATCH = new Polygon(new int[] { -4, 4, 6, -6 }, new int[] { -10, -10, -14, -14 },
            4);
    private static final RadialGradientPaint VOLUME = new RadialGradientPaint(new Point2D.Double(-5.0, -6.0), 22f,
            new float[] { 0f, 0.4f, 1f },
            new Color[] { new Color(255, 255, 255, 150), new Color(255, 255, 255, 0), new Color(0, 0, 0, 120) });

    private final HeadSoccer model;
    private final Particles particles;
    private final double scale;
    private final double ground;
    private final double[] trailX = new double[TRAIL];
    private final double[] trailY = new double[TRAIL];
    private final Ellipse2D.Double shape = new Ellipse2D.Double();

    private double x;
    private double y;
    private double speedY;
    private double speed;
    private double angle;
    private double squash;

    /**
     * @param model Fachada del dominio.
     * @param particles Particulas de la cancha, para el polvo de los botes.
     */
    BallSprite(HeadSoccer model, Particles particles) {
        this.model = model;
        this.particles = particles;
        this.scale = model.getBallRadius() / NOMINAL_RADIUS;
        this.ground = model.getGroundY();
    }

    @Override
    public void update(double seconds) throws POOBSoccerException {
        double[] position = model.getBallPosition();
        double movedX = position[0] - x;
        double movedY = position[1] - y;
        boolean jumped = Math.hypot(movedX, movedY) > TELEPORT_PIXELS || seconds <= 0.0;
        double newSpeedY = jumped ? 0.0 : movedY / seconds;

        for (int i = TRAIL - 1; i > 0; i--) {
            trailX[i] = jumped ? position[0] : trailX[i - 1];
            trailY[i] = jumped ? position[1] : trailY[i - 1];
        }
        trailX[0] = jumped ? position[0] : x;
        trailY[0] = jumped ? position[1] : y;
        if (!jumped) {
            angle += movedX / (NOMINAL_RADIUS * scale);
        }
        if (speedY > BOUNCE_SPEED && newSpeedY < 0.0) {
            squash = 1.0;
            if (position[1] > ground - DUST_HEIGHT) {
                particles.burst(position[0], ground, DUST, DUST_COUNT, DUST_SPEED);
            }
        }
        squash *= Math.exp(-SQUASH_RECOVERY * seconds);
        speed = jumped ? 0.0 : Math.hypot(movedX, movedY) / seconds;
        speedY = newSpeedY;
        x = position[0];
        y = position[1];
    }

    @Override
    public void paint(Graphics2D g) {
        double radius = NOMINAL_RADIUS * scale;
        double fade = Math.max(0.3, 1.0 - (ground - radius - y) / SHADOW_FADE_HEIGHT);
        shape.setFrame(x - radius * fade, ground - 2.0, 2.0 * radius * fade, 8.0);
        g.setColor(SHADOW);
        g.fill(shape);
        if (speed > TRAIL_SPEED) {
            for (int i = TRAIL - 1; i >= 0; i--) {
                double size = radius * (1.0 - i / (double) (TRAIL + 1));
                shape.setFrame(trailX[i] - size, trailY[i] - size, 2.0 * size, 2.0 * size);
                g.setColor(TRAIL_COLORS[i]);
                g.fill(shape);
            }
        }

        AffineTransform world = g.getTransform();
        g.translate(x, y);
        g.scale(scale * (1.0 + squash * SQUASH_AMOUNT), scale * (1.0 - squash * SQUASH_AMOUNT));
        AffineTransform upright = g.getTransform();
        g.rotate(angle);
        g.setColor(LEATHER);
        g.fill(SPHERE);
        g.setColor(PATCH);
        g.fill(CENTER_PATCH);
        for (int i = 0; i < PATCHES; i++) {
            g.fill(EDGE_PATCH);
            g.rotate(2.0 * Math.PI / PATCHES);
        }
        g.setTransform(upright);
        g.setPaint(VOLUME);
        g.fill(SPHERE);
        g.setColor(PATCH);
        g.setStroke(LINE);
        g.draw(SPHERE);
        g.setTransform(world);
    }
}
