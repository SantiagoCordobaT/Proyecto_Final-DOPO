package presentation;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabla de textos de la interfaz en cada idioma (Singleton). El idioma es una
 * preferencia de la vista: el dominio no lo conoce. Los nombres que vienen del
 * dominio (modalidades, personajes, paises, poderes y mensajes de error) no se
 * traducen.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Texts {

    private static final String[] LANGUAGES = { "Español", "English" };

    private static Texts instance;

    private final Map<String, String[]> table = new HashMap<String, String[]>();
    private int language;

    /** Crea la tabla en espanol (idioma inicial) e ingles. */
    private Texts() {
        put("menu.play", "Jugar", "Play");
        put("menu.settings", "Configuración", "Settings");
        put("setup.title", "Selecciona el modo de juego", "Choose the game mode");
        put("setup.mode", "Modalidad:", "Mode:");
        put("setup.type", "Tipo de partido:", "Match type:");
        put("common.select", "Seleccionar", "Select");
        put("common.back", "Volver", "Back");
        put("select.title", "Escoge tu personaje", "Choose your character");
        put("select.player1", "Jugador 1", "Player 1");
        put("select.player2", "Jugador 2", "Player 2");
        put("card.country", "País:", "Country:");
        put("card.speed", "Velocidad:", "Speed:");
        put("card.jump", "Salto:", "Jump:");
        put("card.strength", "Fuerza:", "Strength:");
        put("card.power", "Poder Único:", "Unique Power:");
        put("card.special", "Poder especial:", "Special power:");
        put("settings.title", "Configuración", "Settings");
        put("settings.language", "Idioma:", "Language:");
        put("settings.save", "Guardar", "Save");
        put("hud.armed", "¡PODER LISTO!", "POWER READY!");
        put("pause.title", "Pausa", "Pause");
        put("pause.resume", "Reanudar", "Resume");
        put("pause.giveUp", "Abandonar", "Give up");
        put("pause.help", "Cómo jugar", "How to play");
        put("help.controls",
                "Jugador 1: A y D mover, W saltar, S patear, Q poder.\n"
                        + "Jugador 2: flechas ← y → mover, ↑ saltar, ↓ patear, Ctrl derecho poder.\n"
                        + "P o Esc: pausa.\n"
                        + "La barra de poder se llena con el tiempo. Llena, el poder se arma\n"
                        + "y la siguiente patada al balón lo lanza contra el rival.",
                "Player 1: A and D move, W jump, S kick, Q power.\n"
                        + "Player 2: arrows ← and → move, ↑ jump, ↓ kick, right Ctrl power.\n"
                        + "P or Esc: pause.\n"
                        + "The power bar fills over time. When full, the power is armed\n"
                        + "and the next kick of the ball fires it at the rival.");
        put("result.title", "Fin del partido", "Match over");
        put("result.winner", "Ganador:", "Winner:");
        put("result.next", "Siguiente", "Next");
        put("error.title", "Error", "Error");
    }

    /** @return La unica tabla de textos; se crea en la primera llamada. */
    static Texts getInstance() {
        if (instance == null) {
            instance = new Texts();
        }
        return instance;
    }

    /**
     * @param key Clave del texto.
     * @return El texto en el idioma elegido.
     */
    static String text(String key) {
        return getInstance().translate(key);
    }

    /** @return Nombres de los idiomas disponibles, en el orden de sus indices. */
    String[] getLanguages() {
        return LANGUAGES.clone();
    }

    /** @return Indice del idioma elegido. */
    int getLanguage() {
        return language;
    }

    /** @param language Indice del idioma a usar desde ahora (de {@link #getLanguages()}). */
    void setLanguage(int language) {
        this.language = language;
    }

    private String translate(String key) {
        return table.get(key)[language];
    }

    private void put(String key, String spanish, String english) {
        table.put(key, new String[] { spanish, english });
    }
}
