package presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Personaje de un jugador dibujado dentro de su hitbox: la cabeza arriba y el
 * cuerpo debajo, asi el efecto de reduccion se ve al encogerse la hitbox. Encima
 * se escriben sus efectos activos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class PlayerDrawing implements Drawable {

    private static final Color HEAD = new Color(255, 220, 177);
    private static final Color OUTLINE = Color.BLACK;
    private static final Font EFFECTS_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    private static final String EFFECT_SEPARATOR = ", ";
    private static final double EFFECTS_GAP = 6.0;

    private final int number;
    private final Color shirt;

    /**
     * @param number Jugador (1 o 2).
     * @param shirt Color de la camiseta del jugador.
     */
    PlayerDrawing(int number, Color shirt) {
        this.number = number;
        this.shirt = shirt;
    }

    @Override
    public void draw(Graphics2D g, HeadSoccer game) throws POOBSoccerException {
        double[] bounds = game.getPlayerBounds(number);
        double left = bounds[0];
        double top = bounds[1];
        double width = bounds[2];
        double height = bounds[3];
        Ellipse2D head = new Ellipse2D.Double(left, top, width, width);
        Rectangle2D body = new Rectangle2D.Double(left + width / 4.0, top + width, width / 2.0, height - width);
        g.setColor(shirt);
        g.fill(body);
        g.setColor(HEAD);
        g.fill(head);
        g.setColor(OUTLINE);
        g.draw(body);
        g.draw(head);
        g.setFont(EFFECTS_FONT);
        g.drawString(String.join(EFFECT_SEPARATOR, game.getPlayerEffects(number)),
                (float) left, (float) (top - EFFECTS_GAP));
    }
}
