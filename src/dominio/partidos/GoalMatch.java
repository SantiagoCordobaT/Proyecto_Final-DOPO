package dominio.partidos;

/**
 * Reglas del partido por goles, donde gana el primero en llegar a la meta (3, 5 o 7).
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
    public void golAnotado(int anotador, int golesAnotador) {
        if (golesAnotador >= metaGoles) {
            finalizar(anotador);
        }
    }

    @Override
    public String obtenerTextoEstado() {
        int segundos = (int) (obtenerSegundosTranscurridos() + TOLERANCIA);
        return "Meta: " + metaGoles + " goles | " + formatoReloj(segundos);
    }
}
