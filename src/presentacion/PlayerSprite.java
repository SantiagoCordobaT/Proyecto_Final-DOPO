package presentacion;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import dominio.HeadSoccer;
import dominio.ResumenJugador;
import dominio.excepciones.POOBSoccerException;

/**
 * Dibuja un personaje en la cancha a partir de su resumen actual.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class PlayerSprite {

    private static final double ANCHO_BASE = 60.0;
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
        this.colorIndicador = Estilo.colorJugador(numero);
        this.equipo = Estilo.coloresPais("");
    }

    void actualizar() throws POOBSoccerException {
        ResumenJugador j = modelo.obtenerResumenJugador(numero);
        centroX = j.obtenerX() + j.obtenerAncho() / 2.0;
        baseY = j.obtenerY() + j.obtenerAlto();
        escala = j.obtenerAncho() / ANCHO_BASE;
        orientacion = j.obtenerOrientacion();
        armado = j.estaPoderArmado();
        pateando = j.estaPateando();
        equipo = Estilo.coloresPais(j.obtenerPais());
        efectos = j.obtenerEfectos();
    }

    void dibujar(Graphics2D g) {
        AffineTransform guardado = g.getTransform();
        g.translate(centroX, baseY);
        g.scale(escala, escala);

        g.setColor(colorIndicador);
        g.drawOval(-25, -6, 50, 12);

        if (armado) {
            g.setColor(Estilo.RESALTE);
            g.drawOval(-35, -95, 70, 95);
        }

        g.setColor(equipo[0]);
        g.fillRect(-17, -38, 34, 30);
        g.setColor(equipo[1]);
        g.fillRect(-5, -38, 10, 30);
        g.setColor(Color.BLACK);
        g.drawRect(-17, -38, 34, 30);

        int desplazamientoPatada = pateando ? orientacion * 18 : 0;
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
        int pupila = orientacion > 0 ? 4 : 1;
        g.fillOval(centroCara - 14 + pupila, -64, 5, 5);
        g.fillOval(centroCara + 4 + pupila, -64, 5, 5);

        g.drawArc(centroCara - 6, -50, 12, 8, 200, 140);

        capaEfectos.dibujar(g, efectos);

        g.setTransform(guardado);
    }
}
