package presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Cancha en vista lateral: cielo, cesped, linea central y los arcos con su
 * travesano. Las medidas y la ubicacion de los arcos vienen de la fachada, asi el
 * dibujo coincide con la fisica.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class FieldDrawing implements Drawable {

    /** Alto en pixeles del cesped que se ve bajo el suelo. */
    static final int GRASS_DEPTH = 40;

    private static final Color SKY = new Color(135, 206, 250);
    private static final Color GRASS = new Color(34, 139, 34);
    private static final Color LINE = Color.WHITE;
    private static final Color NET = new Color(255, 255, 255, 90);
    private static final Color POST = new Color(230, 230, 230);
    private static final double NET_SPACING = 15.0;
    private static final double MIDLINE_HEIGHT = 20.0;
    private static final BasicStroke THIN = new BasicStroke(1f);
    private static final BasicStroke THICK = new BasicStroke(4f);

    @Override
    public void draw(Graphics2D g, HeadSoccer game) throws POOBSoccerException {
        double width = game.getFieldWidth();
        double ground = game.getGroundY();
        g.setColor(SKY);
        g.fill(new Rectangle2D.Double(0.0, 0.0, width, ground));
        g.setColor(GRASS);
        g.fill(new Rectangle2D.Double(0.0, ground, width, GRASS_DEPTH));
        g.setColor(LINE);
        g.setStroke(THICK);
        g.draw(new Line2D.Double(0.0, ground, width, ground));
        g.draw(new Line2D.Double(width / 2.0, ground - MIDLINE_HEIGHT, width / 2.0, ground));
        double crossbar = game.getCrossbarThickness();
        for (double[] goal : game.getGoalBounds()) {
            drawGoal(g, goal, crossbar);
        }
    }

    /**
     * Dibuja un arco: red, postes y travesano.
     *
     * @param bounds Arco como {izquierda, arriba, ancho, alto}, tal como lo ubica la cancha.
     * @param crossbar Grosor del travesano.
     */
    private static void drawGoal(Graphics2D g, double[] bounds, double crossbar) {
        Rectangle2D frame = new Rectangle2D.Double(bounds[0], bounds[1], bounds[2], bounds[3]);
        g.setColor(NET);
        g.fill(frame);
        g.setStroke(THIN);
        for (double netX = frame.getMinX() + NET_SPACING; netX < frame.getMaxX(); netX += NET_SPACING) {
            g.draw(new Line2D.Double(netX, frame.getMinY(), netX, frame.getMaxY()));
        }
        g.setColor(POST);
        g.setStroke(THICK);
        g.draw(frame);
        g.fill(new Rectangle2D.Double(frame.getMinX(), frame.getMinY(), frame.getWidth(), crossbar));
    }
}
