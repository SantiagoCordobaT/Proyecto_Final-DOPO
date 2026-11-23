package dominio.partidos;

import dominio.Catalog;
import dominio.excepciones.POOBSoccerException;

/**
 * Catalogo que registra los cuatro tipos de partido disponibles en el orden del
 * menu: Por Goles (3, 5 y 7) y Supervivencia.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class MatchTypeCatalog extends Catalog<MatchType> {

    private static final int[] METAS_GOLES = { 3, 5, 7 };

    /** Inicializa el catalogo con las metas de goles y Supervivencia. */
    public MatchTypeCatalog() {
        super(POOBSoccerException.MODO_INVALIDO);
        for (int meta : METAS_GOLES) {
            agregar(new GoalMatch(meta));
        }
        agregar(new SurvivalMatch());
    }
}
