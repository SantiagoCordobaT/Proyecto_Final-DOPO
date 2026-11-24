package dominio.partidos;

import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;

/**
 * Reglas del partido por goles: gana el primero en alcanzar la meta (3, 5 o 7).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class GoalMatch extends MatchType {

    private final int metaGoles;

    GoalMatch(int metaGoles) {
        super("Por Goles (" + metaGoles + ")");
        this.metaGoles = metaGoles;
    }

    @Override
    public Character registrarGol(int anotador, int golesAnotador, Character rivalActual,
            CharacterCatalog catalogo) {
        if (golesAnotador >= metaGoles) {
            finalizar(anotador);
        }
        return null;
    }

    @Override
    public String obtenerTextoEstado() {
        int segundos = (int) (obtenerSegundosTranscurridos() + TOLERANCIA);
        return "Meta: " + metaGoles + " goles | " + formatoReloj(segundos);
    }
}
