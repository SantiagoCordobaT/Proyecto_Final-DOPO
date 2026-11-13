package presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Lienzo del partido: pinta la cancha callejera, los arcos, los dos personajes y
 * el balon en las coordenadas que entrega la fachada. No decide ninguna regla.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CourtP extends JPanel {

    private static final Color SKIN = new Color(246, 205, 160);
    private static final Color HAIR = new Color(40, 28, 22);
    private static final Color OUTLINE = Color.BLACK;
    private static final Color NET = new Color(255, 255, 255, 70);
    private static final Color POST = new Color(232, 232, 226);
    private static final Color SHADOW = new Color(0, 0, 0, 90);
    private static final Color TAG = new Color(0, 0, 0, 170);
    private static final BasicStroke THIN = new BasicStroke(1f);
    private static final BasicStroke MEDIUM = new BasicStroke(2f);
    private static final BasicStroke THICK = new BasicStroke(5f);
    private static final String EFFECT_SEPARATOR = " · ";
    private static final double NET_SPACING = 12.0;
    private static final double RING_DROP = 6.0;
    private static final double TAG_GAP = 8.0;
    private static final double TAG_PADDING = 6.0;

    private final Resources resources = Resources.getInstance();
    private final HeadSoccer model;

    /** @param model Fachada del dominio. */
    CourtP(HeadSoccer model) {
        this.model = model;
        setPreferredSize(new Dimension((int) model.getFieldWidth(), resources.courtHeight(model.getGroundY())));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        resources.paintBackdrop(g, getWidth(), getHeight());
        try {
            paintGoals(g);
            paintPlayer(g, 1);
            paintPlayer(g, 2);
            paintBall(g);
        } catch (POOBSoccerException e) {
            g.setColor(Resources.TEXT);
            g.setFont(Resources.TEXT_FONT);
            g.drawString(e.getMessage(), Resources.GAP, Resources.GAP * 2);
        } finally {
            g.dispose();
        }
    }

    /** Arcos con red, postes y travesano, donde los ubica la cancha del dominio. */
    private void paintGoals(Graphics2D g) throws POOBSoccerException {
        double crossbar = model.getCrossbarThickness();
        for (double[] bounds : model.getGoalBounds()) {
            Rectangle2D frame = new Rectangle2D.Double(bounds[0], bounds[1], bounds[2], bounds[3]);
            g.setColor(NET);
            g.fill(frame);
            g.setStroke(THIN);
            for (double x = frame.getMinX() + NET_SPACING; x < frame.getMaxX(); x += NET_SPACING) {
                g.draw(new Line2D.Double(x, frame.getMinY(), x, frame.getMaxY()));
            }
            for (double y = frame.getMinY() + NET_SPACING; y < frame.getMaxY(); y += NET_SPACING) {
                g.draw(new Line2D.Double(frame.getMinX(), y, frame.getMaxX(), y));
            }
            g.setColor(POST);
            g.setStroke(THICK);
            g.draw(frame);
            g.fill(new Rectangle2D.Double(frame.getMinX(), frame.getMinY(), frame.getWidth(), crossbar));
        }
    }

    /**
     * Personaje cabezon: aro del color del jugador en el piso, camiseta con los
     * colores de su pais, cabeza que mira al balon y sus efectos activos encima.
     */
    private void paintPlayer(Graphics2D g, int number) throws POOBSoccerException {
        double[] bounds = model.getPlayerBounds(number);
        double left = bounds[0];
        double top = bounds[1];
        double width = bounds[2];
        double height = bounds[3];
        double centerX = left + width / 2.0;
        double look = Math.signum(model.getBallX() - centerX) * width * 0.05;
        Color[] team = resources.teamColors(model.getPlayerCountry(number));

        g.setColor(resources.playerColor(number));
        g.setStroke(THICK);
        g.draw(new Ellipse2D.Double(left - width * 0.1, model.getGroundY() - RING_DROP, width * 1.2, RING_DROP * 3.0));

        double bodyTop = top + width * 0.85;
        double shoeHeight = height * 0.12;
        RoundRectangle2D shirt = new RoundRectangle2D.Double(left + width * 0.2, bodyTop, width * 0.6,
                top + height - shoeHeight - bodyTop, width * 0.2, width * 0.2);
        g.setColor(team[0]);
        g.fill(shirt);
        g.setColor(team[1]);
        g.fill(new Rectangle2D.Double(centerX - width * 0.06, bodyTop, width * 0.12, shirt.getHeight()));
        g.setColor(OUTLINE);
        g.setStroke(MEDIUM);
        g.draw(shirt);
        g.fill(new Ellipse2D.Double(left + width * 0.08, top + height - shoeHeight, width * 0.38, shoeHeight));
        g.fill(new Ellipse2D.Double(left + width * 0.54, top + height - shoeHeight, width * 0.38, shoeHeight));

        Ellipse2D head = new Ellipse2D.Double(left, top, width, width);
        g.setColor(SKIN);
        g.fill(head);
        g.setColor(HAIR);
        g.fill(new Arc2D.Double(left, top, width, width, 15.0, 150.0, Arc2D.CHORD));
        g.setColor(team[2]);
        g.fill(new Rectangle2D.Double(left + width * 0.07, top + width * 0.3, width * 0.86, width * 0.09));
        g.setColor(OUTLINE);
        g.draw(head);
        paintEye(g, centerX - width * 0.2, top + width * 0.55, width * 0.2, look);
        paintEye(g, centerX + width * 0.2, top + width * 0.55, width * 0.2, look);
        g.draw(new Arc2D.Double(centerX - width * 0.15, top + width * 0.62, width * 0.3, width * 0.22, 200.0, 140.0,
                Arc2D.OPEN));

        paintEffects(g, model.getPlayerEffects(number), centerX, top);
    }

    private static void paintEye(Graphics2D g, double centerX, double centerY, double size, double look) {
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Double(centerX - size / 2.0, centerY - size / 2.0, size, size));
        g.setColor(OUTLINE);
        g.fill(new Ellipse2D.Double(centerX - size / 4.0 + look, centerY - size / 4.0, size / 2.0, size / 2.0));
    }

    /** Etiqueta con los efectos activos, centrada sobre la cabeza; nada si no hay efectos. */
    private static void paintEffects(Graphics2D g, String[] effects, double centerX, double top) {
        if (effects.length > 0) {
            String names = String.join(EFFECT_SEPARATOR, effects).toUpperCase();
            g.setFont(Resources.TEXT_FONT);
            FontMetrics metrics = g.getFontMetrics();
            double width = metrics.stringWidth(names) + 2.0 * TAG_PADDING;
            double height = metrics.getHeight();
            double left = centerX - width / 2.0;
            double tagTop = top - TAG_GAP - height;
            g.setColor(TAG);
            g.fill(new RoundRectangle2D.Double(left, tagTop, width, height, TAG_PADDING, TAG_PADDING));
            g.setColor(Resources.READY);
            g.drawString(names, (float) (left + TAG_PADDING), (float) (tagTop + metrics.getAscent()));
        }
    }

    /** Balon con su sombra en el piso: la sombra se encoge cuanto mas alto vuela. */
    private void paintBall(Graphics2D g) throws POOBSoccerException {
        double radius = model.getBallRadius();
        double x = model.getBallX();
        double y = model.getBallY();
        double ground = model.getGroundY();
        double shadow = radius * (1.0 + (y + radius) / ground);
        g.setColor(SHADOW);
        g.fill(new Ellipse2D.Double(x - shadow / 2.0, ground + RING_DROP - radius / 4.0, shadow, radius / 2.0));
        Ellipse2D ball = new Ellipse2D.Double(x - radius, y - radius, 2.0 * radius, 2.0 * radius);
        g.setColor(Color.WHITE);
        g.fill(ball);
        g.setColor(OUTLINE);
        g.setStroke(MEDIUM);
        g.draw(ball);
        g.fill(new Ellipse2D.Double(x - radius * 0.35, y - radius * 0.35, radius * 0.7, radius * 0.7));
        g.draw(new Line2D.Double(x - radius, y, x - radius * 0.35, y));
        g.draw(new Line2D.Double(x + radius * 0.35, y, x + radius, y));
        g.draw(new Line2D.Double(x, y - radius, x, y - radius * 0.35));
        g.draw(new Line2D.Double(x, y + radius * 0.35, x, y + radius));
    }
}
