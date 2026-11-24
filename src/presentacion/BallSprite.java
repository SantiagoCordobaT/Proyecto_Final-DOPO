package presentacion;

import java.awt.Color;
import java.awt.Graphics2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Dibuja el balón y su sombra en la cancha.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class BallSprite {

    private final HeadSoccer modelo;
    private final double radio;
    private double x;
    private double y;

    BallSprite(HeadSoccer modelo) {
        this.modelo = modelo;
        this.radio = modelo.obtenerRadioBalon();
    }

    void actualizar() throws POOBSoccerException {
        double[] posicion = modelo.obtenerPosicionBalon();
        x = posicion[0];
        y = posicion[1];
    }

    void dibujar(Graphics2D g) {
        int diametro = (int) (radio * 2);
        int esquinaX = (int) (x - radio);
        int esquinaY = (int) (y - radio);

        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(esquinaX, (int) modelo.obtenerSueloY() - 4, diametro, 8);

        g.setColor(Color.WHITE);
        g.fillOval(esquinaX, esquinaY, diametro, diametro);
        g.setColor(Color.BLACK);
        g.drawOval(esquinaX, esquinaY, diametro, diametro);

        g.fillOval((int) (x - radio / 3), (int) (y - radio / 3), (int) (radio * 2 / 3), (int) (radio * 2 / 3));
        g.drawLine(esquinaX + 4, (int) y, esquinaX + diametro - 4, (int) y);
        g.drawLine((int) x, esquinaY + 4, (int) x, esquinaY + diametro - 4);
    }
}
