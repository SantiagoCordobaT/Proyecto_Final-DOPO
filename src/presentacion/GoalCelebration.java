package presentacion;

import static presentacion.Texts.texto;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Celebracion de gol: cuando el marcador cambia entre dos cuadros muestra el
 * cartel de gol, lanza papelitos, sacude la red del arco donde entro el balon y
 * hace temblar la camara un instante. No decide si hubo gol: solo reacciona al
 * marcador que informa la fachada.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class GoalCelebration implements Actor {

    private static final int MARCADOR_DESCONOCIDO = -1;
    private static final double SEGUNDOS = 1.5;
    private static final double CARTEL_Y = 0.36;
    private static final double REBOTE_ESCALA = 6.0;
    private static final double PIXELES_TEMBLOR = 7.0;
    private static final double VELOCIDAD_TEMBLOR = 70.0;
    private static final int RAFAGAS_CONFETI = 6;
    private static final int CANTIDAD_CONFETI = 14;
    private static final double VELOCIDAD_CONFETI = 420.0;
    private static final FontRenderContext CONTEXTO_FUENTE = new FontRenderContext(null, true, true);
    private static final BasicStroke CONTORNO = new BasicStroke(9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final BasicStroke BORDE = new BasicStroke(1.5f);
    private static final GradientPaint RELLENO = new GradientPaint(0f, -60f, Resources.LISTO, 0f, 0f,
            Resources.ACENTO);

    private final HeadSoccer modelo;
    private final Particles particulas;
    private final GoalSprite arcos;
    private final double ancho;
    private final double suelo;
    private final Color[] confeti;
    private Shape cartel = new Rectangle2D.Double();
    private int marcador = MARCADOR_DESCONOCIDO;
    private double balonX;
    private double tiempo;

    /**
     * @param modelo     Fachada del dominio.
     * @param particulas Particulas de la cancha, para los papelitos.
     * @param arcos      Arcos, para sacudir la red donde entro el balon.
     */
    GoalCelebration(HeadSoccer modelo, Particles particulas, GoalSprite arcos) {
        Resources recursos = Resources.obtenerInstancia();
        this.modelo = modelo;
        this.particulas = particulas;
        this.arcos = arcos;
        this.ancho = modelo.obtenerAnchoCancha();
        this.suelo = modelo.obtenerSueloY();
        this.confeti = new Color[] { recursos.colorJugador(1), recursos.colorJugador(2), Resources.LISTO,
                Resources.TEXTO };
    }

    /**
     * Olvida el marcador conocido: el primer cuadro de un partido nuevo no es un
     * gol.
     */
    void olvidar() {
        marcador = MARCADOR_DESCONOCIDO;
        tiempo = 0.0;
    }

    /** @return Desplazamiento horizontal de la camara por el temblor del gol. */
    double obtenerSacudida() {
        double intensidad = tiempo / SEGUNDOS;
        return PIXELES_TEMBLOR * intensidad * intensidad * Math.sin(tiempo * VELOCIDAD_TEMBLOR);
    }

    @Override
    public void actualizar(double segundos) throws POOBSoccerException {
        int actual = modelo.obtenerGolesJugador(1) + modelo.obtenerGolesJugador(2);
        if (marcador != MARCADOR_DESCONOCIDO && actual != marcador) {
            celebrar();
        }
        marcador = actual;
        balonX = modelo.obtenerPosicionBalon()[0];
        tiempo = Math.max(0.0, tiempo - segundos);
    }

    private void celebrar() {
        tiempo = SEGUNDOS;
        arcos.sacudirCerca(balonX);
        Shape contorno = Resources.FUENTE_TITULO.createGlyphVector(CONTEXTO_FUENTE, texto("match.goal")).getOutline();
        Rectangle2D caja = contorno.getBounds2D();
        cartel = AffineTransform.getTranslateInstance(-caja.getCenterX(), -caja.getMaxY())
                .createTransformedShape(contorno);
        for (int i = 0; i < RAFAGAS_CONFETI; i++) {
            particulas.lanzarRafaga(ancho * (i + 0.5) / RAFAGAS_CONFETI, suelo * CARTEL_Y, confeti[i % confeti.length],
                    CANTIDAD_CONFETI, VELOCIDAD_CONFETI);
        }
    }

    @Override
    public void dibujar(Graphics2D g) {
        if (tiempo > 0.0) {
            double progreso = 1.0 - tiempo / SEGUNDOS;
            double tamano = Math.min(1.0, progreso * REBOTE_ESCALA) * Math.min(1.0, (1.0 - progreso) * REBOTE_ESCALA);
            AffineTransform mundo = g.getTransform();
            g.translate(ancho / 2.0, suelo * CARTEL_Y);
            g.scale(tamano, tamano);
            g.setColor(Color.BLACK);
            g.setStroke(CONTORNO);
            g.draw(cartel);
            g.setPaint(RELLENO);
            g.fill(cartel);
            g.setColor(Color.WHITE);
            g.setStroke(BORDE);
            g.draw(cartel);
            g.setTransform(mundo);
        }
    }
}
