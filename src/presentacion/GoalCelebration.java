package presentacion;

import static presentacion.Texts.texto;

import java.awt.Color;
import java.awt.Graphics2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Muestra el letrero de gol en la cancha durante unos instantes cuando cambia
 * el marcador.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class GoalCelebration implements Actor {

    private static final int MARCADOR_DESCONOCIDO = -1;
    private static final double SEGUNDOS = 1.5;

    private final HeadSoccer modelo;
    private final double ancho;
    private final double suelo;
    private int marcador = MARCADOR_DESCONOCIDO;
    private double tiempo;

    GoalCelebration(HeadSoccer modelo) {
        this.modelo = modelo;
        this.ancho = modelo.obtenerAnchoCancha();
        this.suelo = modelo.obtenerSueloY();
    }

    void olvidar() {
        marcador = MARCADOR_DESCONOCIDO;
        tiempo = 0.0;
    }

    @Override
    public void actualizar(double segundos) throws POOBSoccerException {
        int actual = modelo.obtenerGolesJugador(1) + modelo.obtenerGolesJugador(2);
        if (marcador != MARCADOR_DESCONOCIDO && actual != marcador) {
            tiempo = SEGUNDOS;
        }
        marcador = actual;
        tiempo -= segundos;
        if (tiempo < 0.0) {
            tiempo = 0.0;
        }
    }

    @Override
    public void dibujar(Graphics2D g) {
        if (tiempo > 0.0) {
            g.setFont(Resources.FUENTE_TITULO);
            String mensaje = texto("match.goal");
            int textoAncho = g.getFontMetrics().stringWidth(mensaje);
            int x = (int) (ancho / 2.0 - textoAncho / 2.0);
            int y = (int) (suelo * 0.4);

            g.setColor(Color.BLACK);
            g.drawString(mensaje, x + 3, y + 3);
            g.setColor(Resources.LISTO);
            g.drawString(mensaje, x, y);
        }
    }
}
