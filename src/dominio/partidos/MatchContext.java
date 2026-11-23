package dominio.partidos;

import dominio.excepciones.POOBSoccerException;

/**
 * Operaciones minimas que un tipo de partido necesita pedirle a la partida en
 * curso (consultar goles y cambiar de rival). Asi las reglas de partido no
 * dependen de la fisica, el balon ni la cancha.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public interface MatchContext {

    /**
     * Consulta los goles actuales de un jugador.
     *
     * @param jugador Numero del jugador (1 o 2).
     * @return Goles anotados por ese jugador en la partida actual.
     * @throws POOBSoccerException Si el numero de jugador no es 1 ni 2.
     */
    int obtenerGoles(int jugador) throws POOBSoccerException;

    /**
     * Cambia al rival (jugador 2) por el siguiente del catalogo, listo para
     * continuar la serie en modo Supervivencia.
     *
     * @throws POOBSoccerException Si el catalogo no puede entregar el nuevo rival.
     */
    void reemplazarRival() throws POOBSoccerException;
}
