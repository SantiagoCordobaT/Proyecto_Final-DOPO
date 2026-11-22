package presentacion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

@SuppressWarnings("serial")
final class CourtP extends JPanel {

    private final Resources recursos = Resources.obtenerInstancia();
    private final GoalCelebration celebracion;
    private final List<Actor> actores;
    private final Dimension tamano;

    CourtP(HeadSoccer modelo) {
        Particles particulas = new Particles();
        GoalSprite arcos = new GoalSprite(modelo);
        celebracion = new GoalCelebration(modelo, particulas, arcos);
        actores = Arrays.asList(new PlayerSprite(modelo, 1, particulas), new PlayerSprite(modelo, 2, particulas),
                new BallSprite(modelo, particulas), arcos, particulas, celebracion);
        tamano = new Dimension((int) modelo.obtenerAnchoCancha(), recursos.altoCancha(modelo.obtenerSueloY()));
        setPreferredSize(tamano);
        setBackground(Color.BLACK);
    }

    void entrar() {
        celebracion.olvidar();
    }

    void animar(double segundos) throws POOBSoccerException {
        for (int i = 0; i < actores.size(); i++) {
            actores.get(i).actualizar(segundos);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graficos) {
        super.paintComponent(graficos);
        Graphics2D g = (Graphics2D) graficos.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double escala = Math.min(getWidth() / (double) tamano.width, getHeight() / (double) tamano.height);
        g.translate((getWidth() - tamano.width * escala) / 2.0, (getHeight() - tamano.height * escala) / 2.0);
        recursos.dibujarFondo(g, (int) Math.round(tamano.width * escala), (int) Math.round(tamano.height * escala));
        g.scale(escala, escala);
        for (int i = 0; i < actores.size(); i++) {
            actores.get(i).dibujar(g);
        }
        g.dispose();
        Toolkit.getDefaultToolkit().sync();
    }
}
