package presentacion;

import java.awt.AlphaComposite;
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

/**
 * Lienzo del partido: compone los actores animados de la cancha (personajes,
 * balon, arcos, particulas y celebracion de gol) sobre el fondo callejero y los
 * pinta en orden, escalados de forma uniforme al espacio disponible. Solo los
 * recorre por la interfaz {@link Actor}; no decide ninguna regla.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CourtP extends JPanel {

    private static final double SEGUNDOS_FUNDIDO = 0.6;

    private final Resources recursos = Resources.obtenerInstancia();
    private final GoalCelebration celebracion;
    private final List<Actor> actores;
    private final Dimension tamano;
    private double fundido;

    /** @param modelo Fachada del dominio. */
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

    /**
     * Prepara el lienzo para un partido nuevo: entra con un fundido y sin
     * celebraciones pendientes.
     */
    void entrar() {
        fundido = SEGUNDOS_FUNDIDO;
        celebracion.olvidar();
    }

    /**
     * Avanza la animacion de todos los actores y pide repintar.
     *
     * @param segundos Segundos reales transcurridos desde el cuadro anterior.
     * @throws POOBSoccerException Si no hay un partido iniciado.
     */
    void animar(double segundos) throws POOBSoccerException {
        for (Actor actor : actores) {
            actor.actualizar(segundos);
        }
        fundido = Math.max(0.0, fundido - segundos);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graficos) {
        super.paintComponent(graficos);
        Graphics2D g = (Graphics2D) graficos.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setComposite(AlphaComposite.SrcOver.derive((float) (1.0 - fundido / SEGUNDOS_FUNDIDO)));
        double escala = Math.min(getWidth() / (double) tamano.width, getHeight() / (double) tamano.height);
        g.translate((getWidth() - tamano.width * escala) / 2.0 + celebracion.obtenerSacudida(),
                (getHeight() - tamano.height * escala) / 2.0);
        recursos.dibujarFondo(g, (int) Math.round(tamano.width * escala), (int) Math.round(tamano.height * escala));
        g.scale(escala, escala);
        for (Actor actor : actores) {
            actor.dibujar(g);
        }
        g.dispose();
        Toolkit.getDefaultToolkit().sync();
    }
}
