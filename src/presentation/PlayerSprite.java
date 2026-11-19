package presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Personaje de Head Soccer animado: cabeza grande, torso corto y zapatos pegados
 * al cuerpo (sin piernas ni manos). Deduce su animacion de lo que cambia entre
 * cuadros (corre, salta, cae) y de lo que informa la fachada (patada, poder
 * armado, efectos). No decide ninguna regla.
 *
 * Se dibuja en coordenadas nominales (origen en los pies, 60 de ancho por 90 de
 * alto) y se escala al tamano real, asi el efecto de reduccion encoge todo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class PlayerSprite implements Actor {

    private static final double NOMINAL_WIDTH = 60.0;
    private static final double RUN_RADIANS_PER_PIXEL = 0.13;
    private static final double TELEPORT_PIXELS = 120.0;
    private static final double LEAN_SPEED = 600.0;
    private static final double MAX_LEAN = 0.10;
    private static final double KICK_SECONDS = 0.22;
    private static final double KICK_REACH = 26.0;
    private static final double KICK_LIFT = 13.0;
    private static final double KICK_LEAN = 0.14;
    private static final double SQUASH_ON_LANDING = 0.9;
    private static final double STRETCH_ON_JUMP = -0.7;
    private static final double SQUASH_AMOUNT = 0.14;
    private static final double SQUASH_RECOVERY = 9.0;
    private static final double BLINK_EVERY = 3.1;
    private static final double BLINK_SECONDS = 0.12;
    private static final double AURA_SPEED = 7.0;
    private static final double AURA_SPARK_EVERY = 0.07;
    private static final double SHADOW_FADE_HEIGHT = 260.0;
    private static final double STRIDE_SPEED = 60.0;
    private static final double STEP_REACH = 7.0;
    private static final double STEP_LIFT = 5.0;
    private static final double BOB = 1.3;
    private static final double LOOK = 2.6;
    private static final double FACE_SHIFT = 5.0;
    private static final int DUST_COUNT = 9;
    private static final double DUST_SPEED = 130.0;
    private static final int SPARK_COUNT = 7;
    private static final double SPARK_SPEED = 240.0;
    private static final double AURA_SPARK_SPEED = 90.0;

    private static final Color SKIN = new Color(246, 205, 160);
    private static final Color SKIN_SHADE = new Color(214, 166, 124);
    private static final Color HAIR = new Color(40, 28, 22);
    private static final Color OUTLINE = new Color(20, 16, 14);
    private static final Color SHOE = new Color(28, 28, 32);
    private static final Color SHOE_SHINE = new Color(255, 255, 255, 120);
    private static final Color SHADOW = new Color(0, 0, 0, 95);
    private static final Color DUST = new Color(176, 170, 160);
    private static final Color[] AURA = { new Color(255, 232, 80, 38), new Color(255, 232, 80, 60),
        new Color(255, 244, 170, 85) };
    private static final BasicStroke LINE = new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final BasicStroke RING = new BasicStroke(3f);

    private static final Ellipse2D HEAD = new Ellipse2D.Double(-30.0, -90.0, 60.0, 60.0);
    private static final Arc2D HAIR_CAP = new Arc2D.Double(-30.0, -90.0, 60.0, 60.0, 18.0, 144.0, Arc2D.CHORD);
    private static final Rectangle2D HEADBAND = new Rectangle2D.Double(-26.5, -73.0, 53.0, 5.5);
    private static final RoundRectangle2D TORSO = new RoundRectangle2D.Double(-17.0, -38.0, 34.0, 30.0, 12.0, 12.0);
    private static final Rectangle2D STRIPE = new Rectangle2D.Double(-4.0, -38.0, 8.0, 30.0);
    private static final RadialGradientPaint HEAD_VOLUME = new RadialGradientPaint(new Point2D.Double(-9.0, -72.0),
            48f, new float[] { 0f, 0.35f, 1f },
            new Color[] { new Color(255, 255, 255, 80), new Color(255, 255, 255, 0), new Color(0, 0, 0, 75) });
    private static final GradientPaint TORSO_VOLUME = new GradientPaint(-17f, 0f, new Color(255, 255, 255, 40), 17f,
            0f, new Color(0, 0, 0, 80));

    private final HeadSoccer model;
    private final int number;
    private final Particles particles;
    private final EffectOverlay overlay = new EffectOverlay();
    private final Color ring;
    private final double ground;
    private final Ellipse2D.Double shape = new Ellipse2D.Double();
    private final Line2D.Double stroke = new Line2D.Double();
    private final Arc2D.Double mouth = new Arc2D.Double();

    private Color[] team;
    private String[] effects = new String[0];
    private double centerX;
    private double bottom;
    private double scale = 1.0;
    private int facing = 1;
    private boolean armed;
    private boolean kicking;
    private boolean airborne;
    private double speedX;
    private double runPhase;
    private double squash;
    private double kickTime;
    private double clock;
    private double sparkTime;
    private double lookX;
    private double lookY;

    /**
     * @param model Fachada del dominio.
     * @param number Jugador (1 o 2) que representa.
     * @param particles Particulas de la cancha, para el polvo y las chispas.
     */
    PlayerSprite(HeadSoccer model, int number, Particles particles) {
        Resources resources = Resources.getInstance();
        this.model = model;
        this.number = number;
        this.particles = particles;
        this.ring = resources.playerColor(number);
        this.ground = model.getGroundY();
        this.team = resources.teamColors("");
        this.clock = number;
    }

    @Override
    public void update(double seconds) throws POOBSoccerException {
        double[] bounds = model.getPlayerBounds(number);
        double[] ball = model.getBallPosition();
        double newCenterX = bounds[0] + bounds[2] / 2.0;
        double newBottom = bounds[1] + bounds[3];
        double moved = newCenterX - centerX;
        boolean nowAirborne = newBottom < ground - 1.0;
        boolean nowKicking = model.isPlayerKicking(number);

        clock += seconds;
        if (Math.abs(moved) < TELEPORT_PIXELS && seconds > 0.0) {
            speedX = moved / seconds;
            runPhase += Math.abs(moved) * RUN_RADIANS_PER_PIXEL;
        }
        if (airborne && !nowAirborne) {
            squash = SQUASH_ON_LANDING;
            particles.burst(newCenterX, ground, DUST, DUST_COUNT, DUST_SPEED);
        }
        if (!airborne && nowAirborne) {
            squash = STRETCH_ON_JUMP;
        }
        squash *= Math.exp(-SQUASH_RECOVERY * seconds);
        if (nowKicking && !kicking) {
            kickTime = KICK_SECONDS;
            particles.burst(newCenterX + facing * bounds[2] / 2.0, newBottom, Resources.READY, SPARK_COUNT,
                    SPARK_SPEED);
        }
        kickTime = Math.max(0.0, kickTime - seconds);

        armed = model.isPlayerPowerArmed(number);
        sparkTime += seconds;
        if (armed && sparkTime >= AURA_SPARK_EVERY) {
            sparkTime = 0.0;
            particles.burst(newCenterX, newBottom - bounds[3] / 2.0, Resources.READY, 1, AURA_SPARK_SPEED);
        }

        double towardX = ball[0] - newCenterX;
        double towardY = ball[1] - (bounds[1] + bounds[2] / 2.0);
        double distance = Math.max(1.0, Math.hypot(towardX, towardY));
        lookX = LOOK * towardX / distance;
        lookY = LOOK * towardY / distance;

        team = Resources.getInstance().teamColors(model.getPlayerCountry(number));
        effects = model.getPlayerEffects(number);
        facing = model.getPlayerFacing(number);
        scale = bounds[2] / NOMINAL_WIDTH;
        centerX = newCenterX;
        bottom = newBottom;
        airborne = nowAirborne;
        kicking = nowKicking;
    }

    @Override
    public void paint(Graphics2D g) {
        double width = NOMINAL_WIDTH * scale;
        double fade = Math.max(0.35, 1.0 - (ground - bottom) / SHADOW_FADE_HEIGHT);
        shape.setFrame(centerX - width * fade / 2.0, ground - 5.0, width * fade, 12.0);
        g.setColor(SHADOW);
        g.fill(shape);
        shape.setFrame(centerX - width * 0.62, ground - 7.0, width * 1.24, 16.0);
        g.setColor(ring);
        g.setStroke(RING);
        g.draw(shape);

        AffineTransform world = g.getTransform();
        g.translate(centerX, bottom);
        g.scale(scale * (1.0 + squash * SQUASH_AMOUNT), scale * (1.0 - squash * SQUASH_AMOUNT));
        g.setStroke(LINE);
        paintAura(g);
        double swing = Math.sin(Math.PI * (1.0 - kickTime / KICK_SECONDS));
        double stride = airborne ? 0.0 : Math.min(1.0, Math.abs(speedX) / STRIDE_SPEED);
        paintShoe(g, -facing, -stride * Math.sin(runPhase), -stride * Math.cos(runPhase), 0.0);
        paintTorso(g);
        paintShoe(g, facing, stride * Math.sin(runPhase), stride * Math.cos(runPhase), swing);
        paintHead(g, swing);
        overlay.paint(g, effects, clock);
        g.setTransform(world);
    }

    /** Halo que late alrededor del personaje mientras su poder esta armado. */
    private void paintAura(Graphics2D g) {
        if (armed) {
            double pulse = 3.0 * Math.sin(clock * AURA_SPEED);
            for (int i = 0; i < AURA.length; i++) {
                double radius = 56.0 - 9.0 * i + pulse;
                shape.setFrame(-radius, -48.0 - radius, 2.0 * radius, 2.0 * radius);
                g.setColor(AURA[i]);
                g.fill(shape);
            }
        }
    }

    /**
     * Zapato de un lado: alterna con el otro al correr y, si es el delantero, sale
     * hacia adelante durante la patada.
     *
     * @param side Lado del cuerpo: 1 derecha, -1 izquierda.
     * @param reach Avance del paso (-1 a 1).
     * @param lift Elevacion del paso (-1 a 1; solo se levanta con valores positivos).
     * @param swing Avance de la patada (0 a 1).
     */
    private void paintShoe(Graphics2D g, int side, double reach, double lift, double swing) {
        double x = side * 13.0 + reach * STEP_REACH + side * swing * KICK_REACH;
        double y = -6.0 - Math.max(0.0, lift) * STEP_LIFT - swing * KICK_LIFT;
        shape.setFrame(x - 12.0, y - 6.0, 24.0, 12.0);
        g.setColor(SHOE);
        g.fill(shape);
        g.setColor(OUTLINE);
        g.draw(shape);
        shape.setFrame(x - 2.0 + facing * 4.0, y - 4.5, 7.0, 3.5);
        g.setColor(SHOE_SHINE);
        g.fill(shape);
    }

    /** Torso corto con la camiseta del pais. */
    private void paintTorso(Graphics2D g) {
        g.setColor(team[0]);
        g.fill(TORSO);
        g.setColor(team[1]);
        g.fill(STRIPE);
        g.setPaint(TORSO_VOLUME);
        g.fill(TORSO);
        g.setColor(OUTLINE);
        g.draw(TORSO);
    }

    /** Cabeza con volumen: se inclina al correr y al patear, mira al balon y parpadea. */
    private void paintHead(Graphics2D g, double swing) {
        AffineTransform body = g.getTransform();
        double lean = MAX_LEAN * Math.max(-1.0, Math.min(1.0, speedX / LEAN_SPEED)) - facing * KICK_LEAN * swing;
        g.rotate(lean, 0.0, -36.0);
        g.translate(0.0, BOB * Math.sin(2.0 * runPhase));

        shape.setFrame(-facing * 27.0 - 5.0, -66.0, 10.0, 13.0);
        g.setColor(SKIN_SHADE);
        g.fill(shape);
        g.setColor(OUTLINE);
        g.draw(shape);
        g.setColor(SKIN);
        g.fill(HEAD);
        g.setColor(HAIR);
        g.fill(HAIR_CAP);
        g.setColor(team[2]);
        g.fill(HEADBAND);
        g.setPaint(HEAD_VOLUME);
        g.fill(HEAD);
        g.setColor(OUTLINE);
        g.draw(HEAD);

        double face = facing * FACE_SHIFT;
        boolean blinking = clock % BLINK_EVERY < BLINK_SECONDS;
        paintEye(g, face - 10.0, blinking);
        paintEye(g, face + 10.0, blinking);
        g.setColor(OUTLINE);
        mouth.setArc(face - 8.0 - 3.0 * swing, -52.0 - 3.0 * swing, 16.0 + 6.0 * swing, 9.0 + 6.0 * swing, 200.0,
                140.0, Arc2D.CHORD);
        g.fill(mouth);
        g.setTransform(body);
    }

    /** Ojo que sigue al balon; al parpadear queda como una linea, y con el poder armado frunce la ceja. */
    private void paintEye(Graphics2D g, double x, boolean blinking) {
        g.setColor(OUTLINE);
        double brow = armed ? 2.5 * Math.signum(x - facing * FACE_SHIFT) : 0.0;
        stroke.setLine(x - 6.0, -72.0 + brow, x + 6.0, -72.0 - brow);
        g.draw(stroke);
        if (blinking) {
            stroke.setLine(x - 5.0, -62.0, x + 5.0, -62.0);
            g.draw(stroke);
        } else {
            shape.setFrame(x - 6.5, -69.5, 13.0, 15.0);
            g.setColor(Color.WHITE);
            g.fill(shape);
            g.setColor(OUTLINE);
            g.draw(shape);
            shape.setFrame(x - 3.0 + lookX, -65.0 + lookY, 6.0, 6.0);
            g.fill(shape);
        }
    }
}
