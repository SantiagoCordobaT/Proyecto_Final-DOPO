package presentation;

import java.awt.Graphics2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Parte de la pantalla de juego que se dibuja a partir del estado de la fachada.
 * La pantalla dibuja su lista de partes sin saber cual es cada una (Polymorphism,
 * Protected Variations): agregar un elemento visual no cambia el panel.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
interface Drawable {

    /**
     * Dibuja la parte con el estado actual del partido.
     *
     * @param g Lienzo donde dibujar.
     * @param game Fachada que da el estado del partido.
     * @throws POOBSoccerException Si no hay partido en curso.
     */
    void draw(Graphics2D g, HeadSoccer game) throws POOBSoccerException;
}
