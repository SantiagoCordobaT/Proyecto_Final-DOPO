package presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import dominio.HeadSoccer;

/**
 * Cancha en vista lateral: cielo, cesped, linea central y los dos arcos con su
 * travesano. Las medidas vienen de la fachada, asi el dibujo coincide con la fisica.
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
    public void draw(Graphics2D g, HeadSoccer game) {
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
        drawGoal(g, game, 0.0);
        drawGoal(g, game, width - game.getGoalWidth());
    }

    /** Dibuja un arco (red, postes y travesano) cuyo borde izquierdo esta en x. */
    private static void drawGoal(Graphics2D g, HeadSoccer game, double x) {
        double goalWidth = game.getGoalWidth();
        double ground = game.getGroundY();
        double top = ground - game.getGoalHeight();
        Rectangle2D frame = new Rectangle2D.Double(x, top, goalWidth, ground - top);
        g.setColor(NET);
        g.fill(frame);
        g.setStroke(THIN);
        for (double netX = x + NET_SPACING; netX < x + goalWidth; netX += NET_SPACING) {
            g.draw(new Line2D.Double(netX, top, netX, ground));
        }
        g.setColor(POST);
        g.setStroke(THICK);
        g.draw(frame);
        g.fill(new Rectangle2D.Double(x, top, goalWidth, game.getCrossbarThickness()));
    }
}
