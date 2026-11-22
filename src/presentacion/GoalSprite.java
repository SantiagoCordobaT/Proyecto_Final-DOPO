package presentacion;

import java.awt.Color;
import java.awt.Graphics2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

final class GoalSprite implements Actor {

    private final HeadSoccer modelo;
    private double[][] arcos = new double[0][];
    private double travesano;

    GoalSprite(HeadSoccer modelo) {
        this.modelo = modelo;
    }

    void sacudirCerca(double x) {
    }

    @Override
    public void actualizar(double segundos) throws POOBSoccerException {
        arcos = modelo.obtenerLimitesArcos();
        travesano = modelo.obtenerGrosorTravesano();
    }

    @Override
    public void dibujar(Graphics2D g) {
        double centro = modelo.obtenerAnchoCancha() / 2.0;
        for (int i = 0; i < arcos.length; i++) {
            int izquierda = (int) arcos[i][0];
            int arriba = (int) arcos[i][1];
            int ancho = (int) arcos[i][2];
            int alto = (int) arcos[i][3];
            int grosor = (int) travesano;
            boolean bocaDerecha = izquierda < centro;

            g.setColor(new Color(255, 255, 255, 90));
            for (int x = izquierda; x < izquierda + ancho; x += 15) {
                g.drawLine(x, arriba, x, arriba + alto);
            }
            for (int y = arriba; y < arriba + alto; y += 15) {
                g.drawLine(izquierda, y, izquierda + ancho, y);
            }

            g.setColor(Color.WHITE);
            g.fillRect(izquierda, arriba, ancho, grosor);

            int posteX = bocaDerecha ? izquierda + ancho - grosor : izquierda;
            g.fillRect(posteX, arriba, grosor, alto);

            g.setColor(Color.GRAY);
            g.drawRect(izquierda, arriba, ancho, grosor);
            g.drawRect(posteX, arriba, grosor, alto);
        }
    }
}
