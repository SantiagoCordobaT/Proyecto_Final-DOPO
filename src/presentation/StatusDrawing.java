package presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Estado del partido centrado en la franja superior: el tipo de partido decide que
 * muestra (reloj o "GOL DE ORO", meta de goles, o balones y racha de Supervivencia).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class StatusDrawing implements Drawable {

    private static final Color TEXT = Color.WHITE;
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    private static final int BASELINE_Y = 52;

    @Override
    public void draw(Graphics2D g, HeadSoccer game) throws POOBSoccerException {
        String status = game.getMatchStatus();
        FontMetrics metrics = g.getFontMetrics(FONT);
        g.setColor(TEXT);
        g.setFont(FONT);
        g.drawString(status, (int) (game.getFieldWidth() - metrics.stringWidth(status)) / 2, BASELINE_Y);
    }
}
