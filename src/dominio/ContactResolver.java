package dominio;

import dominio.personajes.Character;

/**
 * Resuelve los choques y patadas entre ambos jugadores y el balón, recordando
 * quién pateó de último para detectar balonazos fuertes al rival.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class ContactResolver {

    private Character ultimoPateador;

    void resolver(Ball balon, Character izquierdo, Character derecho) {
        atenderJugador(balon, izquierdo, derecho);
        atenderJugador(balon, derecho, izquierdo);
    }

    void reiniciar() {
        ultimoPateador = null;
    }

    private void atenderJugador(Ball balon, Character jugador, Character rival) {
        if (jugador.intentarPatear(balon, rival)) {
            ultimoPateador = jugador;
            return;
        }
        if (!jugador.toca(balon)) {
            return;
        }
        if (jugador != ultimoPateador && balon.esGolpeFuerte()) {
            jugador.aturdir();
            balon.frenar();
        }
        jugador.empujar(balon);
    }
}
