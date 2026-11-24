package presentacion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Lienzo donde se dibujan la cancha, los dos jugadores, el balón, los arcos y
 * el letrero de gol.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CourtP extends JPanel {

    private static final int ALTO_CANCHA = 490;
    private static final double SEGUNDOS_AVISO_GOL = 1.4;

    private final HeadSoccer modelo;
    private final PlayerSprite jugador1;
    private final PlayerSprite jugador2;
    private final BallSprite balon;
    private final GoalSprite arcos;

    private int totalGoles = -1;
    private double tiempoAvisoGol;

    CourtP(HeadSoccer modelo) {
        this.modelo = modelo;
        jugador1 = new PlayerSprite(modelo, 1);
        jugador2 = new PlayerSprite(modelo, 2);
        balon = new BallSprite(modelo);
        arcos = new GoalSprite(modelo);
        setPreferredSize(new Dimension((int) modelo.obtenerAnchoCancha(), ALTO_CANCHA));
        setBackground(Estilo.FONDO);
    }

    void entrar() {
        totalGoles = -1;
        tiempoAvisoGol = 0.0;
    }

    void animar(double segundos) throws POOBSoccerException {
        jugador1.actualizar();
        jugador2.actualizar();
        balon.actualizar();
        arcos.actualizar();

        int golesActuales = modelo.obtenerResumenJugador(1).obtenerGoles()
                + modelo.obtenerResumenJugador(2).obtenerGoles();
        if (totalGoles >= 0 && golesActuales != totalGoles) {
            tiempoAvisoGol = SEGUNDOS_AVISO_GOL;
        }
        totalGoles = golesActuales;
        if (tiempoAvisoGol > 0.0) {
            tiempoAvisoGol -= segundos;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Estilo.dibujarFondoCancha(g2, getWidth(), getHeight(), (int) modelo.obtenerSueloY());
        jugador1.dibujar(g2);
        jugador2.dibujar(g2);
        balon.dibujar(g2);
        arcos.dibujar(g2);

        if (tiempoAvisoGol > 0.0) {
            g2.setFont(Estilo.FUENTE_TITULO);
            String texto = Texts.t("¡GOL!", "GOAL!");
            int x = (getWidth() - g2.getFontMetrics().stringWidth(texto)) / 2;
            int y = (int) (modelo.obtenerSueloY() * 0.42);
            g2.setColor(Color.BLACK);
            g2.drawString(texto, x + 3, y + 3);
            g2.setColor(Estilo.RESALTE);
            g2.drawString(texto, x, y);
        }
    }
}
