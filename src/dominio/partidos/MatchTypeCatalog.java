package dominio.partidos;

import dominio.excepciones.POOBSoccerException;

/**
 * Lista de tipos de partido disponibles en el orden del menú:
 * Por Goles (3, 5 y 7) y Supervivencia.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class MatchTypeCatalog {

    private final MatchType[] tipos = {
            new GoalMatch(3),
            new GoalMatch(5),
            new GoalMatch(7),
            new SurvivalMatch()
    };

    public String[] obtenerNombres() {
        String[] nombres = new String[tipos.length];
        for (int i = 0; i < tipos.length; i++) {
            nombres[i] = tipos[i].obtenerNombre();
        }
        return nombres;
    }

    public MatchType obtener(int indice) throws POOBSoccerException {
        if (indice < 0 || indice >= tipos.length) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        return tipos[indice];
    }
}
