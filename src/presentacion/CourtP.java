package presentacion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Lienzo donde se dibuja el fondo de la cancha y todos los actores del partido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CourtP extends JPanel {

    private final Resources recursos = Resources.obtenerInstancia();
    private final GoalCelebration celebracion;
    private final Actor[] actores;
    private final Dimension tamano;

    CourtP(HeadSoccer modelo) {
        Particles particulas = new Particles();
        GoalSprite arcos = new GoalSprite(modelo);
        celebracion = new GoalCelebration(modelo);
        actores = new Actor[] {
                new PlayerSprite(modelo, 1),
                new PlayerSprite(modelo, 2),
                new BallSprite(modelo),
                arcos,
                particulas,
                celebracion
        };
        tamano = new Dimension((int) modelo.obtenerAnchoCancha(), recursos.altoCancha(modelo.obtenerSueloY()));
        setPreferredSize(tamano);
        setBackground(Color.BLACK);
    }

    void entrar() {
        celebracion.olvidar();
    }

    void animar(double segundos) throws POOBSoccerException {
        for (int i = 0; i < actores.length; i++) {
            actores[i].actualizar(segundos);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graficos) {
        super.paintComponent(graficos);
        Graphics2D g = (Graphics2D) graficos.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double escalaX = getWidth() / (double) tamano.width;
        double escalaY = getHeight() / (double) tamano.height;
        double escala = escalaX;
        if (escalaY < escala) {
            escala = escalaY;
        }
        g.translate((getWidth() - tamano.width * escala) / 2.0, (getHeight() - tamano.height * escala) / 2.0);
        recursos.dibujarFondo(g, (int) Math.round(tamano.width * escala), (int) Math.round(tamano.height * escala));
        g.scale(escala, escala);
        for (int i = 0; i < actores.length; i++) {
            actores[i].dibujar(g);
        }
        g.dispose();
        Toolkit.getDefaultToolkit().sync();
    }
}
