package dominio.partidos;

import dominio.Catalog;
import dominio.excepciones.POOBSoccerException;

/**
 * Catalogo de tipos de partido (Creator): Por Tiempo, Por Goles (3, 5 y 7) y
 * Supervivencia, en ese orden. Es el unico que conoce las metas de goles ofrecidas.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public final class MatchTypeCatalog extends Catalog<MatchType> {

    private static final int[] GOAL_TARGETS = { 3, 5, 7 };

    /** Crea el catalogo con los cinco tipos de partido. */
    public MatchTypeCatalog() {
        super(POOBSoccerException.MODO_INVALIDO);
        add(new TimeMatch());
        for (int target : GOAL_TARGETS) {
            add(new GoalMatch(target));
        }
        add(new SurvivalMatch());
    }
}
