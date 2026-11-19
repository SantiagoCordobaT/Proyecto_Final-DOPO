package presentacion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Arcos vistos de lado: poste en la boca, travesano arriba, soporte al fondo y
 * red en diagonal. La red del arco donde entra el balon se sacude al celebrarse
 * un gol. Las medidas vienen de la cancha del dominio.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class GoalSprite implements Actor {

    private static final Color SOMBRA_RED = new Color(10, 10, 14, 70);
    private static final Color RED = new Color(255, 255, 255, 110);
    private static final Color POSTE = new Color(236, 236, 230);
    private static final Color SOMBRA_POSTE = new Color(150, 150, 150);
    private static final BasicStroke LINEA_RED = new BasicStroke(1f);
    private static final double ESPACIADO_RED = 11.0;
    private static final double ANCHO_POSTE = 7.0;
    private static final double ANCHO_FONDO = 3.0;
    private static final double SEGUNDOS_SACUDIDA = 0.9;
    private static final double PIXELES_SACUDIDA = 7.0;
    private static final double VELOCIDAD_SACUDIDA = 38.0;

    private final HeadSoccer modelo;
    private final double centro;
    private final Rectangle2D.Double marco = new Rectangle2D.Double();
    private final Rectangle2D.Double barra = new Rectangle2D.Double();
    private final Line2D.Double hilo = new Line2D.Double();
    private double[][] arcos = new double[0][];
    private double[] sacudida = new double[0];
    private double travesano;

    /** @param modelo Fachada del dominio. */
    GoalSprite(HeadSoccer modelo) {
        this.modelo = modelo;
        this.centro = modelo.obtenerAnchoCancha() / 2.0;
    }

    /**
     * Sacude la red del arco mas cercano a un punto.
     *
     * @param x Coordenada X donde se vio el balon por ultima vez antes del gol.
     */
    void sacudirCerca(double x) {
        for (int i = 0; i < arcos.length; i++) {
            if ((arcos[i][0] < centro) == (x < centro)) {
                sacudida[i] = SEGUNDOS_SACUDIDA;
            }
        }
    }

    @Override
    public void actualizar(double segundos) throws POOBSoccerException {
        arcos = modelo.obtenerLimitesArcos();
        travesano = modelo.obtenerGrosorTravesano();
        if (sacudida.length != arcos.length) {
            sacudida = new double[arcos.length];
        }
        for (int i = 0; i < sacudida.length; i++) {
            sacudida[i] = Math.max(0.0, sacudida[i] - segundos);
        }
    }

    @Override
    public void dibujar(Graphics2D g) {
        for (int i = 0; i < arcos.length; i++) {
            double izquierda = arcos[i][0];
            double arriba = arcos[i][1];
            double ancho = arcos[i][2];
            double alto = arcos[i][3];
            double vaiven = PIXELES_SACUDIDA * sacudida[i] / SEGUNDOS_SACUDIDA
                    * Math.sin(sacudida[i] * VELOCIDAD_SACUDIDA);
            boolean bocaDerecha = izquierda < centro;
            marco.setRect(izquierda, arriba, ancho, alto);

            Shape recorte = g.getClip();
            g.clip(marco);
            g.setColor(SOMBRA_RED);
            g.fill(marco);
            g.setColor(RED);
            g.setStroke(LINEA_RED);
            for (double desplazamiento = -alto; desplazamiento < ancho; desplazamiento += ESPACIADO_RED) {
                hilo.setLine(izquierda + desplazamiento, arriba, izquierda + desplazamiento + alto + vaiven,
                        arriba + alto);
                g.draw(hilo);
                hilo.setLine(izquierda + desplazamiento + alto, arriba, izquierda + desplazamiento + vaiven,
                        arriba + alto);
                g.draw(hilo);
            }
            g.setClip(recorte);

            g.setColor(SOMBRA_POSTE);
            barra.setRect(bocaDerecha ? izquierda : izquierda + ancho - ANCHO_FONDO, arriba, ANCHO_FONDO, alto);
            g.fill(barra);
            g.setColor(POSTE);
            barra.setRect(izquierda, arriba, ancho, travesano);
            g.fill(barra);
            barra.setRect(bocaDerecha ? izquierda + ancho - ANCHO_POSTE : izquierda, arriba, ANCHO_POSTE, alto);
            g.fill(barra);
        }
    }
}
