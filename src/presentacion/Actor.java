package presentacion;

import java.awt.Graphics2D;

import dominio.excepciones.POOBSoccerException;

/**
 * Elemento que se actualiza y se dibuja en cada cuadro dentro de la cancha.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
interface Actor {

    void actualizar(double segundos) throws POOBSoccerException;

    void dibujar(Graphics2D g);
}
