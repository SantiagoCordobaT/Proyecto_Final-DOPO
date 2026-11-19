package presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Escudo de un equipo: franjas con los colores de su pais.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class TeamBadge extends JPanel {

    private static final Dimension SIZE = new Dimension(60, 40);

    private String country = "";

    /** Crea el escudo con colores neutros hasta que se le indique un pais. */
    TeamBadge() {
        setPreferredSize(SIZE);
    }

    /** @param country Pais cuyos colores debe mostrar el escudo. */
    void setCountry(String country) {
        if (!country.equals(this.country)) {
            this.country = country;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color[] colors = Resources.getInstance().teamColors(country);
        int stripe = getWidth() / colors.length;
        for (int i = 0; i < colors.length; i++) {
            g.setColor(colors[i]);
            g.fillRect(i * stripe, 0, stripe, getHeight());
        }
        g.setColor(Resources.TEXT);
        g.drawRect(0, 0, colors.length * stripe - 1, getHeight() - 1);
    }
}
