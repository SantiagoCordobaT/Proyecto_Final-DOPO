package presentation;

import java.awt.Graphics2D;

import dominio.excepciones.POOBSoccerException;

/**
 * Elemento animado de la cancha. En cada cuadro primero lee de la fachada lo que
 * necesita y avanza su animacion, y despues se pinta con ese estado; asi el
 * pintado nunca consulta al dominio.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
interface Actor {

    /**
     * Lee el estado del partido y avanza la animacion.
     *
     * @param seconds Segundos reales transcurridos desde el cuadro anterior.
     * @throws POOBSoccerException Si no hay un partido iniciado.
     */
    void update(double seconds) throws POOBSoccerException;

    /**
     * Pinta el actor en coordenadas de la cancha.
     *
     * @param g Lienzo del cuadro actual.
     */
    void paint(Graphics2D g);
}
