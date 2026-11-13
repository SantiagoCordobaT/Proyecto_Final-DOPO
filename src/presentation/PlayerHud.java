package presentation;

import static presentation.Texts.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Marcador de un jugador en la franja superior: nombre y pais, goles, barra de
 * poder y nombre del poder. Con el poder armado la barra se pone dorada y avisa
 * que el poder esta listo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class PlayerHud implements Drawable {

    /** Ancho en pixeles que ocupa el marcador. */
    static final int WIDTH = 270;

    private static final Color TEXT = Color.WHITE;
    private static final Color READY = new Color(255, 200, 0);
    private static final Color READY_TEXT = Color.BLACK;
    private static final Font NAME_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    private static final Font POWER_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    private static final Font GOALS_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 40);
    private static final int NAME_Y = 24;
    private static final int BAR_Y = 38;
    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 16;
    private static final int TEXT_INSET = 4;
    private static final int POWER_Y = 74;
    private static final int GOALS_X = BAR_WIDTH + 25;
    private static final int GOALS_Y = 66;

    private final int number;
    private final int x;
    private final Color color;

    /**
     * @param number Jugador (1 o 2).
     * @param x Borde izquierdo del marcador.
     * @param color Color del jugador para su barra de poder.
     */
    PlayerHud(int number, int x, Color color) {
        this.number = number;
        this.x = x;
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g, HeadSoccer game) throws POOBSoccerException {
        g.setColor(TEXT);
        g.setFont(NAME_FONT);
        g.drawString(game.getPlayerName(number) + " (" + game.getPlayerCountry(number) + ")", x, NAME_Y);
        g.setFont(POWER_FONT);
        g.drawString(game.getPlayerPowerName(number), x, POWER_Y);
        g.setFont(GOALS_FONT);
        g.drawString(String.valueOf(game.getPlayerGoals(number)), x + GOALS_X, GOALS_Y);

        Rectangle2D bar = new Rectangle2D.Double(x, BAR_Y, BAR_WIDTH, BAR_HEIGHT);
        g.setColor(color);
        g.fill(new Rectangle2D.Double(x, BAR_Y, BAR_WIDTH * game.getPlayerPowerBar(number), BAR_HEIGHT));
        if (game.isPlayerPowerArmed(number)) {
            g.setColor(READY);
            g.fill(bar);
            g.setColor(READY_TEXT);
            g.setFont(POWER_FONT);
            g.drawString(text("hud.armed"), x + TEXT_INSET, BAR_Y + BAR_HEIGHT - TEXT_INSET);
        }
        g.setColor(TEXT);
        g.draw(bar);
    }
}
