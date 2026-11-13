package dominio.partidos;

import dominio.excepciones.POOBSoccerException;

/**
 * Lo que un tipo de partido necesita del partido en curso (Protected Variations):
 * leer el marcador y pedir un nuevo rival. Asi {@link MatchType} no conoce a los
 * personajes ni a la simulacion.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public interface MatchContext {

    /**
     * @param player Jugador (1 o 2).
     * @return Goles del jugador.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    int getGoals(int player) throws POOBSoccerException;

    /**
     * Reemplaza al rival (jugador 2) por el siguiente del catalogo, listo para jugar.
     *
     * @throws POOBSoccerException Si el catalogo no pudiera entregar el rival.
     */
    void replaceRival() throws POOBSoccerException;
}
