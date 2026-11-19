package dominio.modalidades;

import dominio.Catalog;
import dominio.excepciones.POOBSoccerException;

/**
 * Catalogo de modalidades de juego (Creator): Jugador vs Jugador, Jugador vs
 * Maquina y Maquina vs Maquina, en ese orden.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public final class GameModeCatalog extends Catalog<GameMode> {

    /** Crea el catalogo con las tres modalidades. */
    public GameModeCatalog() {
        super(POOBSoccerException.MODO_INVALIDO);
        add(new PvsP());
        add(new PvsM());
        add(new MvsM());
    }
}
