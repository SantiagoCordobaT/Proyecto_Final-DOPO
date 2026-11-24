package presentacion;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Dibuja un jugador en la cancha con los colores de su país, animación de
 * patada y efectos activos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class PlayerSprite implements Actor {

    private static final double ANCHO_NOMINAL = 60.0;
    private static final Color PIEL = new Color(246, 205, 160);
    private static final Color CABELLO = new Color(40, 28, 22);
    private static final Color ZAPATO = new Color(28, 28, 32);

    private final HeadSoccer modelo;
    private final int numero;
    private final EffectOverlay capaEfectos = new EffectOverlay();
    private final Color colorIndicador;

    private Color[] equipo;
    private String[] efectos = new String[0];
    private double centroX;
    private double baseY;
    private double escala = 1.0;
    private int orientacion = 1;
    private boolean armado;
    private boolean pateando;

    PlayerSprite(HeadSoccer modelo, int numero) {
        this.modelo = modelo;
        this.numero = numero;
        this.colorIndicador = Resources.obtenerInstancia().colorJugador(numero);
        this.equipo = Resources.obtenerInstancia().coloresEquipo("");
    }

    @Override
    public void actualizar(double segundos) throws POOBSoccerException {
        double[] limites = modelo.obtenerLimitesJugador(numero);
        centroX = limites[0] + limites[2] / 2.0;
        baseY = limites[1] + limites[3];
        escala = limites[2] / ANCHO_NOMINAL;
        orientacion = modelo.obtenerOrientacionJugador(numero);
        armado = modelo.estaPoderJugadorArmado(numero);
        pateando = modelo.estaJugadorPateando(numero);
        equipo = Resources.obtenerInstancia().coloresEquipo(modelo.obtenerPaisJugador(numero));
        efectos = modelo.obtenerEfectosJugador(numero);
    }

    @Override
    public void dibujar(Graphics2D g) {
        AffineTransform guardado = g.getTransform();
        g.translate(centroX, baseY);
        g.scale(escala, escala);

        g.setColor(colorIndicador);
        g.drawOval(-25, -6, 50, 12);

        if (armado) {
            g.setColor(Resources.LISTO);
            g.drawOval(-35, -95, 70, 95);
        }

        g.setColor(equipo[0]);
        g.fillRect(-17, -38, 34, 30);
        g.setColor(equipo[1]);
        g.fillRect(-5, -38, 10, 30);
        g.setColor(Color.BLACK);
        g.drawRect(-17, -38, 34, 30);

        int desplazamientoPatada = 0;
        if (pateando) {
            desplazamientoPatada = orientacion * 18;
        }
        g.setColor(ZAPATO);
        g.fillRect(-16, -10, 14, 10);
        g.fillRect(2 + desplazamientoPatada, -10, 14, 10);
        g.setColor(Color.BLACK);
        g.drawRect(-16, -10, 14, 10);
        g.drawRect(2 + desplazamientoPatada, -10, 14, 10);

        g.setColor(PIEL);
        g.fillOval(-30, -90, 60, 60);
        g.setColor(CABELLO);
        g.fillArc(-30, -90, 60, 60, 20, 140);
        g.setColor(equipo[2]);
        g.fillRect(-26, -73, 52, 6);
        g.setColor(Color.BLACK);
        g.drawOval(-30, -90, 60, 60);

        int centroCara = orientacion * 6;
        g.setColor(Color.WHITE);
        g.fillOval(centroCara - 14, -68, 10, 12);
        g.fillOval(centroCara + 4, -68, 10, 12);
        g.setColor(Color.BLACK);
        g.drawOval(centroCara - 14, -68, 10, 12);
        g.drawOval(centroCara + 4, -68, 10, 12);
        int pupila = 1;
        if (orientacion > 0) {
            pupila = 4;
        }
        g.fillOval(centroCara - 14 + pupila, -64, 5, 5);
        g.fillOval(centroCara + 4 + pupila, -64, 5, 5);

        g.drawArc(centroCara - 6, -50, 12, 8, 200, 140);

        capaEfectos.dibujar(g, efectos);

        g.setTransform(guardado);
    }
}
