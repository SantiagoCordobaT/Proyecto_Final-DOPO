package presentation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Balon: un circulo con el radio de la fisica centrado en su posicion.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class BallDrawing implements Drawable {

    private static final Color FILL = Color.WHITE;
    private static final Color OUTLINE = Color.BLACK;

    @Override
    public void draw(Graphics2D g, HeadSoccer game) throws POOBSoccerException {
        double radius = game.getBallRadius();
        Ellipse2D ball = new Ellipse2D.Double(game.getBallX() - radius, game.getBallY() - radius,
                2.0 * radius, 2.0 * radius);
        g.setColor(FILL);
        g.fill(ball);
        g.setColor(OUTLINE);
        g.draw(ball);
    }
}
