package presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Arcos vistos de lado: poste en la boca, travesano arriba, soporte al fondo y
 * red en diagonal. La red del arco donde entra el balon se sacude al celebrarse
 * un gol. Las medidas vienen de la cancha del dominio.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class GoalSprite implements Actor {

    private static final Color NET_SHADE = new Color(10, 10, 14, 70);
    private static final Color NET = new Color(255, 255, 255, 110);
    private static final Color POST = new Color(236, 236, 230);
    private static final Color POST_SHADE = new Color(150, 150, 150);
    private static final BasicStroke NET_LINE = new BasicStroke(1f);
    private static final double NET_SPACING = 11.0;
    private static final double POST_WIDTH = 7.0;
    private static final double BACK_WIDTH = 3.0;
    private static final double SHAKE_SECONDS = 0.9;
    private static final double SHAKE_PIXELS = 7.0;
    private static final double SHAKE_SPEED = 38.0;

    private final HeadSoccer model;
    private final double center;
    private final Rectangle2D.Double frame = new Rectangle2D.Double();
    private final Rectangle2D.Double bar = new Rectangle2D.Double();
    private final Line2D.Double thread = new Line2D.Double();
    private double[][] goals = new double[0][];
    private double[] shake = new double[0];
    private double crossbar;

    /** @param model Fachada del dominio. */
    GoalSprite(HeadSoccer model) {
        this.model = model;
        this.center = model.getFieldWidth() / 2.0;
    }

    /**
     * Sacude la red del arco mas cercano a un punto.
     *
     * @param x Coordenada X donde se vio el balon por ultima vez antes del gol.
     */
    void shakeNear(double x) {
        for (int i = 0; i < goals.length; i++) {
            if ((goals[i][0] < center) == (x < center)) {
                shake[i] = SHAKE_SECONDS;
            }
        }
    }

    @Override
    public void update(double seconds) throws POOBSoccerException {
        goals = model.getGoalBounds();
        crossbar = model.getCrossbarThickness();
        if (shake.length != goals.length) {
            shake = new double[goals.length];
        }
        for (int i = 0; i < shake.length; i++) {
            shake[i] = Math.max(0.0, shake[i] - seconds);
        }
    }

    @Override
    public void paint(Graphics2D g) {
        for (int i = 0; i < goals.length; i++) {
            double left = goals[i][0];
            double top = goals[i][1];
            double width = goals[i][2];
            double height = goals[i][3];
            double sway = SHAKE_PIXELS * shake[i] / SHAKE_SECONDS * Math.sin(shake[i] * SHAKE_SPEED);
            boolean mouthOnRight = left < center;
            frame.setRect(left, top, width, height);

            Shape clip = g.getClip();
            g.clip(frame);
            g.setColor(NET_SHADE);
            g.fill(frame);
            g.setColor(NET);
            g.setStroke(NET_LINE);
            for (double offset = -height; offset < width; offset += NET_SPACING) {
                thread.setLine(left + offset, top, left + offset + height + sway, top + height);
                g.draw(thread);
                thread.setLine(left + offset + height, top, left + offset + sway, top + height);
                g.draw(thread);
            }
            g.setClip(clip);

            g.setColor(POST_SHADE);
            bar.setRect(mouthOnRight ? left : left + width - BACK_WIDTH, top, BACK_WIDTH, height);
            g.fill(bar);
            g.setColor(POST);
            bar.setRect(left, top, width, crossbar);
            g.fill(bar);
            bar.setRect(mouthOnRight ? left + width - POST_WIDTH : left, top, POST_WIDTH, height);
            g.fill(bar);
        }
    }
}
